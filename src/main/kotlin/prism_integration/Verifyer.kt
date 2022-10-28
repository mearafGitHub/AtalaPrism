package com.fiarway.prismintegration

import io.iohk.atala.prism.api.CredentialClaim
import io.iohk.atala.prism.api.KeyGenerator
import io.iohk.atala.prism.api.models.AtalaOperationId
import io.iohk.atala.prism.api.models.AtalaOperationStatus
import io.iohk.atala.prism.api.node.NodeAuthApiImpl
import io.iohk.atala.prism.api.node.NodePayloadGenerator
import io.iohk.atala.prism.api.node.NodePublicApi
import io.iohk.atala.prism.credentials.PrismCredential
import io.iohk.atala.prism.crypto.MerkleInclusionProof
import io.iohk.atala.prism.crypto.derivation.KeyDerivation
import io.iohk.atala.prism.crypto.derivation.MnemonicCode
import io.iohk.atala.prism.crypto.keys.ECKeyPair
import io.iohk.atala.prism.identity.*
import io.iohk.atala.prism.protos.GrpcOptions
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive


class Verifyer{

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

    // creates unpublished did for holder
    fun holderCreateDID(): LongFormPrismDid {
        // Holder generates its identity
        val holderKeys = prepareKeysFromMnemonic(KeyDerivation.randomMnemonicCode(), "secret")
        val holderUnpublishedDid =
            PrismDid.buildLongFormFromMasterPublicKey(holderKeys[PrismDid.DEFAULT_MASTER_KEY_ID]?.publicKey!!)
        println("Holder: DID generated: $holderUnpublishedDid")
        return holderUnpublishedDid
    }

    // Waits until an operation is confirmed by the Cardano network.
    // NOTE: Confirmation doesn't necessarily mean that operation was applied.
    // For example, it could be rejected because of an incorrect signature or other reasons.
    fun waitUntilConfirmed(nodePublicApi: NodePublicApi, operationId: AtalaOperationId) {
        var status = runBlocking {
            nodePublicApi.getOperationInfo(operationId).status
        }
        while (status != AtalaOperationStatus.CONFIRMED_AND_APPLIED &&
            status != AtalaOperationStatus.CONFIRMED_AND_REJECTED
        ) {
            println("Current operation status: ${AtalaOperationStatus.asString(status)}")
            Thread.sleep(1000) // 1 second
            status = runBlocking {
                nodePublicApi.getOperationInfo(operationId).status
            }
        }
    }

    // Creates a list of potentially useful keys out of a mnemonic code
    fun prepareKeysFromMnemonic(mnemonic: MnemonicCode, pass: String): Map<String, ECKeyPair> {
        val seed = KeyDerivation.binarySeed(mnemonic, pass)
        val issuerMasterKeyPair = KeyGenerator.deriveKeyFromFullPath(seed, 0, MasterKeyUsage, 0)
        val issuerIssuingKeyPair = KeyGenerator.deriveKeyFromFullPath(seed, 0, IssuingKeyUsage, 0)
        val issuerRevocationKeyPair = KeyGenerator.deriveKeyFromFullPath(seed, 0, RevocationKeyUsage, 0)
        return mapOf(
            Pair(PrismDid.DEFAULT_MASTER_KEY_ID, issuerMasterKeyPair),
            Pair(PrismDid.DEFAULT_ISSUING_KEY_ID, issuerIssuingKeyPair),
            Pair(PrismDid.DEFAULT_REVOCATION_KEY_ID, issuerRevocationKeyPair)
        )
    }

    fun createIssuerDID(nodeAuthApi: NodeAuthApiImpl, holderUnpublishedDid: LongFormPrismDid): List<Any> {
        println("Creating Issuer DID")

        // Issuer claims an identity
        println("Issuer: Generates and registers a DID")
        val issuerKeys = prepareKeysFromMnemonic(KeyDerivation.randomMnemonicCode(), "passphrase")
        val issuerUnpublishedDid =
            PrismDid.buildLongFormFromMasterPublicKey(issuerKeys[PrismDid.DEFAULT_MASTER_KEY_ID]?.publicKey!!)
        println("""
        - Unpublished issuer Did:  $issuerUnpublishedDid is created
        """)
        println()
        val issuerDid = issuerUnpublishedDid.asCanonical()
        println("""
        - Canonical Unpublished issuer Did:  $issuerDid is created
        """)
        println()

        var issuerNodePayloadGenerator = NodePayloadGenerator(
            issuerUnpublishedDid,
            mapOf(
                PrismDid.DEFAULT_MASTER_KEY_ID to issuerKeys[PrismDid.DEFAULT_MASTER_KEY_ID]?.privateKey!!
            )
        )
        // creation of CreateDID operation
        val issuerCreateDidInfo = issuerNodePayloadGenerator.createDid()

        // sending CreateDID operation to the ledger
        val issuerCreateDidOperationId = runBlocking {
            nodeAuthApi.createDid(
                issuerCreateDidInfo.payload,
                issuerUnpublishedDid,
                PrismDid.DEFAULT_MASTER_KEY_ID
            )
        }

        println(
            """
            - Issuer sent a request to create a new DID to PRISM Node.
            - The transaction can take up to 10 minutes to be confirmed by the Cardano network.
            - Operation identifier: ${issuerCreateDidOperationId.hexValue()}
            """.trimIndent()
            )
        println()

        // Wait until Cardano network confirms the DID creation
        waitUntilConfirmed(nodeAuthApi, issuerCreateDidOperationId)

        println(
            """
        - DID with id $issuerDid is created
        """.trimIndent()
        )
        println()
        // return issuerDid

        // Generator should contain the issuing key so let's create a new instance of it with this key inside
        issuerNodePayloadGenerator = NodePayloadGenerator(
            issuerNodePayloadGenerator.did,
            issuerNodePayloadGenerator.keys +
                    (PrismDid.DEFAULT_ISSUING_KEY_ID to issuerKeys[PrismDid.DEFAULT_ISSUING_KEY_ID]?.privateKey!!)
        )
        val issuingKeyInfo =
            PrismKeyInformation(
                DidPublicKey(
                    PrismDid.DEFAULT_ISSUING_KEY_ID,
                    IssuingKeyUsage,
                    issuerKeys[PrismDid.DEFAULT_ISSUING_KEY_ID]?.publicKey!!
                )
            )
// creation of UpdateDID operation
        val addIssuingKeyDidInfo = issuerNodePayloadGenerator.updateDid(
            issuerCreateDidInfo.operationHash,
            PrismDid.DEFAULT_MASTER_KEY_ID,
            keysToAdd = arrayOf(issuingKeyInfo)
        )
// sending the operation to the ledger
        val addIssuingKeyOperationId = runBlocking {
            nodeAuthApi.updateDid(
                addIssuingKeyDidInfo.payload,
                issuerDid,
                PrismDid.DEFAULT_MASTER_KEY_ID,
                issuerCreateDidInfo.operationHash,
                keysToAdd = arrayOf(issuingKeyInfo),
                keysToRevoke = arrayOf()
            )
        }
        println(
            """
            Issuer: Add issuing key, the transaction can take up to 10 minutes to be confirmed by the Cardano network
            - IssuerDID = $issuerDid
            - Add issuing key to DID operation identifier = ${addIssuingKeyOperationId.hexValue()}
            """.trimIndent())

        // prepare credential for a holder
        // Issuer generates a credential to Holder identified by its unpublished DID
        val credentialClaim = CredentialClaim(
            subjectDid = holderUnpublishedDid,
            content = JsonObject(
                mapOf(
                    Pair("name", JsonPrimitive("Mearaf Tadewos")),
                    Pair("certificate", JsonPrimitive("Certificate of First Degree in Computer Science")),
                    Pair("issuer", JsonPrimitive("Jimma University"))
                )
            )
        )
        val issueCredentialsInfo = issuerNodePayloadGenerator.issueCredentials(
            PrismDid.DEFAULT_ISSUING_KEY_ID,
            arrayOf(credentialClaim)
        )

        // publish the credential in to cardano blockchain
        val issueCredentialBatchOperationId = runBlocking {
            nodeAuthApi.issueCredentials(
                issueCredentialsInfo.payload,
                issuerDid,
                PrismDid.DEFAULT_ISSUING_KEY_ID,
                issueCredentialsInfo.merkleRoot
            )
        }
        // Wait for the process to finish
        waitUntilConfirmed(nodeAuthApi, issueCredentialBatchOperationId)

        val holderSignedCredential = issueCredentialsInfo.credentialsAndProofs.first().signedCredential
        val holderCredentialMerkleProof = issueCredentialsInfo.credentialsAndProofs.first().inclusionProof

        println("""Holder full signed credential: ${holderSignedCredential} """ )

        println(
            """
            Issuer [$issuerDid] issued new credentials for the holder [$holderUnpublishedDid].
            - issueCredentialBatch operation identifier: ${issueCredentialBatchOperationId.hexValue()}
            - Holder's Credential content: ${holderSignedCredential.content}
            - Holder Signed credential: ${holderSignedCredential.canonicalForm}
            - Inclusion proof (encoded): ${holderCredentialMerkleProof.encode()}
            - Batch id: ${issueCredentialsInfo.batchId}
            """.trimIndent()
        )

        var return_list = listOf(
            issuerDid,
            issuerNodePayloadGenerator,
            holderSignedCredential,
            holderCredentialMerkleProof
        )  // arrayList
        return return_list
    }

    fun verifier(nodeAuthApi: NodeAuthApiImpl,
                 holderSignedCredential: PrismCredential,
                 holderCredentialMerkleProof: MerkleInclusionProof) {
        // Verifier, who owns credentialClam, can easily verify the validity of the credentials.
        println("""Holder full signed credential: ${holderSignedCredential} """ )
        println("Verifier: Verifying received credential using single convenience method")

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

    fun holder_cred_former(holderCredInfo:String){
        /*var sample_cred = JsonBasedCredential(content=
        CredentialContent(
            fields={
                "id":holderCredInfo.issuerDID
                "keyId":holderCredInfo.issuingKey,
                "credentialSubject":{
                    "name":holderCredInfo.holder_fullName,
                    "certificate":holderCredInfo.certificateConcern,
                    "issuer":holderCredInfo.issuerName,
                    "id":holderCredInfo.holderDID}),
            signature= ECSignature@278f8425) */
    }

    fun main(args: Array<String>) {
        println("Hello Atala Prism!")
///*
        val environment = "ppp-vasil.atalaprism.io"
        val nodeAuthApi = NodeAuthApiImpl(GrpcOptions("http", environment, 50053))
        val holderUnpublishedDid = holderCreateDID()
        var returned_list = createIssuerDID(nodeAuthApi, holderUnpublishedDid)
        var issuerDid = returned_list.get(0)
        var issuerNodePayloadGenerator = returned_list.get(1)
        var holderSignedCredential = returned_list.get(2)
        var holderCredentialMerkleProof = returned_list.get(3)

        /*   var credInfo = JsonBasedCredential(content= CredentialContent(fields={
               "id":"did:prism:d496fc273fe347e42bc1226b6128ed7a7bbb5f421cbfe4279675f767493e4e99",
               "keyId":"issuing0",
               "credentialSubject":{
                   "name":"Mearaf Tadewos",
                   "certificate":"Certificate of First Degree in Computer Science",
                   "issuer":"Jimma University",
                   "id":"did:prism:be427ec7a5cb2724ee6410d2c977747d832a2fe44d33c520035d6aab7deef22a:Cj8KPRI7CgdtYXN0ZXIwEAFKLgoJc2VjcDI1NmsxEiECZuwAvbIevPJ0-Y5WhNQCT-7msecXHHKyiGszbGH0lYs"}
               }),
               signature = ECSignature@5eccd3b9)*/

        verifier(nodeAuthApi,
            holderSignedCredential as PrismCredential,
            holderCredentialMerkleProof as MerkleInclusionProof
        )
//*/
    }


}

