package com.example.pokedex.ui

import android.app.Application
import com.example.pokedex.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) { Timber.plant(Timber.DebugTree()) }
    }

}