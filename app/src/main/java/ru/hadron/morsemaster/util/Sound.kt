package ru.hadron.morsemaster.util

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.*
import ru.hadron.morsemaster.util.Constants.ATTACK
import ru.hadron.morsemaster.util.Constants.FREQ
import ru.hadron.morsemaster.util.Constants.SAMPLE_RATE
import ru.hadron.morsemaster.util.Constants.SYMBOL_PAUSE
import ru.hadron.morsemaster.util.Constants.WORD_PAUSE
import timber.log.Timber
import java.lang.Thread.sleep
import kotlin.math.roundToInt
import kotlin.math.sin

class Sound {

    private var dit = 100 // ms
    private var dah = 300 //ms
    private var buf: ByteArray? = null

    fun wpm(x: Int) {
        dit = ((60.0 / (x * 50.0) * 1000.0).roundToInt())
        dah = dit * 3

        dit += ATTACK
        dah += ATTACK
    }

    fun tone(ms: Int, freq: Int, from: Int): Int {
        val length: Int = SAMPLE_RATE * ms / 1000
        val a: Int = SAMPLE_RATE * ATTACK / 1000
        val r = length - a

        for (i in 0 .. length - 1) {
            val period: Double  = (SAMPLE_RATE / freq).toDouble()
            // val period = SAMPLE_RATE.toDouble() / freq //низкочастотный звук если частота меньше 1000

            val angle = 2.0 * Math.PI * i / period
            var amp: Double = 1.0  //f?
            if (i < a) {
              //  amp = (i / a).toFloat()
                amp = (i / a).toDouble()
            } else if (i > r) {
                amp = (1.0f - ((i - r) / a).toDouble())
            }
            buf!![from + i] = (sin(angle) * amp * 127f).toByte()
        }
        return from + length
    }

    fun pause(ms: Int, from: Int): Int {
        val length: Int = SAMPLE_RATE * ms / 1000
        for (i in 0 until length) {
            buf?.set(from + i, 0)
        }
        return from + length
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
        buf = ByteArray(SAMPLE_RATE * length / 1000)

        GlobalScope.launch(Dispatchers.Default) {
            try {
                var from: Int
                from = pause(100, 0)

                for (c in chars) {
                    when (c) {
                        '.' -> {
                            from = tone(dit, FREQ, from)
                            from = pause(dit, from)
                        }
                        '-' -> {
                            from = tone(dah, FREQ, from)
                            from = pause(dit, from)
                        }
                        ' ' -> from = pause(dit * (SYMBOL_PAUSE - 1), from)

                        '|' -> from = pause(dit * (WORD_PAUSE - 1), from)
                    }
                }
                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT, buf!!.size,
                    AudioTrack.MODE_STATIC
                )

                Timber.e("--------audioTrack.state-------${audioTrack.state}")
                audioTrack.write(buf!!, 0, buf!!.size)
                audioTrack.play()
                counttest++
                sleep(length.toLong())
                audioTrack.stop()
                audioTrack.release()

                Timber.e("code from sound $counttest")

            } catch (t: Throwable) {
                Timber.e("playback failed")
            }
        }
        return length
    }

    private var counttest = 0
    fun alarm() {
        GlobalScope.launch(Dispatchers.Default) {
            try {
                var from = 0
                val length = (50 + 50) * 3
                buf = ByteArray(SAMPLE_RATE * length / 1000)
                for (i in 0..2) {
                    from = tone(50, 220, from)
                    from = tone(50, 440, from)
                }
                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, buf!!.size,
                    AudioTrack.MODE_STATIC
                )

                audioTrack.write(buf!!, 0, buf!!.size)
                audioTrack.play()
                counttest++
                sleep(length.toLong())
                audioTrack.stop()
                audioTrack.release()
                Timber.e("alarm from sound $counttest")

            } catch (t: Throwable) {
                Timber.e("playback failed")
            }
        }
    }
}