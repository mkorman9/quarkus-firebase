package com.github.mkorman9.firebase.auth;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@InterceptorBinding
public @interface WithFirebaseAuthentication {
    @Nonbinding
    String uid() default "default_firebase_uid";

    @Nonbinding
    String tenantId() default "";

    @Nonbinding
    String issuer() default "";

    @Nonbinding
    String displayName() default "";

    @Nonbinding
    String picture() default "";

    @Nonbinding
    String email() default "";

    @Nonbinding
    boolean isEmailVerified() default false;
}
