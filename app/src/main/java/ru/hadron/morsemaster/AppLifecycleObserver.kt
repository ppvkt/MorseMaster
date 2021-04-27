package ru.hadron.morsemaster

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import javax.inject.Inject

class AppLifecycleObserver @Inject constructor(
    context: Context
) : LifecycleObserver {

    companion object {
        var isMoveToBackground = false
        var isMoveToForeground = true
        var count = -1
    }

    private val enterForegroundToast =
        Toast.makeText(context, context.getString(R.string.foreground_message), Toast.LENGTH_SHORT)

    private val enterBackgroundToast =
        Toast.makeText(context, context.getString(R.string.background_message), Toast.LENGTH_SHORT)



    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        enterForegroundToast.showAfterCanceling(enterBackgroundToast)

        isMoveToForeground = true
        isMoveToBackground = false
        count++
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        enterBackgroundToast.showAfterCanceling(enterForegroundToast)

        isMoveToForeground = false
        isMoveToBackground = true
        count--
        count--
    }

    private fun Toast.showAfterCanceling(toastToCancel: Toast) {
        toastToCancel.cancel()
        this.show()
    }
}
