package com.aslansari.hypocoin.account.data

import com.google.firebase.database.Exclude

sealed class UserResult {
    object Error: UserResult()
    data class User(
        val uid: String,
        val email: String,
        val displayName: String,
        val balance: Long,
        @Exclude val hasPassword: Boolean = false,
        @Exclude val netWorth: Long = 0L,
        @Exclude val createdAt: Long = 0L,
        @Exclude val lastLogin: Long = 0L,
        @Exclude val isEmailVerified: Boolean = false,
        @Exclude val multiFactorMethods: List<String> = listOf(),
        @Exclude val phoneNumber: String = "",
    ): UserResult()
}