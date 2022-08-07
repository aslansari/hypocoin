package com.aslansari.hypocoin.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

    @Provides
    fun auth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    fun realtimeDB(): FirebaseDatabase {
        val database = Firebase.database("https://hypo-coin-default-rtdb.europe-west1.firebasedatabase.app")
        return database
    }

    @Provides
    fun realtimeDBReference(firebaseDatabase: FirebaseDatabase): DatabaseReference {
        return firebaseDatabase.reference
    }

    @Provides
    fun analytics(): FirebaseAnalytics {
        return Firebase.analytics
    }

}