package PrismIntegration;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\u0018\u0000 \u00032\u00020\u0001:\u0001\u0003B\u0005\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0004"}, d2 = {"LPrismIntegration/VerifyEndpoint;", "", "()V", "Companion", "PrismIntegration"})
public final class VerifyEndpoint {
    @org.jetbrains.annotations.NotNull
    public static final PrismIntegration.VerifyEndpoint.Companion Companion = null;
    
    public VerifyEndpoint() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002JB\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00042\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00072\u0006\u0010\b\u001a\u00020\u00052\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\nJ,\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0005J\u001e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bJ6\u0010\u001c\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00042\u0006\u0010\u001d\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u00052\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\n\u00a8\u0006\u001e"}, d2 = {"LPrismIntegration/VerifyEndpoint$Companion;", "", "()V", "fairwayVerify", "", "", "credContentMap", "", "userName", "education", "Ljava/util/HashMap;", "prismCredential_maker", "", "contentBytes", "", "content", "Lio/iohk/atala/prism/credentials/content/CredentialContent;", "signature", "Lio/iohk/atala/prism/crypto/signature/ECSignature;", "canonicalForm", "prismVerify", "", "nodeAuthApi", "Lio/iohk/atala/prism/api/node/NodeAuthApiImpl;", "holderSignedCredential", "Lio/iohk/atala/prism/credentials/PrismCredential;", "holderCredentialMerkleProof", "Lio/iohk/atala/prism/crypto/MerkleInclusionProof;", "verifier", "encodedSignedCredential", "PrismIntegration"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final boolean prismVerify(@org.jetbrains.annotations.NotNull
        io.iohk.atala.prism.api.node.NodeAuthApiImpl nodeAuthApi, @org.jetbrains.annotations.NotNull
        io.iohk.atala.prism.credentials.PrismCredential holderSignedCredential, @org.jetbrains.annotations.NotNull
        io.iohk.atala.prism.crypto.MerkleInclusionProof holderCredentialMerkleProof) {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<java.lang.Object> prismCredential_maker(@org.jetbrains.annotations.NotNull
        byte[] contentBytes, @org.jetbrains.annotations.NotNull
        io.iohk.atala.prism.credentials.content.CredentialContent content, @org.jetbrains.annotations.NotNull
        io.iohk.atala.prism.crypto.signature.ECSignature signature, @org.jetbrains.annotations.NotNull
        java.lang.String canonicalForm) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.Map<java.lang.String, java.lang.Object> fairwayVerify(@org.jetbrains.annotations.NotNull
        java.util.Map<java.lang.String, ? extends java.lang.Object> credContentMap, @org.jetbrains.annotations.NotNull
        java.lang.String userName, @org.jetbrains.annotations.NotNull
        java.util.HashMap<java.lang.String, java.lang.String> education) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.Map<java.lang.String, java.lang.Object> verifier(@org.jetbrains.annotations.NotNull
        java.lang.String encodedSignedCredential, @org.jetbrains.annotations.NotNull
        java.lang.String userName, @org.jetbrains.annotations.NotNull
        java.util.HashMap<java.lang.String, java.lang.String> education) {
            return null;
        }
    }
}