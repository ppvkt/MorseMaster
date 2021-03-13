package ru.hadron.morsemaster.util

import android.R.attr
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.hadron.morsemaster.util.Constants.SAMPLE_RATE
import timber.log.Timber


class Sound {
    private var freq = 700  //Hz
    private var attack = 3 //ms
    private var dit = 100 // ms
    private var dah = 300 //ms
    private var symbol_pause = 3 // dits
    private var word_pause = 7 // dits
    lateinit var buf: ByteArray

   fun wpm(x: Int) {
       dit = Math.round(60.0 / (attr.x * 50.0) * 1000.0).toInt()
        dah = dit * 3

        dit += attack
        dah += attack
    }

    fun tone(ms: Int, freq: Int, from: Int): Int {
        val length: Int = SAMPLE_RATE * ms / 1000   /// 1000
        val a: Int = SAMPLE_RATE * attack / 1000 /// 1000
        val r = length - a
        for (i in 0 until length) {
            val period = SAMPLE_RATE / freq
            val angle = 2.0 * Math.PI * i / period
            var amp = 1.0
            if (i < a) {
                amp = i.toDouble() / a
            } else if (i > r) {
                amp = 1.0f - (i - r).toDouble() / a
            }
            buf[from + i] = (Math.sin(angle) * amp * 127f).toInt().toByte()
        }
        return from + length
    }

    fun pause(ms: Int, from: Int): Int {
        val length: Int = SAMPLE_RATE * ms / 1000
        for (i in 0 until length) {
            buf[from + i] = 0
        }
        return from + length
    }


    fun code(text: String): Int {
        val chars = text.toCharArray()
        var length = 100
        for (c in chars) {
            when (c) {
                '.' -> length += dit + dit
                '-' -> length += dah + dit
                ' ' -> length += dit * (symbol_pause - 1)
                '|' -> length += dit * (word_pause - 1)
            }
        }
        buf = ByteArray(SAMPLE_RATE * length / 1000)
        Timber.e("code from sound buf.size === ${buf.size}")
        runBlocking {
            GlobalScope.async(Dispatchers.IO) {
                var from: Int

                from = pause(100, 0)

                for (c in chars) {
                    when (c) {
                        '.' -> {
                            from = tone(dit, freq, from)
                            from = pause(dit, from)
                        }
                        '-' -> {
                            from = tone(dah, freq, from)
                            from = pause(dit, from)
                        }
                        ' ' -> from = pause(dit * (symbol_pause - 1), from)
                        '|' -> from = pause(dit * (word_pause - 1), from)


                        ',' -> Timber.e (" -----------------------------fuck comma!!!!")
                    }
                }
                // line.write(buf, 0, buf.length)
                var   audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, buf.size,
                    AudioTrack.MODE_STATIC
                )

                audioTrack.write(buf, 0, buf.size);
                audioTrack.play()
                Timber.e("code from sound")
            }.await()
        }

        return length
    }

    fun alarm() {
        runBlocking {
            GlobalScope.async(Dispatchers.IO) {
                var from = 0
                val length = (50 + 50) * 3
                buf = ByteArray(SAMPLE_RATE * length / 1000)
                for (i in 0..2) {
                    from = tone(50, 220, from)
                    from = tone(50, 440, from)  //??
                }
                //  line.write(buf, 0, buf.length)
                var  audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, buf.size,
                    AudioTrack.MODE_STATIC
                )

                audioTrack.write(buf, 0, buf.size);
                audioTrack.play()
                Timber.e("alarm from sound")
            }.await()
        }
    }
}