package com.aslansari.hypocoin.viewmodel.login;

public class LoginUIModel {

    public final boolean isLoading;
    public final boolean isFailed;
    public final boolean isComplete;

    public LoginUIModel(boolean isLoading, boolean isFailed, boolean isComplete) {
        this.isLoading = isLoading;
        this.isFailed = isFailed;
        this.isComplete = isComplete;
    }

    public static LoginUIModel idle() {
        return new LoginUIModel(false, false, false);
    }

    public static LoginUIModel loading() {
        return new LoginUIModel(true, false, false);
    }

    public static LoginUIModel fail() {
        return new LoginUIModel(false, true, false);
    }

    public static LoginUIModel complete() {
        return new LoginUIModel(false, false, true);
    }

}
