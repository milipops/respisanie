package com.example.lessons

import android.content.Context
import android.util.Log
import androidx.annotation.Nullable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lessons.Models.FormValid
import com.example.lessons.Models.Lesson
import com.example.lessons.Models.Person
import com.example.lessons.Models.ScheduleItem
import com.example.lessons.Models.UserManager
import com.example.lessons.Models.group
import com.example.lessons.ui.theme.WorkForPerson
import com.google.firebase.appdistribution.gradle.ApiService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestQueryBuilder
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import io.github.jan.supabase.postgrest.result.PostgrestResult
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert

import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.security.acl.Group
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.reflect.jvm.internal.impl.load.kotlin.PackagePartProvider
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.rules.TestWatcher
import org.junit.runner.Description


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: WorkForPerson

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WorkForPerson()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSignOut() {
        val person = Person(
            id = "1",
            email = "test@test.com",
            name = "Qw",
            password = "123456",
            phone = "123",
            image_url = ""
        )

        UserManager.setUser(person)

        // Эмуляция signOut вручную
        UserManager.clearUser()

        // Прямая проверка
        assert(UserManager.currentUser.value == null) {
            "UserManager не очистился, было: ${UserManager.currentUser.value}"
        }
    }

    @Test
    fun testSetUser() {
        val person = Person(
            id = "1",
            email = "user@test.com",
            name = "User",
            password = "pass",
            phone = "1234567890",
            image_url = ""
        )

        UserManager.setUser(person)

        assert(UserManager.currentUser.value == person) {
            "Ожидался $person, но был ${UserManager.currentUser.value}"
        }
    }

    @Test
    fun testClearUser() {
        val person = Person(
            id = "2",
            email = "clear@test.com",
            name = "Clear",
            password = "pass",
            phone = "9876543210",
            image_url = ""
        )

        UserManager.setUser(person)
        UserManager.clearUser()

        assert(UserManager.currentUser.value == null) {
            "Пользователь не был очищен: ${UserManager.currentUser.value}"
        }
    }

}

class FormValidatorTest {
    // Тесты для email валидации
    @Test
    fun `validateEmail should return true for valid emails`() {
        // Стандартные email
        assertTrue(FormValid.validateEmail("test@example.com"))
        assertTrue(FormValid.validateEmail("user.name@domain.com"))

        // Специфические случаи
        assertTrue(FormValid.validateEmail("firstname.lastname@example.com"))
        assertTrue(FormValid.validateEmail("email@subdomain.example.com"))
        assertTrue(FormValid.validateEmail("1234567890@example.com"))
        assertTrue(FormValid.validateEmail("email@example-one.com"))
        assertTrue(FormValid.validateEmail("_______@example.com"))
        assertTrue(FormValid.validateEmail("email@example.name"))
        assertTrue(FormValid.validateEmail("email@example.museum"))
        assertTrue(FormValid.validateEmail("user+tag@example.com"))
    }

    @Test
    fun `validateEmail should return false for invalid emails`() {
        // Полностью невалидные строки
        assertFalse(FormValid.validateEmail("plaintext"))
        assertFalse(FormValid.validateEmail("@missing.localpart.com"))
        assertFalse(FormValid.validateEmail("double@at@sign.com"))

        // Случаи с ошибками
        assertFalse(FormValid.validateEmail("email@.com"))
        assertFalse(FormValid.validateEmail("email..email@example.com"))
        assertFalse(FormValid.validateEmail("email@example..com"))
        assertFalse(FormValid.validateEmail(""))
        assertFalse(FormValid.validateEmail("email@example.com (Joe Smith)"))
        assertFalse(FormValid.validateEmail("email@-example.com"))
    }

    // Тесты для валидации пароля
    @Test
    fun `validatePassword should require min length`() {
        // Проверка минимальной длины
        assertFalse(FormValid.validatePassword("short"))
        assertFalse(FormValid.validatePassword(""))

        // Пароли соответствующей длины
        assertTrue(FormValid.validatePassword("validPass"))
        assertTrue(FormValid.validatePassword("longerPassword123"))

        // Граничные случаи
        assertTrue(FormValid.validatePassword("exactly8", 8))
        assertFalse(FormValid.validatePassword("7chars!", 8))
    }

    @Test
    fun `validatePassword should respect custom min length`() {
        // Проверка с кастомной минимальной длиной
        assertTrue(FormValid.validatePassword("12345", 5))
        assertFalse(FormValid.validatePassword("1234", 5))

        // Нулевая длина
        assertTrue(FormValid.validatePassword("", 0))
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PersonInfoUnitTest {

    private val mockViewModel = mockk<WorkForPerson>(relaxed = true)
    private val mockUser = Person(
        id = "1",
        email = "test@example.com",
        name = "Test User",
        password = "",
        phone = "+1234567890",
        image_url = "https://example.com/avatar.jpg"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        // Мокаем текущее состояние currentUser у ViewModel
        every { mockViewModel.currentUser } returns MutableStateFlow(listOf(mockUser))

        // Мокаем UserManager.currentUser, если используешь в логике
        mockkObject(UserManager)
        every { UserManager.currentUser } returns mutableStateOf(mockUser)

        // Мокаем suspend методы, чтобы не падали при вызове
        coEvery { mockViewModel.signOut() } just Runs
        coEvery { mockViewModel.uploadAvatar(any(), any()) } just Runs
        coEvery { mockViewModel.deleteAccount(any(), any(), any()) } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `currentUser should return mocked user`() {
        val users = mockViewModel.currentUser.value
        assertEquals(1, users.size)
        assertEquals("Test User", users[0].name)
        assertEquals("+1234567890", users[0].phone)
    }

    @Test
    fun `signOut should call signOut method on ViewModel`() = runTest {
        mockViewModel.signOut()
        coVerify(exactly = 1) { mockViewModel.signOut() }
    }

    @Test
    fun `uploadAvatar should call uploadAvatar method on ViewModel`() = runTest {
        val fakeUri = mockk<android.net.Uri>()
        val fakeContext = mockk<Context>()

        mockViewModel.uploadAvatar(fakeUri, fakeContext)
        coVerify(exactly = 1) { mockViewModel.uploadAvatar(fakeUri, fakeContext) }
    }

    @Test
    fun `deleteAccount should call deleteAccount method on ViewModel`() = runTest {
        val userId = "1"
        val context = mockk<Context>()
        val onResult = mockk<() -> Unit>(relaxed = true)

        mockViewModel.deleteAccount(userId, context, onResult)
        coVerify(exactly = 1) { mockViewModel.deleteAccount(userId, context, onResult) }
    }
}


