package com.aslansari.hypocoin.register.unit;

import com.aslansari.hypocoin.register.Register;
import com.aslansari.hypocoin.register.RegisterViewModel;
import com.aslansari.hypocoin.register.dto.RegisterInput;
import com.aslansari.hypocoin.register.exception.PasswordMismatchException;
import com.aslansari.hypocoin.register.exception.RegisterException;
import com.aslansari.hypocoin.repository.AccountRepository;
import com.aslansari.hypocoin.viewmodel.DataStatus;
import com.aslansari.hypocoin.viewmodel.Resource;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class RegisterUnitTest {

    @Test(expected = IllegalArgumentException.class)
    public void empty_username_should_throw_illegal_argument() {
        Register register = new Register();
        register.validateUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void empty_password_should_throw_illegal_argument() throws PasswordMismatchException {
        Register register = new Register();
        register.validatePassword("", "password");
    }

    @Test(expected = IllegalArgumentException.class)
    public void empty_password_repeat_should_throw_illegal_argument() throws PasswordMismatchException {
        Register register = new Register();
        register.validatePassword("password", "");
    }

    @Test(expected = PasswordMismatchException.class)
    public void different_password_should_throw_password_mismatch() throws PasswordMismatchException {
        Register register = new Register();
        register.validatePassword("password", "differentPassword");
    }

//    @Test
//    public void different_password_should_return_register_error() {
//        Register register = new Register();
//        RegisterViewModel registerViewModel = new RegisterViewModel(register, null);
//
//        RegisterInput registerInput = new RegisterInput("username", "password", "differentPassword");
//        Resource<RegisterInput> registerResource = registerViewModel.validate(registerInput).blockingLast();
//
//        assertEquals(DataStatus.ERROR, registerResource.getStatus());
//        assertEquals(RegisterException.class, registerResource.getThrowable().getClass());
//    }
//
//    @Test
//    public void same_password_should_not_return_register_error() {
//        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
//        Register register = new Register();
//        when(accountRepository.isAccountExists(anyString())).thenReturn(false);
//        RegisterViewModel registerViewModel = new RegisterViewModel(register, accountRepository);
//
//        RegisterInput registerInput = new RegisterInput("username", "password", "password");
//        Resource<RegisterInput> registerResource = registerViewModel.validate(registerInput).blockingLast();
//
//        assertNotEquals(DataStatus.ERROR, registerResource.getStatus());
//        assertNull(registerResource.getThrowable());
//    }
}
