package com.akshayashokcode.youtubeandroid.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiKeyClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OAuthClient
