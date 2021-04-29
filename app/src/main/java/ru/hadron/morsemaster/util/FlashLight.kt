package ru.hadron.morsemaster.util

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.*
import ru.hadron.morsemaster.util.Constants.ATTACK
import ru.hadron.morsemaster.util.Constants.SYMBOL_PAUSE
import ru.hadron.morsemaster.util.Constants.WORD_PAUSE
import timber.log.Timber
import kotlin.math.roundToInt

class FlashLight constructor(
    val context: Context
) {
    private var dit = 100 // ms
    private var dah = 300 //ms

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val packageManager = context.packageManager as PackageManager

    companion object {
        var isDeviceHasCamera = false
    }

    private var job: Job? = null

    fun cancelPlayLightQuestion() {
        if (job != null && job!!.isActive) {
            job!!.cancel()
        }
    }

    fun wpm(x: Int) {
        dit = ((60.0 / (x * 50.0) * 1000.0).roundToInt())
        dah = dit * 3

        dit += ATTACK
        dah += ATTACK
    }

    fun flash(ms: Int) {
        try {
            if (isDeviceHasCamera) {
                val cameraId = cameraManager.cameraIdList[0]
                cameraManager.setTorchMode(cameraId, true)
                runBlocking {
                    delay(ms.toLong())
                }
            }
        } catch (exp: CameraAccessException) {
            Timber.e("=======================================exp: CameraAccessException !!! ")
        }
    }

    fun pause(ms: Int) {
        try {
            if (isDeviceHasCamera) {
                val cameraId = cameraManager.cameraIdList[0]
                cameraManager.setTorchMode(cameraId, false)
                runBlocking {
                    delay(ms.toLong())
                }
            }
        } catch (exp: CameraAccessException) {
            Timber.e("=======================================exp: CameraAccessException !!! ")
        }
    }

    fun code(text: String): Int {
        val chars = text.toCharArray()
        var length = 100
        for (c in chars) {
            when (c) {
                '.' -> {
                    length += dit + dit
                }
                '-' -> {
                    length += dah + dit
                }
                ' ' -> {
                    length += dit * (SYMBOL_PAUSE - 1)
                }
                '|' -> {
                    length += dit * (WORD_PAUSE - 1)
                }
            }
        }

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

                isDeviceHasCamera = true

              job = GlobalScope.launch(Dispatchers.Default) {
                    try {
                        if (isDeviceHasCamera) {
                            pause(100)
                            for (c in chars) {
                                if (job!!.isActive) {
                                    when (c) {
                                        '.' -> {
                                            flash(dit)
                                            pause(dit)
                                        }
                                        '-' -> {
                                            flash(dah)
                                            pause(dit)
                                        }
                                        ' ' -> pause(dit * (SYMBOL_PAUSE - 1))

                                        '|' -> pause(dit * (WORD_PAUSE - 1))
                                    }
                                }
                            }
                        }
                    } catch (exp: CameraAccessException) {
                        Timber.e("===============================CameraAccessException")
                    }
                }
            }
        }
        return length
    }

//-------------
    private var buf: ByteArray? = null
    fun alarm() {
        GlobalScope.launch(Dispatchers.Default) {
            try {
                var from = 0
                val length = (50 + 50) * 3
                buf = ByteArray(Constants.SAMPLE_RATE * length / 1000)
                for (i in 0..2) {
                    from = tone(50, 220, from)
                    from = tone(50, 440, from)
                }
                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    Constants.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, buf!!.size,
                    AudioTrack.MODE_STATIC
                )

                audioTrack.write(buf!!, 0, buf!!.size)
                audioTrack.play()
                Thread.sleep(length.toLong())
                audioTrack.stop()
                audioTrack.release()

            } catch (t: Throwable) {
                Timber.e("playback failed")
            }
        }
    }
  private  fun tone(ms: Int, freq: Int, from: Int): Int {
        val length: Int = Constants.SAMPLE_RATE * ms / 1000
        val a: Int = Constants.SAMPLE_RATE * ATTACK / 1000
        val r = length - a
        for (i in 0 .. length - 1) {
            val period  = Constants.SAMPLE_RATE / freq
            val angle = (2.0 * Math.PI * i) / period
            var amp = 1.0F
            if (i < a) {
                amp = (i / a).toFloat()
            } else if (i > r) {
                amp = (1.0f - (i - r) / a)
            }
            buf!![from + i] = (Math.sin(angle) * amp * 127f).toByte()
        }
        return from + length
    }
}
