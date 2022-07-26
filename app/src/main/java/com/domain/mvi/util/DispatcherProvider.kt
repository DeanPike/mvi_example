package com.domain.mvi.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

interface DispatcherProvider {
    fun getIoDispatcher(): CoroutineDispatcher
    fun getMainDispatcher(): CoroutineDispatcher
    fun getDefaultDispatcher(): CoroutineDispatcher
}

@Singleton
class DispatcherProviderImpl @Inject constructor() : DispatcherProvider {
    override fun getIoDispatcher() = Dispatchers.IO

    override fun getMainDispatcher() = Dispatchers.Main

    override fun getDefaultDispatcher() = Dispatchers.Default
}