package com.example.propcare

import android.app.Application
import com.example.propcare.di.AppContainer

class PropCareApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}