package com.github.mkorman9.firebase.auth;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@WithFirebaseAuthorization
@Priority(Interceptor.Priority.PLATFORM_AFTER)
@Interceptor
public class WithFirebaseAuthorizationInterceptor {
    @AroundInvoke
    Object intercept(InvocationContext context) throws Exception {
        var withFirebaseAuthorization = context.getMethod().getAnnotation(WithFirebaseAuthorization.class);
        TestAuthorizationConfig.mockUid(withFirebaseAuthorization.uid());
        TestAuthorizationConfig.mockTenantId(withFirebaseAuthorization.tenantId());
        TestAuthorizationConfig.mockIssuer(withFirebaseAuthorization.issuer());
        TestAuthorizationConfig.mockDisplayName(withFirebaseAuthorization.displayName());
        TestAuthorizationConfig.mockPicture(withFirebaseAuthorization.picture());
        TestAuthorizationConfig.mockEmail(withFirebaseAuthorization.email());
        TestAuthorizationConfig.mockEmailVerified(withFirebaseAuthorization.isEmailVerified());

        var ret = context.proceed();

        TestAuthorizationConfig.reset();
        return ret;
    }
}
