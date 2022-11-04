package prism_integration

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import io.iohk.atala.prism.api.node.NodeAuthApiImpl
import io.iohk.atala.prism.credentials.PrismCredential
import io.iohk.atala.prism.credentials.content.CredentialContent
import io.iohk.atala.prism.crypto.*
import io.iohk.atala.prism.crypto.keys.ECPrivateKey
import io.iohk.atala.prism.crypto.signature.ECSignature
import io.iohk.atala.prism.identity.*
import io.micronaut.http.HttpResponse.ok
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import kotlinx.coroutines.runBlocking
import java.util.*

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
            // todo: if the result can be comparable then use conditional statement to return T/F
            return true
        }

        fun prismCredential_maker(contentBytes:ByteArray,
                                  content:CredentialContent,
                                  signature: ECSignature,
                                  canonicalForm: String):List<Any>
        {
            var holderSignedCredential: PrismCredential = myPrismCredential(canonicalForm, content, contentBytes, signature)

            // making the holderCredentialMerkleProof
            var hash :Hash = Sha256Digest.fromBytes(contentBytes) // todo: has ERROR: The given byte array does not correspond to a SHA256 hash. It must have exactly 32 bytes
            var siblings:List<Hash> = listOf()
            var index: Index = 0
            var holderCredentialMerkleProof : MerkleInclusionProof = MerkleInclusionProof(hash,index,siblings)

            var credentialAndProfList:List<Any> = listOfNotNull(holderSignedCredential, holderCredentialMerkleProof)
            return credentialAndProfList
        }

        fun fairwayVerify(credContentMap: Map<String, Any>, userName: String, education: HashMap<String, String>): Any {
            // println("fairwayVerify() debug")
            println("credential map:  $credContentMap")
            println("Education array: $education")
            println("userName: $userName")
            var gson = Gson()

            var errorMsg = mutableMapOf<String, Any>(
                "flag" to false,
                "message" to ""
            )

            val organizations:HashMap<String, String> = hashMapOf(
                "did:prism:297506b34a0572ac615e04ea440d34c73e2948df491d50ebe1f8ba1d8d13f065" to "Addis Ababa University",
                "did:prism:4d5257d64a4dab5c69e3b97668d4df0b022966b35242699695735f8d53c5b07a" to "Hawasa University",
                "did:prism:5567fe8833a9f88f116169df1035fa32d236537b3c1a004c559f73b333b1c4f8" to "John Snow",
                "did:prism:9fe2b88c280a0159a2c4d7e7e74f0cf96f2af976adf9a03bcbb5db02c71f8dbe"  to "Jimma University",
                "did:prism:855ade0b7ffded0f9950aff5faa560b47b2e90ef55cd5791c09abf5e2e949196" to "Bahir Dar University"
            )

            var subject: LinkedTreeMap<String, Any> = credContentMap.get("credentialSubject") as LinkedTreeMap<String, Any>
            println("Subject:  $subject")

            // School name vs value in 'organizations' ^ return - Wrong Issuer if false
            if ( education.get("school") != organizations.get(credContentMap.get("id")) ){
                errorMsg["message"] = "Wrong Issuer."
                var jsonErrorMsg = gson.toJson(errorMsg)
                return  jsonErrorMsg
            }
            // Holder name vs username in 'userName' ^ return - Not owner if false
            else if (userName != subject.get("name")) {
                errorMsg["message"] = "This User is Not the Owner of the Credential."
                var jsonErrorMsg = gson.toJson(errorMsg)
                return  jsonErrorMsg
            }
            // Certificate vs field_of_study ^ return - Wrong Field of study
            else if (subject.get("certificate") != "Certificate of "+ education.get("study")){
                errorMsg["message"] = "Wrong Field Of Study."
                var jsonErrorMsg = gson.toJson(errorMsg)
                return  jsonErrorMsg
            }
            // Else ^ return ture
            errorMsg["message"] = "Valid Credential."
            errorMsg["flag"] = true
            var jsonErrorMsg = gson.toJson(errorMsg)
            return  jsonErrorMsg
            return jsonErrorMsg
        }

        fun verifier(encodedSignedCredential: String, userName: String, education: HashMap<String, String>): Any {

            val encodedSignedCredentialArray = encodedSignedCredential.split(".").toTypedArray()
            val holderSignedCredentialHash_contentBytes = encodedSignedCredentialArray[0]
            val someThing = encodedSignedCredentialArray[1]

            val decoder: Base64.Decoder = Base64.getDecoder()
            val credContent = String(decoder.decode(holderSignedCredentialHash_contentBytes))
            var map: Map<String, Any> = HashMap()
            var credContentMap = Gson().fromJson(credContent, map.javaClass)

            // var credContentJson: JsonObject = JsonObject(credContentMap as Map<String, JsonElement>)
            // var contentBytes:ByteArray = credContent.toByteArray()
            // var content:CredentialContent = CredentialContent(credContentJson)
            // var signature: ECSignature = ECSignature(data = contentBytes)
            // var canonicalForm: String = credContentMap.get("id") as String
            // var credAndProof = prismCredential_maker(contentBytes,content,signature,canonicalForm)

            // var credMerkleProof = credAndProof[1] as MerkleInclusionProof
            // var signedCred = credAndProof[0] as PrismCredential

            // makes the NodeAuthApiImpl instance
            // val environment = "ppp-vasil.atalaprism.io"
            // val nodeAuthApi = NodeAuthApiImpl(GrpcOptions("http", environment, 50053))
            // if (prismVerify(nodeAuthApi, signedCred, credMerkleProof)){
                // return fairwayVerify(credContentMap, userName, education)
            // }

            return  fairwayVerify(credContentMap, userName, education) // "Testing..."
        }
    }

}

private typealias Hash = Sha256Digest
private typealias Index = Int

@Controller("/") // accessed via the link http://localhost:8080/api/verify
class VerifyService {
    @Get("/")
    fun index(): MutableHttpResponse<String>? = ok("\n\n\n\n\n\n\n\n\n\n\n\n" +
            "\t\t\t\t\t\t\t Hello    Hola    Bonjur    Merehaba ...  " +
            "\n\n\t\t\t\t\t\t\t This is Prism, where you can get verification for your Credential." +
            "\n\t\t\t\t\t\t\t Please use the link http://localhost:8080/api/verify" +
            "\n\n\t\t\t\t\t\t\t Thank you    Gracias    Merci    Sağol ..."+
            "\n\n\n\n" +
            "\n" +
            "\n"
    )

    @Post("/api/verify")
    fun verify(holderSignedCredentialDID:String, userName:String, education:HashMap<String,String>): MutableHttpResponse<Any>? {
        var result = VerifyEndpoint.verifier(holderSignedCredentialDID, userName, education)
        println("Verification Result: " + result )
        return ok(result)
    }
    // todo: dockerize
    // todo: Frontend integration test
}