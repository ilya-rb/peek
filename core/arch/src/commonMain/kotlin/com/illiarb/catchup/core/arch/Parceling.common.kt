package com.illiarb.catchup.core.arch

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommonParcelize

expect interface CommonParcelable
