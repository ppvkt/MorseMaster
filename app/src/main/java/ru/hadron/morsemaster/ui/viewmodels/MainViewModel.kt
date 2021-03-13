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
import ru.hadron.morsemaster.db.entity.*
import ru.hadron.morsemaster.repositories.Storage
import ru.hadron.morsemaster.util.CurrentLesson
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

    private val _lessons  = storage.getInfoFromLesson()//MutableLiveData<List<String>>()
    val lessons: LiveData<List<String>> get() = _lessons

    val worth = storage.worth
    fun insertStat(stat: Stat) = storage.insertStat(stat = stat)
    fun clearStat() = storage.clearStat()
    //-----

    private val sound: Sound = Sound()

    fun getHelloSoundCode() {
        questionSymbol.postValue("get ready!")
        seconds = sound.code("...-...-...-")
    }


    private fun setWpmValue () {
        sound.wpm(_speedName.toInt())
        Timber.e("speed namem to int = = = = = ${_speedName.toInt()}")
    }

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
    fun startLessonTask() {
/*        viewModelScope.launch {
            Timber.e(" ----------------------------")
            while (true) {
                LessonTask().run()
            }
        }*/
        LessonTask().run()
    }
    //---------

    lateinit var question: Question
    lateinit var currentLesson: CurrentLesson
    lateinit var answer_buf: String
    private val help_wait = 3
    private var question_wait = 1  //0

    val questionSymbol: MutableLiveData<String> = MutableLiveData()
    val isBackgroundChange: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isBackgroundChange.postValue(false)

        questionSymbol.postValue("get ready!")
       sound.code("...-...-...-")
    }


    //mappingLessonToCurrentLesson
    fun loadLesson() {
        storage.setAdvLevel(_levelName)
        storage.setAdvMax(_maxcharName)

        Timber.e(" ==============current _lesson name is ... $_lessonName ==========")
        val lesson = storage.loadLesson(_lessonName)

        currentLesson = CurrentLesson(storage = storage, lesson?.symbols)
        Timber.e(" ==============current lesson hashe in view model===> ${currentLesson.hashCode()}")
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
    }

//---------------------------------------

    private var seconds = _repeatName * 1
    inner class LessonTask : TimerTask() {
        override fun run() {
            Timber.e("inside run!!!!")
            question = currentLesson.getQuestion()
            answer_buf = ""

            var help = false
            if (question._correct <= 3) { help = true }

            Timber.e("---help ---- > $help")

            isBackgroundChange.postValue(true)

            seconds = playQuestion(_repeatName)

            if (help) {
                questionSymbol.postValue(question._symbol)
                Timber.e(" -----question symbol----live data-----> ${questionSymbol.value}")
                startTimer((seconds + help_wait)*1000.toLong())
            } else {
                questionSymbol.postValue(question.getSecret(""))
                if (question_wait > 0) {
                    startTimer((seconds + question_wait) * 1000.toLong())
                }
            }
        }
    }


    lateinit var timer: Timer
    private var isTimerRun: Boolean = false

    private fun startTimer(currDelay: Long) {
        timer  = Timer()
        timer.schedule(LessonTask(), currDelay)
        isTimerRun = true
    }
    fun startTimerFromFragment() {
        setWpmValue()
        startTimer((seconds*1000 + 1000).toLong())
      //
       // getHelloSoundCode()

    }

    fun stopTimerFromFragment() {
        stopTimer()
        timer.purge()
    }

    private fun stopTimer() {
        timer.cancel()
        isTimerRun = false
    }

    fun playQuestion(x: Int): Int {
        var q = storage.getCode(question.symbol)

        Timber.e("   var q = storage.getCode(question.symbol) =====> ${q}")
        var code = q
        if (question.length() > 1) {
            for (i in 1 until x step 1) {
                code+="|" + q
            }
        }
        return sound.code(code)
    }

    ///----
    private fun keyTyped() {
        if (question == null) return
        var key = curranswer
        answer_buf += key
        questionSymbol.postValue(question.getSecret(answer_buf))   // typed?
        Timber.e("----question.getSecret(answer_buf)----- ${question.getSecret(answer_buf)}")

        if (answer_buf.length == question.length()) {
            timer.cancel()

            if (currentLesson.setAnswer(answer_buf)) {
                startTimer(100)
            } else {
                questionSymbol.postValue(question.symbol)
                isBackgroundChange.postValue(false)
                sound.alarm()
                startTimer((help_wait * 1000).toLong())
            }
        }
    }

    lateinit var curranswer: String
    fun setAnswer(answer: String) {
        curranswer = answer
        keyTyped()
    }
}

