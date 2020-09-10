package com.elanciers.bringszo

import android.app.Application
import com.elanciers.bringszo.Common.DBController
import com.elanciers.bringszo.Common.DatabaseManager

class AppClass : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseManager.initializeInstance(DBController(applicationContext))
    }

}