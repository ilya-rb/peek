package com.illiarb.peek.core.arch.di

import dev.zacsweers.metro.Qualifier
import dev.zacsweers.metro.Scope

@Scope
public annotation class AppScope

@Scope
public annotation class UiScope

@Qualifier
public annotation class AppCoroutineScope
