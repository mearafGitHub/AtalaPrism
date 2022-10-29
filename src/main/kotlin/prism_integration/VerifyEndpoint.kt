package prism_integration

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import io.iohk.atala.prism.identity.*
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import java.util.*

class VerifyEndpoint{
    private val holders:HashMap<String, String> = hashMapOf(
        "Blein Mesfin" to
                "did:prism:c2a4c9eebe9fdc57d472faddec21e82b2f1de6cf44094d3c1f9ef990bb7b8b4c:Cj8KPRI7CgdtYXN0ZXIwEAFKLgoJc2VjcDI1NmsxEiEC1v6aCe35bTymidbmuQDnncj1eormA9Xn7x3IISaxqVs"
        ,
        "Eba Ayana" to
                "did:prism:2a1c76a9dcc6f92ff6cb0143857de561b61f2a32beb3e5b856a22c9343579069:Cj8KPRI7CgdtYXN0ZXIwEAFKLgoJc2VjcDI1NmsxEiEC_KK7moccRT4D5wkoqy25DVTrfWk8LBjc9XbYrmvFSJ8"
        ,
        "Daenarys Shelmazmel Targaeryan" to
                "did:prism:abe35abbbdbd639dbff0e4420136667a93c7eb578290f7c2ab5cb7f683fac56e:Cj8KPRI7CgdtYXN0ZXIwEAFKLgoJc2VjcDI1NmsxEiECQPBaK2b4q85523VohJIsah18VLkcut308UoWRPpP5Nc"
        ,
        "Mearaf Tadewos" to
                "did:prism:0d3ed77d9652334af0343958c00a7e0db6ac3b21366d37d6d9d9eb4deb21feb2:Cj8KPRI7CgdtYXN0ZXIwEAFKLgoJc2VjcDI1NmsxEiEDiLEXAW4WBzd_SLbiM9rfPflOo5kqnYUQI7op2ktAPIs"
    )

    companion object {
        fun verifier(holderSignedCredential: String, userName: String, education: HashMap<String, String>): String {

            val organizations:HashMap<String, String> = hashMapOf(
                "did:prism:297506b34a0572ac615e04ea440d34c73e2948df491d50ebe1f8ba1d8d13f065" to "Addis Ababa University",
                "did:prism:4d5257d64a4dab5c69e3b97668d4df0b022966b35242699695735f8d53c5b07a" to "Hawasa University",
                "did:prism:5567fe8833a9f88f116169df1035fa32d236537b3c1a004c559f73b333b1c4f8" to "John Snow",
                "did:prism:9fe2b88c280a0159a2c4d7e7e74f0cf96f2af976adf9a03bcbb5db02c71f8dbe"  to "Jimma University",
                "did:prism:855ade0b7ffded0f9950aff5faa560b47b2e90ef55cd5791c09abf5e2e949196" to "Bahir Dar University"
            )
            // todo: split 'holderSignedCredential' at (.)
            val decoder: Base64.Decoder = Base64.getDecoder()
            val content = String(decoder.decode(holderSignedCredential))
            println("Credential content: $content")
            println(content::class.java.typeName)

            var map: Map<String, Any> = HashMap()
            var contentHashMmap = Gson().fromJson(content, map.javaClass)
            println()
            println(contentHashMmap)
            println()

            var subject: LinkedTreeMap<String, Any> = contentHashMmap.get("credentialSubject") as LinkedTreeMap<String, Any>
            println("Subject:  $subject")

            // School name vs value in 'organizations' ^ return INVALID CREDENTIAL - Wrong Issuer if false
            if ( education.get("school") != organizations.get(contentHashMmap.get("id")) ){
                println()
                val temp =organizations.get(contentHashMmap.get("id"))
                println("School: $education[1]   $temp")
                return  "INVALID CREDENTIAL - Wrong Issuer"
                }
            // Else holder name vs username in 'userName' ^ return INVALID CREDENTIAL - Not owner if false
            else if (userName != subject.get("name")) {
                return "INVALID CREDENTIAL - Not owner."
            }
            // Else certificate vs field_of_study ^ return INVALID CREDENTIAL - Wrong Field of study
            else if (subject.get("certificate") != "Certificate of "+ education.get("study")){
                val temp = "Certificate of "+ education.get("study")
                val temp2 = subject.get("certificate")
                println("stydy: $temp2   $temp")
                return "INVALID CREDENTIAL - Wrong Field of study"
            }
            // Else ^ return { VERIFIED OR VALID CREDENTIAL }

            return "VALID"
        }
    }

}

@Controller("/") // accessed via the link http://localhost:8080/api/verify
class VerifyService {

    @Get("/")
    fun index(): String = "\n\n\n\n\n\n\n\n\n\n\n\n" +
            "\t\t\t\t\t\t\t Hello    Hola    Bonjur    Merehaba ...  " +
            "\n\n\t\t\t\t\t\t\t This is Prism, where you can get verification for your Credential." +
            "\n\t\t\t\t\t\t\t Please use the link http://localhost:8080/api/verify" +
            "\n\n\t\t\t\t\t\t\t Thank you    Gracias    Merci    Sağol ..."

    @Post("/api/verify")
    fun verify(holderSignedCredentialDID:String, userName:String, education:HashMap<String,String>):String{
        var result = VerifyEndpoint.verifier(holderSignedCredentialDID, userName, education)
        println("Verification Result: " + result )
        return result
    }
    // todo: cloud host
    // todo: Frontend integration test
}