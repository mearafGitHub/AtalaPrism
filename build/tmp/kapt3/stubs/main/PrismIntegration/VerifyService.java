package PrismIntegration;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0004H\u0007J4\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00042\u0006\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u00052\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\nH\u0007\u00a8\u0006\u000b"}, d2 = {"LPrismIntegration/VerifyService;", "", "()V", "index", "Lio/micronaut/http/MutableHttpResponse;", "", "verify", "holderSignedCredentialDID", "userName", "education", "Ljava/util/HashMap;", "PrismIntegration"})
@io.micronaut.http.annotation.Controller(value = "/")
public final class VerifyService {
    
    public VerifyService() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    @io.micronaut.http.annotation.Get(value = "/")
    public final io.micronaut.http.MutableHttpResponse<java.lang.String> index() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    @io.micronaut.http.annotation.Post(value = "/api/verify")
    public final io.micronaut.http.MutableHttpResponse<java.lang.Object> verify(@org.jetbrains.annotations.NotNull
    java.lang.String holderSignedCredentialDID, @org.jetbrains.annotations.NotNull
    java.lang.String userName, @org.jetbrains.annotations.NotNull
    java.util.HashMap<java.lang.String, java.lang.String> education) {
        return null;
    }
}