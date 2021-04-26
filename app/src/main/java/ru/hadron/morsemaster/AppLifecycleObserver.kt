package ru.hadron.morsemaster

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber
import javax.inject.Inject

class AppLifecycleObserver @Inject constructor(
    context: Context
) : LifecycleObserver {
    companion object {
        var isMoveToBackground = false
        var isMoveToForeBackground = true
    }

    private val enterForegroundToast =
        Toast.makeText(context, context.getString(R.string.foreground_message), Toast.LENGTH_SHORT)

    private val enterBackgroundToast =
        Toast.makeText(context, context.getString(R.string.background_message), Toast.LENGTH_SHORT)


    val isMovedToForeground: MutableLiveData<Boolean> = MutableLiveData()
    init {
        isMovedToForeground.postValue(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground(): Boolean {
        enterForegroundToast.showAfterCanceling(enterBackgroundToast)
        Timber.e(" app moved to foreground   //isMovedToForeground == ${isMovedToForeground.value}, ${isMoveToForeBackground}")
        isMovedToForeground.postValue(true)
        isMoveToForeBackground = true
        isMoveToBackground = false
        Timber.e(" app moved to foreground   //isMovedToForeground == ${isMovedToForeground.value}, ${isMoveToForeBackground}")
        return isMoveToForeBackground
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        enterBackgroundToast.showAfterCanceling(enterForegroundToast)
        Timber.e(" app moved to background /isMovedToForeground = ${isMovedToForeground.value}, ${isMoveToForeBackground}")
        isMoveToBackground = true
        isMovedToForeground.postValue(false)
        isMoveToForeBackground = false
        Timber.e(" app moved to background /isMovedToForeground = ${isMovedToForeground.value}, ${isMoveToForeBackground}")
    }
    private fun Toast.showAfterCanceling(toastToCancel: Toast) {
        toastToCancel.cancel()
        this.show()
    }
}
