package com.aslansari.hypocoin.register.behaviour;

import com.aslansari.hypocoin.register.Register;
import com.aslansari.hypocoin.register.RegisterViewModel;
import com.aslansari.hypocoin.register.dto.RegisterInput;
import com.aslansari.hypocoin.register.exception.PasswordMismatchException;
import com.aslansari.hypocoin.repository.AccountRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class RegisterBehaviourTest {

    @Mock
    public RegisterInput registerInput;
    @Mock
    public Register register;
    @Mock
    public AccountRepository accountRepository;

    @Test
    public void should_validate_username_when_validate_register() {
        given(registerInput.getUsername()).willReturn(null);
        RegisterViewModel registerViewModel = new RegisterViewModel(register, accountRepository);

        registerViewModel.validate(registerInput).blockingLast();

        then(register).should().validateUsername(null);
    }

    @Test
    public void should_validate_password_when_validate_register() throws PasswordMismatchException {
        given(registerInput.getUsername()).willReturn("");
        given(registerInput.getPassword()).willReturn("password");
        given(registerInput.getPasswordRepeat()).willReturn("differentpassword");
        RegisterViewModel registerViewModel = new RegisterViewModel(register, accountRepository);

        registerViewModel.validate(registerInput).blockingLast();

        then(register).should().validatePassword("password", "differentpassword");
    }
}
