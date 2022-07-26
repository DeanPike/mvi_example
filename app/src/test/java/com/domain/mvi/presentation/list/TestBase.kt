package com.domain.mvi.presentation.list

import com.domain.mvi.util.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After

@ExperimentalCoroutinesApi
abstract class TestBase {

    protected lateinit var dispatcherProvider: DispatcherProvider

    protected fun setupDispatcherProvider(scheduler: TestCoroutineScheduler) {
        val dispatcher = StandardTestDispatcher(scheduler)
        dispatcherProvider = object : DispatcherProvider {
            override fun getIoDispatcher(): CoroutineDispatcher = dispatcher

            override fun getMainDispatcher(): CoroutineDispatcher = dispatcher

            override fun getDefaultDispatcher(): CoroutineDispatcher = dispatcher
        }
        Dispatchers.setMain(dispatcher)
    }

    abstract fun setupTest(testScheduler: TestCoroutineScheduler)

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}