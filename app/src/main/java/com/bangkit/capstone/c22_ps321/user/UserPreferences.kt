package com.bangkit.capstone.c22_ps321.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    
    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null
        private val NAME = stringPreferencesKey("name")
        private val EMAIL = stringPreferencesKey("email")
        private val PASSWORD = stringPreferencesKey("password")
        private val TOKEN = stringPreferencesKey("token")
        private val STATE = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun login() {
        dataStore.edit { preferences ->
            preferences[STATE] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[STATE] = false
            preferences.clear()
        }
    }

    suspend fun token(user: User) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = user.token
        }
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[NAME] = user.name
            preferences[EMAIL] = user.email
            preferences[PASSWORD] = user.password
            preferences[STATE] = user.isLogin
        }
    }

    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[NAME] ?: "",
                preferences[EMAIL] ?: "",
                preferences[PASSWORD] ?: "",
                preferences[STATE] ?: false,
                preferences[TOKEN] ?: ""
            )
        }
    }
}