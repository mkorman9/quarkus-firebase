package com.github.mkorman9.firebaseintegration.firebase.auth;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@WithFirebaseAuthentication
@Priority(Interceptor.Priority.PLATFORM_AFTER)
@Interceptor
public class WithFirebaseAuthenticationInterceptor {
    @AroundInvoke
    Object intercept(InvocationContext context) throws Exception {
        var withFirebaseAuthentication = context.getMethod().getAnnotation(WithFirebaseAuthentication.class);
        TestAuthenticationConfig.mockUid(withFirebaseAuthentication.uid());
        TestAuthenticationConfig.mockTenantId(withFirebaseAuthentication.tenantId());
        TestAuthenticationConfig.mockIssuer(withFirebaseAuthentication.issuer());
        TestAuthenticationConfig.mockDisplayName(withFirebaseAuthentication.displayName());
        TestAuthenticationConfig.mockPicture(withFirebaseAuthentication.picture());
        TestAuthenticationConfig.mockEmail(withFirebaseAuthentication.email());
        TestAuthenticationConfig.mockEmailVerified(withFirebaseAuthentication.isEmailVerified());

        var ret = context.proceed();

        TestAuthenticationConfig.reset();
        return ret;
    }
}
