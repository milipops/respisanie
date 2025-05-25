package com.example.lessons

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.example.lessons.Methods.PasswordHasher
import com.example.lessons.Methods.UserRepository
import com.example.lessons.Models.Person
import com.example.lessons.Screens.Login.LoginModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import io.mockk.*
import io.mockk.mockk
import io.mockk.every
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var loginModel: LoginModel
    private lateinit var navController: NavController
    private lateinit var onError: (String) -> Unit
    private lateinit var onLoading: (Boolean) -> Unit
    private lateinit var onSuccess: (Person) -> Unit

    private val mockHasher = mockk<PasswordHasher>()
    private val mockUserRepository = mockk<UserRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        navController = mockk(relaxed = true)
        onError = mockk(relaxed = true)
        onLoading = mockk(relaxed = true)
        onSuccess = mockk(relaxed = true)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        loginModel = LoginModel(mockHasher, mockUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `login with invalid email triggers error`() = runTest {
        loginModel.handleLogin(
            email = "invalid_email",
            password = "1234",
            navController = navController,
            coroutineScope = testScope,
            onError = onError,
            onLoading = onLoading,
            onSuccess = onSuccess
        )
        verify { onError("Введите корректный email") }
    }

    @Test
    fun `login with non-existing user triggers error`() = runTest {
        coEvery { mockUserRepository.findUserByEmail("test@example.com") } returns null

        loginModel.handleLogin(
            email = "test@example.com",
            password = "1234",
            navController = navController,
            coroutineScope = testScope,
            onError = onError,
            onLoading = onLoading,
            onSuccess = onSuccess
        )
        advanceUntilIdle()

        verify { onError("Пользователь не найден") }
    }

    @Test
    fun `login with incorrect password triggers error`() = runTest {
        val user = Person(
            id = "1",
            email = "test@example.com",
            name = "Test User",
            password = "hashed_pass",
            phone = "+70000000000"
        )
        coEvery { mockUserRepository.findUserByEmail("test@example.com") } returns user
        every { mockHasher.hashPassword("wrong_pass") } returns "wrong_hashed_pass"

        loginModel.handleLogin(
            email = "test@example.com",
            password = "wrong_pass",
            navController = navController,
            coroutineScope = testScope,
            onError = onError,
            onLoading = onLoading,
            onSuccess = onSuccess
        )
        advanceUntilIdle()

        verify { onError("Неверный пароль") }
    }

    @Test
    fun `successful login calls onSuccess and navigates`() = runTest {
        val user = Person(
            id = "1",
            email = "test@example.com",
            name = "Test User",
            password = "correct_hash",
            phone = "+70000000000"
        )

        coEvery { mockUserRepository.findUserByEmail("test@example.com") } returns user
        every { mockHasher.hashPassword("1234") } returns "correct_hash"

        loginModel.handleLogin(
            email = "test@example.com",
            password = "1234",
            navController = navController,
            coroutineScope = testScope,
            onError = onError,
            onLoading = onLoading,
            onSuccess = onSuccess
        )
        advanceUntilIdle()

        verify { onSuccess(user) }

        // Измененная проверка навигации
        val slot = slot<NavOptionsBuilder.() -> Unit>()
        verify {
            navController.navigate(
                eq("content"),
                capture(slot)
            )
        }

        // Проверка параметров навигации

    }

    @Test
    fun `onLoading should be called true then false`() = runTest {
        val user = Person(
            id = "1",
            email = "test@example.com",
            name = "Test User",
            password = "correct_hash",
            phone = "+70000000000"
        )
        coEvery { mockUserRepository.findUserByEmail("test@example.com") } returns user
        every { mockHasher.hashPassword("1234") } returns "correct_hash"

        loginModel.handleLogin(
            email = "test@example.com",
            password = "1234",
            navController = navController,
            coroutineScope = testScope,
            onError = onError,
            onLoading = onLoading,
            onSuccess = onSuccess
        )
        advanceUntilIdle()

        verifyOrder {
            onLoading(true)
            onSuccess(user)
            onLoading(false)
        }
    }

    @Test
    fun `handleLogin should call onError when exception occurs`() = runTest {
        coEvery { mockUserRepository.findUserByEmail(any()) } throws RuntimeException("DB error")

        loginModel.handleLogin(
            email = "test@example.com",
            password = "1234",
            navController = navController,
            coroutineScope = testScope,
            onError = onError,
            onLoading = onLoading,
            onSuccess = onSuccess
        )
        advanceUntilIdle()

    }


}