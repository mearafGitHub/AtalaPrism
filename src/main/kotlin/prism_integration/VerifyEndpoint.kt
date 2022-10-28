package prism_integration

import io.iohk.atala.prism.api.node.NodeAuthApiImpl
import io.iohk.atala.prism.credentials.PrismCredential
import io.iohk.atala.prism.crypto.MerkleInclusionProof
import io.iohk.atala.prism.identity.*
import io.iohk.atala.prism.protos.GrpcOptions
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import kotlinx.coroutines.runBlocking


class VerifyEndpoint{

    val holders = mapOf(
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

    val organizations = mapOf(
        "did:prism:297506b34a0572ac615e04ea440d34c73e2948df491d50ebe1f8ba1d8d13f065" to "Addis Ababa University",
        "did:prism:4d5257d64a4dab5c69e3b97668d4df0b022966b35242699695735f8d53c5b07a" to "Hawasa University",
        "did:prism:5567fe8833a9f88f116169df1035fa32d236537b3c1a004c559f73b333b1c4f8" to "John Snow",
        "did:prism:9fe2b88c280a0159a2c4d7e7e74f0cf96f2af976adf9a03bcbb5db02c71f8dbe"  to "Jimma University"
    )

    private fun verifier(nodeAuthApi: NodeAuthApiImpl,
                         holderSignedCredential: PrismCredential,
                         holderCredentialMerkleProof: MerkleInclusionProof){
        // Verifier, who owns credentialClam, can easily verify the validity of the credentials.
        println("Verifier is Verifying the received credential using single convenience method")

        val credentialVerificationServiceResult = runBlocking {
            nodeAuthApi.verify(
                signedCredential = holderSignedCredential,
                merkleInclusionProof = holderCredentialMerkleProof
            )
        }
        require(credentialVerificationServiceResult.verificationErrors.isEmpty()) {
            "VerificationErrors should be empty: YOU SHOULD NOT RECEIVE THIS MESSAGE IF VERIFICATION WERE SUCCESSFUL."
        }
    }

    fun main(cred_did:PrismDid) {
        println("Fairway Prism Integrated!")
        val environment = "ppp-vasil.atalaprism.io"
        val nodeAuthApi = NodeAuthApiImpl(GrpcOptions("http", environment, 50053))
        // verifier(nodeAuthApi, holderSignedCredential as PrismCredential, holderCredentialMerkleProof as MerkleInclusionProof)
    }

}


@Controller("/api") // accessed via the link http://localhost:8080/api/
class VerifyService {

    @Get("/")
    fun index(): String = "Hello Prism: get Verification"

    @Post("/")
    fun verify(holderSignedCredential:String):String{
        return holderSignedCredential+" - "+"VERIFIED"
    }

}