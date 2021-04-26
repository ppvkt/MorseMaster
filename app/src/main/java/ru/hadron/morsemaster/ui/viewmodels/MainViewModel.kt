package ru.hadron.morsemaster.ui.viewmodels

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import ru.hadron.morsemaster.AppLifecycleObserver
import ru.hadron.morsemaster.db.entity.*
import ru.hadron.morsemaster.repositories.Storage
import ru.hadron.morsemaster.util.CurrentLesson
import ru.hadron.morsemaster.util.FlashLight
import ru.hadron.morsemaster.util.Question
import ru.hadron.morsemaster.util.Sound
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

open class MainViewModel @ViewModelInject constructor(

    val  storage: Storage,
    application: Application
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    private val _lessons  = storage.getInfoFromLesson()
    val lessons: LiveData<List<String>> get() = _lessons

    val worth = storage.getWorth()
    fun insertStat(stat: Stat) = storage.insertStat(stat = stat)
    fun clearStat() = storage.clearStat()
    //-----

    private val sound: Sound = Sound()
    private val flashLight: FlashLight = FlashLight(context = context)

    ///----import cvcs files--------//// это остается во VM
    fun insertLesson(lesson: Lesson) = storage.insertCvsLesson(lesson)
    fun insertCodes(codes: Codes) = storage.insertCvsCodes(codes)
    fun insertCodesGroup(codesGroup: CodesGroup) = storage.insertCvsCodesGroup(codesGroup)

    private val tsvReader = csvReader {
        //  charset = "ISO_8859_1"
        quoteChar = '\t'
        delimiter = '\t'
        escapeChar = '\\'
        skipEmptyLine = true
    }

    @Throws(IOException::class)
    private fun getFileFromAssets(context: Context, fileName: String): File =
        File(context.cacheDir, fileName)
            .also {
                if (!it.exists()) {
                    it.outputStream().use { cache ->
                        context.assets.open(fileName).use { inputStream ->
                            inputStream.copyTo(cache)
                        }
                    }
                }
            }

    @RequiresApi(Build.VERSION_CODES.N)
    fun importCvsLesson() {
        val filePath = getFileFromAssets(context, "lesson.cvs").absolutePath

        tsvReader.open(filePath) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                Timber.e("row  = ${row}  ")
                val lesson = Lesson(info = row.getOrDefault("info", ""), symbols = row.getOrDefault("symbols", ""))
                Timber.e("======${lesson.hashCode()}")
                insertLesson(lesson)

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun importCvsCodes() {
        val filePath = getFileFromAssets(context, "codes.cvs").absolutePath
        tsvReader.open(filePath) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                Timber.e("row  = ${row}  ")
                val codes = Codes(
                    group_id = row.getOrDefault("group_id",""),
                    symbol = row.getOrDefault("symbol", ""),
                    code = row.getOrDefault("code", ""))

                insertCodes(codes)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun importCvsCodesGroup() {
        val filePath = getFileFromAssets(context, "codes_group.cvs").absolutePath

        tsvReader.open(filePath) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                Timber.e("row  = ${row}  ")
                val codesGroup = CodesGroup(id = row.getOrDefault("id",""), info = row.getOrDefault("info", ""))
                Timber.e("======${codesGroup.hashCode()}")
                insertCodesGroup(codesGroup)
            }
        }
    }

    //-------

    lateinit var question: Question
    lateinit var currentLesson: CurrentLesson
    var answer_buf: String = ""
    private val help_wait = 3000 //ms
    private var question_wait = 0 //ms
    private var timeout_items = arrayOf(0, 1000, 2000, 3000)

    val questionSymbol: MutableLiveData<String> = MutableLiveData()
    val isBackgroundChange: MutableLiveData<Int> = MutableLiveData()
    val currentMorseCode: MutableLiveData<String> = MutableLiveData()
    val coutShowedSymbols: MutableLiveData<String> = MutableLiveData()


    /*
    1 gray
    2 red
    3 green
     */
    init {
        isBackgroundChange.postValue(1)
        currentMorseCode.postValue("...- ...- ...-")
    }

    //mappingLessonToCurrentLesson
    fun loadLesson() {
        val lesson = storage.loadLesson(_lessonName)
        currentLesson = CurrentLesson(storage = storage, lesson?.symbols)

        if (currentLesson != null) {
            currentLesson.initStat()
        }
    }

    private var _lessonName: String = ""
    private var _speedName: String = ""
    private var _timeoutName: String = ""
    private var _levelName: Int = 40
    private var _maxcharName: Int = 1
    private var _repeatName: Int = 1
    //public
    fun  setDataName (lessonName: String,
                      speedName: String,
                      timeoutName: String,
                      levelName: Int,
                      maxcharName: Int,
                      repeatName: Int
    ) {
        _lessonName = lessonName
        _speedName = speedName
        _timeoutName = timeoutName
        _levelName = levelName
        _maxcharName = maxcharName
        _repeatName = repeatName

        storage.setAdvLevel(_levelName)
        storage.setAdvMax(_maxcharName)

        sound.wpm(_speedName.toInt())
        flashLight.wpm(_speedName.toInt())

        var index = 0
        when (_timeoutName) {
            "Forever" -> index = 0
            "1 sec" -> index = 1
            "2 sec" -> index = 2
            "3 sec" -> index = 3
        }
        question_wait = timeout_items[index]
    }

    //---------------------------------------
    private var count = 1
    inner class LessonTask : TimerTask() {

        private fun doLogic() {
            question = currentLesson.getQuestion()
            answer_buf = ""

            var help = false
            if (question._correct <= 3) { help = true }

            isBackgroundChange.postValue(1)
            val _isSound = isSound
            val _isFlash = isFlash

            if (_isSound && !_isFlash) {
                Timber.e("is sound ========$isSound")
                Timber.e("is flash =========$isFlash")
                Timber.e("has camera? =======${FlashLight.isDeviceHasCamera} ")
                var  ms = playQuestion(_repeatName)
                currentMorseCode.postValue(storage.getCode(question._symbol))

                if (help) {
                    questionSymbol.postValue(question._symbol)
                    if (question_wait > 0) {
                        startTimer(ms + help_wait)
                    }
                } else {
                    questionSymbol.postValue("(mask) "+ question.getSecret(""))
                    if (question_wait > 0) {
                        startTimer(ms + question_wait)
                    }
                }
                coutShowedSymbols.postValue("count : $count")
                count++
            }
            if (_isSound && _isFlash && FlashLight.isDeviceHasCamera) {
                var  msSound = playQuestion(_repeatName)
                var msFlash = lightQuestion(_repeatName)

                Timber.e("is sound ========$isSound")
                Timber.e("is flash =========$isFlash")
                Timber.e("has camera? =======${FlashLight.isDeviceHasCamera} ")
                Timber.e("check length light = ${msFlash} and play = $msSound")
                currentMorseCode.postValue(storage.getCode(question._symbol))

                if (help) {
                    questionSymbol.postValue(question._symbol)
                    if (question_wait > 0) {
                        startTimer(msSound + help_wait)
                    }
                } else {
                    questionSymbol.postValue("(mask) "+ question.getSecret(""))
                    if (question_wait > 0) {
                        startTimer(msSound + question_wait)
                    }
                }
                coutShowedSymbols.postValue("count : $count")
                count++
            }
            if (_isSound && _isFlash && !FlashLight.isDeviceHasCamera) {
                var  ms = playQuestion(_repeatName)
                currentMorseCode.postValue(storage.getCode(question._symbol))

                if (help) {
                    questionSymbol.postValue(question._symbol)
                    if (question_wait > 0) {
                        startTimer(ms + help_wait)
                    }
                } else {
                    questionSymbol.postValue("(mask) "+ question.getSecret(""))
                    if (question_wait > 0) {
                        startTimer(ms + question_wait)
                    }
                }
                coutShowedSymbols.postValue("count : $count")
                count++
            }
            if (!_isSound && _isFlash && FlashLight.isDeviceHasCamera) {
                var ms = lightQuestion(_repeatName)
                currentMorseCode.postValue(storage.getCode(question._symbol))

                if (help) {
                    questionSymbol.postValue(question._symbol)
                    if (question_wait > 0) {
                        startTimer(ms + help_wait)
                    }
                } else {
                    questionSymbol.postValue("(mask) "+ question.getSecret(""))
                    if (question_wait > 0) {
                        startTimer(ms + question_wait)
                    }
                }
                coutShowedSymbols.postValue("count : $count")
                count++
            }
            if (!_isSound && _isFlash && !FlashLight.isDeviceHasCamera) {

                var  ms = playQuestion(_repeatName)
                currentMorseCode.postValue(storage.getCode(question._symbol))

                if (help) {
                    questionSymbol.postValue(question._symbol)
                    if (question_wait > 0) {
                        startTimer(ms + help_wait)
                    }
                } else {
                    questionSymbol.postValue("(mask) "+ question.getSecret(""))
                    if (question_wait > 0) {
                        startTimer(ms + question_wait)
                    }
                }
                coutShowedSymbols.postValue("count : $count")
                count++
            }
        }

        override fun run() {

            if (AppLifecycleObserver.isMoveToBackground) {
                isStopButtonClicked = true
            }
          /*  if (!AppLifecycleListener.isMoveToBackground) {
                isStopButtonClicked = false
                //goto setting screen
            }
*/

            if (isStopButtonClicked) {
                timer.cancel()
                timer.purge()
                Timber.e(" if (isStopButtonClicked) return.....")
                return
            }

            doLogic()
        }

    }

    lateinit var timer: Timer
    private var isTimerRun: Boolean = false

    fun startTimer(currDelay: Int) {
        timer  = Timer()
        timer.schedule(LessonTask(), currDelay.toLong())
        isTimerRun = true
    }
    fun startTimerFromFragment() {
        questionSymbol.postValue("get ready!")
        sound.wpm(_speedName.toInt())
        flashLight.wpm(_speedName.toInt())
        helloMs = sound.code("...-...-...-")
        flashLight.code("...-...-...-")
    }

    private fun stopTimer() {
        timer.cancel()
        timer.purge()
        isTimerRun = false
    }

    fun playQuestion(x: Int): Int {
        var q = storage.getCode(question._symbol)
        var code = q
        if (question.length() > 0) {
            for (i in 1 until x step 1) {
                //code += "|" + q
                val prepended = "|${q}"
                code += prepended
            }
        }
        Timber.e("   ==================code q =====>${code}")
        return sound.code(code)
    }

    fun lightQuestion(x: Int): Int {
        var q = storage.getCode(question._symbol)
        var code = q
        if (question.length() > 0) {
            for (i in 1 until x step 1) {
                val prepended = "|${q}"
                code += prepended
            }
        }
        return flashLight.code(code)
    }

    ///----
    private fun keyTyped(): Unit {
        //if (question.equals(null)) return

        if (isStopButtonClicked) return
        if(curranswer == "repeat") {
            if (isSound && !isFlash) {
                playQuestion(1)
            }
            if (isSound && isFlash && FlashLight.isDeviceHasCamera) {
                playQuestion(1)
                lightQuestion(1)
            }
            if (isSound && isFlash && !FlashLight.isDeviceHasCamera) {
                playQuestion(1)
            }
            if (!isSound && isFlash && FlashLight.isDeviceHasCamera) {
                lightQuestion(1)
            }
            if (!isSound && isFlash && !FlashLight.isDeviceHasCamera) {
                playQuestion(1)
            }
            return
        }

        var key = curranswer
        if (isBackgroundChange.value == 1) {
            answer_buf += key
        }
        questionSymbol.postValue(question.getSecret(answer_buf))   // typed?

        if (answer_buf.length == question.length()) {
            stopTimer()
            if (currentLesson.setAnswer(answer_buf)) {
                isBackgroundChange.postValue(3)
                startTimer(100)
            } else {
                questionSymbol.postValue(question.symbol)
                isBackgroundChange.postValue(2)
                sound.alarm()
                startTimer(help_wait)
            }
        }
    }

    var curranswer: String = ""
    fun setAnswer(answer: String) {
        curranswer = answer
        keyTyped()
    }

    var isStopButtonClicked = false
    fun whenStopBtnClickedPassTrue( ) {
        this.isStopButtonClicked = true
    }

    fun whenStopBtnClickedPassFalse( ) {
        this.isStopButtonClicked = false
    }

    var helloMs = 0

    private var isSound = true
    private var isFlash = false
    fun whenSwitchSoundClicked(state: Boolean) {
        isSound = state
    }
    fun whenSwitchLightClicked(state: Boolean) {
        isFlash = state
    }
}
