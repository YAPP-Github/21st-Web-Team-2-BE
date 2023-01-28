package com.yapp.web2.common

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithUserDetailsSecurityContextFactory::class)
annotation class TestMember
