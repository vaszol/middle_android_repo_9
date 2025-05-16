package ru.yandex.loginapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty email and password should show EmptyFieldsError`() = runTest(testDispatcher) {
        viewModel.login("", "")

        assertEquals(LoginScreenState.EmptyFieldsError, viewModel.state.value)
    }

    @Test
    fun `invalid email format should show EmailValidationError`() = runTest(testDispatcher) {
        viewModel.login("invalid-email", "password123")

        assertEquals(LoginScreenState.EmailValidationError, viewModel.state.value)
    }

    @Test
    fun `valid credentials should transition to Loading state`() = runTest(testDispatcher) {
        viewModel.login("valid@email.com", "password123")

        testDispatcher.scheduler.runCurrent()
        assertEquals(LoginScreenState.Loading, viewModel.state.value)
    }

    @Test
    fun `after loading delay should show Success state`() = runTest(testDispatcher) {
        viewModel.login("valid@email.com", "password123")

        testDispatcher.scheduler.runCurrent()
        testDispatcher.scheduler.advanceTimeBy(2000)
        assertEquals(LoginScreenState.Loading, viewModel.state.value)
        testDispatcher.scheduler.advanceTimeBy(4000)
        assertEquals(LoginScreenState.Success, viewModel.state.value)
    }
}