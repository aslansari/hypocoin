package com.aslansari.hypocoin.app.util

import android.os.Bundle
import com.aslansari.hypocoin.app.util.log.LoginMethod
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

class AnalyticsReporter(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    fun reportSubmitEmailForLoginClick() {
        firebaseAnalytics.logEvent("submit_email_for_login_click", Bundle().apply {

        })
    }

    fun reportSignUpButtonClick() {
        firebaseAnalytics.logEvent("sign_up_button_click", Bundle().apply {

        })
    }

    fun reportSignInWithGoogleClick() {
        firebaseAnalytics.logEvent("sign_in_with_google_click", Bundle().apply {

        })
    }

    fun reportForgotPasswordClick() {
        firebaseAnalytics.logEvent("forgot_password_click", Bundle().apply {

        })
    }

    fun reportRegisterWGoogleDialogCancelClick() {
        firebaseAnalytics.logEvent("register_with_google_dialog_cancel", Bundle().apply {

        })
    }

    fun reportRegisterWGoogleDialogContinueClick() {
        firebaseAnalytics.logEvent("register_with_google_dialog_continue", Bundle().apply {

        })
    }

    fun reportLoginButtonClick() {
        firebaseAnalytics.logEvent("login_button_click", Bundle().apply {

        })
    }

    fun reportSubmitEmailForRegisterClick() {
        firebaseAnalytics.logEvent("submit_email_for_register_click", Bundle().apply {

        })
    }

    fun reportRegisterWGoogleButtonClick() {
        firebaseAnalytics.logEvent("register_with_google_button_click", Bundle().apply {

        })
    }

    fun reportTermsAndCondLinkClick() {
        firebaseAnalytics.logEvent("terms_and_conditions_link_click", Bundle().apply {

        })
    }

    fun reportPrivacyPolicyLinkClick() {
        firebaseAnalytics.logEvent("privacy_policy_link_click", Bundle().apply {

        })
    }

    fun reportRegisterButtonClick() {
        firebaseAnalytics.logEvent("register_button_click", Bundle().apply {

        })
    }

    fun reportRegisterSuccessPageShow() {
        firebaseAnalytics.logEvent("register_success_page_show", Bundle().apply {

        })
    }

    fun reportGoogleRegisterSuccess() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
            param(FirebaseAnalytics.Param.METHOD, LoginMethod.GOOGLE.name)
        }
    }

    fun reportEmailRegisterSuccess() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
            param(FirebaseAnalytics.Param.METHOD, LoginMethod.EMAIL.name)
        }
    }

    fun reportGoogleLoginSuccess() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
            param(FirebaseAnalytics.Param.METHOD, LoginMethod.GOOGLE.name)
        }
    }

    fun reportEmailLoginSuccess() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
            param(FirebaseAnalytics.Param.METHOD, LoginMethod.EMAIL.name)
        }
    }

}