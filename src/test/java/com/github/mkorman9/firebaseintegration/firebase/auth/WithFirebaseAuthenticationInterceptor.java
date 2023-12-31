package com.github.mkorman9.firebaseintegration.firebase.auth;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.util.Arrays;
import java.util.stream.Collectors;

@WithFirebaseAuthentication
@Priority(Interceptor.Priority.PLATFORM_AFTER)
@Interceptor
public class WithFirebaseAuthenticationInterceptor {
    @AroundInvoke
    Object intercept(InvocationContext context) throws Exception {
        var withFirebaseAuthentication = context.getMethod().getAnnotation(WithFirebaseAuthentication.class);
        TestAuthenticationConfig.setup(new FirebaseAuthentication(
            withFirebaseAuthentication.uid(),
            withFirebaseAuthentication.tenantId(),
            withFirebaseAuthentication.issuer(),
            withFirebaseAuthentication.displayName(),
            withFirebaseAuthentication.picture(),
            withFirebaseAuthentication.email(),
            withFirebaseAuthentication.isEmailVerified(),
            Arrays.stream(withFirebaseAuthentication.claims())
                .collect(Collectors.toMap(FirebaseClaim::name, FirebaseClaim::value))
        ));

        var ret = context.proceed();

        TestAuthenticationConfig.reset();
        return ret;
    }
}
