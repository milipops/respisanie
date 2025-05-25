package com.example.lessons

import com.example.lessons.Screens.Register.RegisterState
import com.example.lessons.Screens.Register.RegisterViewModel
import com.example.lessons.Screens.Register.ResultState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        viewModel = RegisterViewModel()
    }

    @Test
    fun emailIsValidWhenCorrect() {
        val state = RegisterState(email = "test@example.com")
        viewModel.updateState(state)
        assertTrue(viewModel.uiState.value.isEmailValid)
    }

    @Test
    fun emailIsInvalidWhenIncorrect() {
        val state = RegisterState(email = "invalid-email")
        viewModel.updateState(state)
        assertFalse(viewModel.uiState.value.isEmailValid)
    }

    @Test
    fun phoneisvalidwhenstartswith7andlength11() {
        assertTrue(viewModel.isPhoneValid("71234567890"))
    }

    @Test
    fun phoneisinvalidwhenlengthnot11() {
        assertFalse(viewModel.isPhoneValid("71234"))
    }

    @Test
    fun phoneisinvalidwhennotstartswith7() {
        assertFalse(viewModel.isPhoneValid("81234567890"))
    }

    @Test
    fun passwordmismatchtriggerserror() {
        val state = RegisterState(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "pass123",
            phone = "71234567890"
        )
        viewModel.updateState(state)
        viewModel.register()

        val result = viewModel.resultState.value
        assertTrue(result is ResultState.Error)
        assertEquals("Пароли не совпадают", (result as ResultState.Error).message)
    }


}
