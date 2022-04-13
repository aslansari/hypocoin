package com.aslansari.hypocoin.register

import androidx.lifecycle.ViewModel
import com.aslansari.hypocoin.register.dto.RegisterInput
import com.aslansari.hypocoin.register.exception.PasswordMismatchException
import com.aslansari.hypocoin.register.exception.RegisterException
import com.aslansari.hypocoin.register.exception.UserAlreadyExistsException
import com.aslansari.hypocoin.repository.AccountRepository
import com.aslansari.hypocoin.repository.model.Account
import com.aslansari.hypocoin.viewmodel.DataStatus
import com.aslansari.hypocoin.viewmodel.Resource
import com.aslansari.hypocoin.viewmodel.Resource.Companion.complete
import com.aslansari.hypocoin.viewmodel.Resource.Companion.error
import io.reactivex.rxjava3.core.Observable

/**
 * TODO: 6/27/2021 add user repository as dependency to verify user not exists
 */
class RegisterViewModel(
    private val register: Register,
    private val accountRepository: AccountRepository
): ViewModel() {


    fun validate(registerInput: RegisterInput): Observable<Resource<RegisterInput>> {
        return Observable.just(registerInput)
            .flatMap { (username, password, passwordRepeat) ->
                try {
                    // TODO: 6/27/2021 check if user exists
                    register.validateUsername(username)
                    register.validatePassword(password, passwordRepeat)
                } catch (exception: IllegalArgumentException) {
                    return@flatMap Observable.just(error(registerInput,
                        RegisterException(exception.message, exception)))
                } catch (exception: PasswordMismatchException) {
                    return@flatMap Observable.just(error(registerInput,
                        RegisterException(exception.message, exception)))
                }
                Observable.just(complete(registerInput))
            }
            .map { registerInputResource: Resource<RegisterInput> ->
                var resource: Resource<RegisterInput>? = null
                if (DataStatus.COMPLETE === registerInputResource.status) {
                    if (accountRepository.isAccountExists(registerInputResource.value!!.username)) {
                        resource = error(registerInputResource.value,
                            RegisterException("User already Exists", UserAlreadyExistsException()))
                    }
                }
                resource ?: registerInputResource
            }
    }

    fun register(registerInput: RegisterInput): Observable<Resource<Register>> {
        return Observable.just(registerInput)
            .map { registerInput1: RegisterInput ->
                register.validateUsername(registerInput1.username)
                register.validatePassword(registerInput1.password, registerInput1.passwordRepeat)
                registerInput1
            }
            .map { (username) ->
                Account(
                    username)
            }
            .flatMap { account: Account? ->
                accountRepository.createAccount(
                    account!!)
                    .andThen(Observable.just(complete(
                        register)))
            }
    }
}