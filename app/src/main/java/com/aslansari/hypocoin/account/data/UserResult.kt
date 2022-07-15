package com.aslansari.hypocoin.account.data

import androidx.annotation.Keep

@Keep
sealed class UserResult {
    object Error: UserResult()
    data class User(
        val uid: String,
        val email: String,
        val displayName: String,
        val balance: Long,
        val hasPassword: Boolean = false,
        val netWorth: Long = 0L,
        val createdAt: Long = 0L,
        val lastLogin: Long = 0L,
        val isEmailVerified: Boolean = false,
        val multiFactorMethods: List<String> = listOf(),
        val phoneNumber: String = "",
    ): UserResult()
}