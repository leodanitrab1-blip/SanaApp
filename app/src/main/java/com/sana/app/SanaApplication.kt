package com.sana.app

import android.app.Application

class SanaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    companion object { lateinit var instance: SanaApplication private set }
}
