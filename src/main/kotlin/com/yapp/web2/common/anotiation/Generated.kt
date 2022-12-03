package com.yapp.web2.common.anotiation

import java.lang.annotation.Documented

@Documented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
annotation class Generated
