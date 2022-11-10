package PrismIntegration


import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import io.iohk.atala.prism.api.VerificationResult
import io.iohk.atala.prism.api.node.NodeAuthApiImpl
import io.iohk.atala.prism.credentials.PrismCredential
import io.iohk.atala.prism.credentials.content.CredentialContent
import io.iohk.atala.prism.credentials.json.JsonBasedCredential
import io.iohk.atala.prism.crypto.*
import io.iohk.atala.prism.crypto.keys.ECPrivateKey
import io.iohk.atala.prism.crypto.signature.ECSignature
import io.iohk.atala.prism.identity.*
import io.iohk.atala.prism.protos.GrpcOptions
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse.ok
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.Serializable
import java.util.*
import kotlin.Error


class myPrismCredential(
    override val canonicalForm: String,
    override val content: CredentialContent,
    override val contentBytes: ByteArray,
    override val signature: ECSignature?
) : PrismCredential(){
    override fun sign(privateKey: ECPrivateKey): PrismCredential {
        TODO("Not yet implemented")
    }

}

class GrpcConfig {
    companion object {
        var protocol: String = System.getenv("PRISM_NODE_PROTOCOL") ?: "http"
        var host: String = System.getenv("PRISM_NODE_HOST") ?: "ppp-vasil.atalaprism.io"
        var port: String = System.getenv("PRISM_NODE_PORT") ?: "5053"
        var token: String? = System.getenv("PRISM_NODE_TOKEN") ?: null
        fun options(): GrpcOptions {
            println("Connecting to $protocol://$host:$port")
            return GrpcOptions(protocol, host, port.toInt(), token)
        }
    }
}
class VerifyEndpoint{

    companion object {
        fun prismVerify(nodeAuthApi: NodeAuthApiImpl,
                        holderSignedCredential: PrismCredential,
                        holderCredentialMerkleProof: MerkleInclusionProof
        ):Boolean {
            println("""Received Holder full signed credential: ${holderSignedCredential} """ )
            println("Verifier is Verifying received credential using single convenience method")

            val credentialVerificationServiceResult = runBlocking {
                nodeAuthApi.verify(
                    signedCredential = holderSignedCredential,
                    merkleInclusionProof = holderCredentialMerkleProof
                )
            }
            var res = require(credentialVerificationServiceResult.verificationErrors.isEmpty()) {
                "VerificationErrors should be empty: YOU SHOULD NOT RECEIVE THIS MESSAGE IF VERIFICATION WERE SUCCESSFUL."
            }
            println(""" the verification result:  $res""")
            return true
        }

        fun prismCredential_maker(contentBytes:ByteArray,
                                  content:CredentialContent,
                                  signature: ECSignature,
                                  canonicalForm: String):List<Any>
        {
            var holderSignedCredential: PrismCredential = myPrismCredential(canonicalForm, content, contentBytes, signature)

            // making the holderCredentialMerkleProof
            var hash :Hash = Sha256Digest.fromBytes(contentBytes)
            var siblings:List<Hash> = listOf()
            var index: Index = 0
            var holderCredentialMerkleProof : MerkleInclusionProof = MerkleInclusionProof(hash,index,siblings)

            var credentialAndProfList:List<Any> = listOfNotNull(holderSignedCredential, holderCredentialMerkleProof)
            return credentialAndProfList
        }

        fun fairwayVerify(credContentMap: Map<String, Any>, userName: String, education: HashMap<String, String>): String {
            // println("fairwayVerify() debug")
            println("credential map:  $credContentMap")
            println("Education array: $education")
            println("userName: $userName")
            var gson = Gson()
            var message = mutableMapOf<String, Any>(
                "flag" to false,
                "message" to ""
            )
            val organizations:HashMap<String, String> = hashMapOf(
                "did:prism:297506b34a0572ac615e04ea440d34c73e2948df491d50ebe1f8ba1d8d13f065" to "Addis Ababa University",
                "did:prism:4d5257d64a4dab5c69e3b97668d4df0b022966b35242699695735f8d53c5b07a" to "Hawasa University",
                "did:prism:9fe2b88c280a0159a2c4d7e7e74f0cf96f2af976adf9a03bcbb5db02c71f8dbe"  to "Jimma University",
                "did:prism:855ade0b7ffded0f9950aff5faa560b47b2e90ef55cd5791c09abf5e2e949196" to "Bahir Dar University",
                "did:prism:91127e3c92cf916eb037b3d15e0d206d973fb3353b53a2970a2c19fc90caa585" to "Hamburg University"
            )

            var subject: LinkedTreeMap<String, Any> = credContentMap.get("credentialSubject") as LinkedTreeMap<String, Any>
            println("Subject:  $subject")

            // School name vs value in 'organizations' ^ return - Wrong Issuer if false
            if ( education.get("school") != organizations.get(credContentMap.get("id")) ){
                message["message"] = "Wrong Issuer."
                var jsonMessage = gson.toJson(message)
                return  jsonMessage
            }
            // Holder name vs username in 'userName' ^ return - Not owner if false
            else if (userName != subject.get("name")) {
                message["message"] = "This User is Not the Owner of the Credential."
                var jsonMessage = gson.toJson(message)
                return  jsonMessage
            }
            // Certificate vs field_of_study ^ return - Wrong Field of study
            else if (subject.get("certificate") != "Certificate of "+ education.get("study")){
                message["message"] = "Wrong Field Of Study."
                var jsonMessage = gson.toJson(message)
                return  jsonMessage
            }
            // Else ^ return ture
            message["message"] = "Valid Credential."
            message["flag"] = true
            var jsonMessage = gson.toJson(message)
            return  jsonMessage
        }

        fun verifier(encodedSignedCredential: String, userName: String, education: HashMap<String, String>): String  {
            var message = mutableMapOf<String, Any>(
                "flag" to false,
                "message" to ""
            )
            var gson = Gson()
            if (!encodedSignedCredential.contains(".", ignoreCase = true)){
                message["message"] = "Invalid Credential DID."
                message["flag"] = false
                var jsonMessage = gson.toJson(message)
                return  jsonMessage
            }
            if (encodedSignedCredential.length != 623){
                message["message"] = "Invalid Credential DID."
                message["flag"] = false
                var jsonMessage = gson.toJson(message)
                return  jsonMessage
            }
            val encodedSignedCredentialArray = encodedSignedCredential.split(".").toTypedArray()
            val holderSignedCredentialHash_contentBytes = encodedSignedCredentialArray[0]

            val decoder: Base64.Decoder = Base64.getDecoder()
            val credContent = String(decoder.decode(holderSignedCredentialHash_contentBytes))
            var map: Map<String, Any> = HashMap()
            var credContentMap = Gson().fromJson(credContent, map.javaClass)

            return  fairwayVerify(credContentMap, userName, education)
        }

        private fun VerificationResult.toMessageArray(): List<String> {
            val messages = mutableListOf<String>()
            for (message in this.verificationErrors) {
                messages.add(message.errorMessage)
            }
            return messages
        }

        fun myPrismVerify(credential: HashMap<String, Serializable>):List<String>{
            val nodeAuthApi = NodeAuthApiImpl(GrpcConfig.options())
            val signed = JsonBasedCredential.fromString(credential.get("encodedSignedCredential") as String)
            // Use encodeDefaults to generate empty siblings field on proof
            val format = Json { encodeDefaults = true }
            val gson = Gson();
            val proofData = gson.toJson(credential.get("proof"))
            val proof = MerkleInclusionProof.decode(proofData)
            return runBlocking {nodeAuthApi.verify(signed, proof).toMessageArray()}
        }

        fun fromJsonToString(data:HashMap<String, Any>):String{
            var return_string = ""
            for ((key, value) in data) {
                return_string += key + ":" + value + ","
            }
            return return_string.removeSuffix(",")
        }

    }

}


private typealias Hash = Sha256Digest
private typealias Index = Int

@Controller("/")
class VerifyService {
    @Get("/")
    fun index(): MutableHttpResponse<String>?{
        return ok("\n\n\n\n\n\n\n\n\n\n\n\n" +
        "\t\t\t\t\t\t\t Hello    Hola    Bonjur    Merehaba ...  " +
        "\n\n\t\t\t\t\t\t\t This is Prism, where you can get verification for your Credential." +
        "\n\t\t\t\t\t\t\t Please use the link http://localhost:8080/api/verify" +
        "\n\n\t\t\t\t\t\t\t Thank you    Gracias    Merci    Sağol ..."+
        "\n\n\n\n" +
        "\n" +
        "\n"
        )
    }

    @Post("/api/verify")
    fun verify(holderSignedCredentialDID:String, userName:String, education:HashMap<String,String>):
            MutableHttpResponse<Any>? {
        var result = VerifyEndpoint.verifier(holderSignedCredentialDID, userName, education)
        println("Verification Result: " + result )
        return ok(result)
    }

    @Post("/api/prism_verify")
    fun verify(credentialData:HashMap<String,Serializable>): MutableHttpResponse<List<String>> {
        println("Received Data")
        println(credentialData)
        var result = VerifyEndpoint.myPrismVerify(credentialData)
        println("Verification Result: " + result )
        return ok(result)
    }

}

@Controller("/")
class OptionsController {
    @Options("{/path:.*}")
    fun handleOptions(@Nullable @PathVariable path: String?) {
    }
}