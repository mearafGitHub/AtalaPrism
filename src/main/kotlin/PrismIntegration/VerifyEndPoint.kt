package PrismIntegration


import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import io.iohk.atala.prism.api.VerificationResult
import io.iohk.atala.prism.api.node.NodeAuthApiImpl
import io.iohk.atala.prism.credentials.json.JsonBasedCredential
import io.iohk.atala.prism.crypto.*
import io.iohk.atala.prism.identity.*
import io.iohk.atala.prism.protos.GrpcOptions
import io.micronaut.http.HttpResponse.ok
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.*

class GrpcConfig {
    companion object {
        var protocol: String = System.getenv("PRISM_NODE_PROTOCOL") ?: "http"
        var host: String = System.getenv("PRISM_NODE_HOST") ?: "ppp-vasil.atalaprism.io"
        var port: String = System.getenv("PRISM_NODE_PORT") ?: "50053"
        var token: String? = System.getenv("PRISM_SDK_PASSWORD") ?: null
        fun options(): GrpcOptions {
            println("Connecting to $protocol://$host:$port")
            return GrpcOptions(protocol, host, port.toInt(), token)
        }
    }
}
class VerifyEndpoint{

    companion object {
        fun fairwayVerify(
            credContentMap: Map<String, Any>,
            userName: String,
            education: HashMap<String, String>,
            message: MutableMap<String, Any>
        ): MutableMap<String, Any> {
            // println("fairwayVerify() debug")
            println("credential map:  $credContentMap")
            println("Education array: $education")
            println("userName: $userName")
            val organizations:HashMap<String, String> = hashMapOf(
                "did:prism:297506b34a0572ac615e04ea440d34c73e2948df491d50ebe1f8ba1d8d13f065" to "Addis Ababa University",
                "did:prism:4d5257d64a4dab5c69e3b97668d4df0b022966b35242699695735f8d53c5b07a" to "Hawasa University",
                "did:prism:9fe2b88c280a0159a2c4d7e7e74f0cf96f2af976adf9a03bcbb5db02c71f8dbe"  to "Jimma University",
                "did:prism:855ade0b7ffded0f9950aff5faa560b47b2e90ef55cd5791c09abf5e2e949196" to "Bahir Dar University",
                "did:prism:91127e3c92cf916eb037b3d15e0d206d973fb3353b53a2970a2c19fc90caa585" to "Hamburg University",
                "did:prism:98d810a11389049a70240555c826bf2b47af85da213c054872bd0d88993dd20d" to "Atse Minilik High School"
            )

            val subject: LinkedTreeMap<String, Any> = credContentMap["credentialSubject"] as LinkedTreeMap<String, Any>
            println("Subject:  $subject")

            // School name vs value in 'organizations' ^ return - Wrong Issuer if false
            if ( education["school"] != organizations[credContentMap["id"]] ){
                message["message"] = "Wrong Issuer."
                return  message
            }
            // Holder name vs username in 'userName' ^ return - Not owner if false
            else if (userName != subject["name"]) {
                message["message"] = "This User is Not the Owner of the Credential."
                return  message
            }
            // Certificate vs field_of_study ^ return - Wrong Field of study
            else if (subject["certificate"] != "Certificate of "+ education["study"]){
                message["message"] = "Wrong Field Of Study."
                return  message
            }
            // Else ^ return ture
            message["message"] = "Valid Credential."
            message["flag"] = true
            return  message
        }

        fun prismVerifier(credential: HashMap<String, Any>, userName: String, education: HashMap<String, String>): String  {
            println("Received Data at prism_verifier: ")
            println(credential.get("encodedSignedCredential"))
            val encodedSignedCredential = credential["encodedSignedCredential"] as String
            var message = mutableMapOf<String, Any>(
                "flag" to false,
                "message" to "",
                "prism_message" to listOf<String>()
            )
            val gson = Gson()
            if (!encodedSignedCredential.contains(".", ignoreCase = true)){
                message["message"] = "Invalid Credential DID."
                message["flag"] = false
                return  gson.toJson(message)
            }
            if (encodedSignedCredential.length != 623){
                message["message"] = "Invalid Credential DID."
                message["flag"] = false
                return  gson.toJson(message)
            }
            val encodedSignedCredentialArray = encodedSignedCredential.split(".").toTypedArray()
            val holderSignedCredentialHashContentBytes = encodedSignedCredentialArray[0]

            val decoder: Base64.Decoder = Base64.getDecoder()
            val credContent = String(decoder.decode(holderSignedCredentialHashContentBytes))
            val map: Map<String, Any> = HashMap()
            val credContentMap = gson.fromJson(credContent, map.javaClass)

            val res = fairwayVerify(credContentMap, userName, education, message)
            val flag = res["flag"] as Boolean
            if (flag){
                val verficationRes = myPrismVerify(credential)
                message["prism_message"] = verficationRes
                val prismMsgOne = verficationRes[0]
                val prismMsgTwo = verficationRes[1]

                if(prismMsgOne.contains("not found", ignoreCase = true) or prismMsgTwo.contains("Invalid", ignoreCase = true)){
                    message["flag"] = false
                    message["message"] = "Invalid Credential"
                }
                return gson.toJson(message)
            }
            return gson.toJson(message)
        }
        fun verifier(encodedSignedCredential: String, userName: String, education: HashMap<String, String>): String  {
            var message = mutableMapOf<String, Any>(
                "flag" to false,
                "message" to ""
            )
            val gson = Gson()
            if (!encodedSignedCredential.contains(".", ignoreCase = true)){
                message["message"] = "Invalid Credential DID."
                message["flag"] = false
                return  gson.toJson(message)
            }
            if (encodedSignedCredential.length < 600){
                message["message"] = "Invalid Credential DID."
                message["flag"] = false
                return  gson.toJson(message)
            }
            val encodedSignedCredentialArray = encodedSignedCredential.split(".").toTypedArray()
            val holderSignedCredentialHashContentBytes = encodedSignedCredentialArray[0]

            val decoder: Base64.Decoder = Base64.getDecoder()
            val credContent = String(decoder.decode(holderSignedCredentialHashContentBytes))
            val map: Map<String, Any> = HashMap()
            val credContentMap = Gson().fromJson(credContent, map.javaClass)

            val res = fairwayVerify(credContentMap, userName, education, message)
            return gson.toJson(res)
        }

        private fun VerificationResult.toMessageArray(): List<String> {
            val messages = mutableListOf<String>()
            for (message in this.verificationErrors) {
                messages.add(message.errorMessage)
            }
            return messages
        }

        fun myPrismVerify(credential: HashMap<String, Any>):List<String>{
            val nodeAuthApi = NodeAuthApiImpl(GrpcConfig.options())
            val signed = JsonBasedCredential.fromString(credential["encodedSignedCredential"] as String)
            // Use encodeDefaults to generate empty siblings field on proof
            val gson = Gson()
            val proofData = gson.toJson(credential["proof"])
            val proof = MerkleInclusionProof.decode(proofData)
            return runBlocking {nodeAuthApi.verify(signed, proof).toMessageArray()}
        }

    }

}

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

    @Post("/api/fairway_verify")
    fun verify(holderSignedCredentialDID:String, userName:String, education:HashMap<String,String>):
            MutableHttpResponse<Any>? {
        val result = VerifyEndpoint.verifier(holderSignedCredentialDID, userName, education)
        println("Verification Result: " + result )
        return ok(result)
    }

    @Post("/api/verify")
    fun prismVerify(holderSignedCredentialDID:HashMap<String,Any>, userName:String, education:HashMap<String,String>):
            MutableHttpResponse<String>? {
        val result = VerifyEndpoint.prismVerifier(holderSignedCredentialDID, userName, education)
        println("Verification Result: " + result )
        return ok(result)
    }

}
