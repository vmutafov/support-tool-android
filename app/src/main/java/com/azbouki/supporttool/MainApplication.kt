package com.azbouki.supporttool

import android.app.Application
import com.azbouki.supporttool.sdk.SupportTool

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SupportTool.init(applicationContext = this, supportToolKey = "N30ahj0Yvo967gL5wHkT")
    }
}