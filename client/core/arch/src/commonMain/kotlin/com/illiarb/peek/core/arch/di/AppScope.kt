package com.illiarb.peek.core.arch.di

import me.tatarka.inject.annotations.Scope
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
public annotation class AppScope