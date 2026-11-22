package com.example.propcare.di

import android.content.Context
import com.example.propcare.data.local.PropCareDatabase
import com.example.propcare.data.repository.PropCareRepository

class AppContainer(context: Context) {

    private val database: PropCareDatabase by lazy {
        PropCareDatabase.getDatabase(context)
    }

    val repository: PropCareRepository by lazy {
        PropCareRepository(
            userDao = database.userDao(),
            complaintDao = database.complaintDao()
        )
    }
}
