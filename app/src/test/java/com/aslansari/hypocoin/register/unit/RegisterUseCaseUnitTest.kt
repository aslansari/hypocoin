package com.aslansari.hypocoin.register.unit

import com.aslansari.hypocoin.register.RegisterUseCase
import com.aslansari.hypocoin.register.exception.PasswordMismatchException
import org.junit.Test

class RegisterUseCaseUnitTest {

    @Test(expected = IllegalArgumentException::class)
    fun `empty username should throw illegal argument`() {
        val registerUseCase = RegisterUseCase()
        registerUseCase.validateUsername(null)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty password should throw illegal argument`() {
        val registerUseCase = RegisterUseCase()
        registerUseCase.validatePassword("", "password")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty password repeat should throw illegal argument`() {
        val registerUseCase = RegisterUseCase()
        registerUseCase.validatePassword("password", "")
    }

    @Test(expected = PasswordMismatchException::class)
    fun `different password should throw password mismatch`() {
        val registerUseCase = RegisterUseCase()
        registerUseCase.validatePassword("password", "differentPassword")
    }
}