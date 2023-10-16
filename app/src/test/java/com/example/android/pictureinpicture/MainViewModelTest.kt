package com.example.android.pictureinpicture

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `should have correct initial value`() {
        val viewModel = MainViewModel { 0 }

        viewModel.started.value.shouldBe(false)
        viewModel.time.value.shouldBeNull()
    }

    @Test
    fun `should trigger start`() {
        val viewModel = MainViewModel { 0 }
        viewModel.startOrPause()

        viewModel.started.value.shouldBe(true)
    }

    @Test
    fun `should trigger stop`() {
        val viewModel = MainViewModel(object : () -> Long {
            private var time = 0L
            override fun invoke(): Long = time++
        })
        viewModel.startOrPause()
        viewModel.startOrPause()

        viewModel.started.value.shouldBe(false)
    }

    @Test
    fun `should increment timer`() {
        val viewModel = MainViewModel(object : () -> Long {
            private var time = 0L
            override fun invoke(): Long = time++
        })
        viewModel.startOrPause()

        viewModel.timeLong.value.shouldNotBeNull()
        viewModel.timeLong.value!!.shouldBeGreaterThan(0)
    }

    @Test
    fun `should reset timer after clear`() {
        val viewModel = MainViewModel(object : () -> Long {
            private var time = 0L
            override fun invoke(): Long = time++
        })
        viewModel.startOrPause()
        viewModel.clear()

        viewModel.timeLong.value.shouldBe(0)
    }
}
