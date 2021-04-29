package ru.hadron.morsemaster

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {

    @Inject
    lateinit var appLifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        // register observer
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
    }
}