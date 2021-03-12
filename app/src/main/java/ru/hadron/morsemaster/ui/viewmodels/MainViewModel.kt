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
                val codes = Codes(group_id = row.getOrDefault("group_id",""), symbol = row.getOrDefault("symbol", ""), code = row.getOrDefault("code", ""))
                Timber.e("======${codes.hashCode()}")
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
    private val help_wait = 3000L
    private var question_wait = 0

    val questionSymbol: MutableLiveData<String> = MutableLiveData()
    val isBackgroundChange: MutableLiveData<Boolean> = MutableLiveData()

    init {
       // questionSymbol.postValue("start")
        isBackgroundChange.postValue(false)
    }


//mappingLessonToCurrentLesson
    fun loadLesson() {
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

    private var ms = _repeatName * 1000L
    inner class LessonTask : TimerTask() {
        override fun run() {

            Timber.e("inside run!!!!")
            question = currentLesson.getQuestion()
            answer_buf = ""

            var help: Boolean = question.correct <= 3

            Timber.e("---help ---- > $help")

            isBackgroundChange.postValue(true)

//
          //  var ms = _repeatName * 1000L

            Timber.e("---ms ---- > $ms")

            if (help) {
                //change text???  question.symbol put in livedata
                questionSymbol.postValue(question.symbol)
                Timber.e(" -----question symbol----live data-----> ${questionSymbol.value}")
                startTimer(ms + help_wait)
            } else {
                //getsecret
                questionSymbol.postValue(question.getSecret(""))

                if (question_wait > 0) {
                    startTimer(ms + question_wait)
                }
            }
        }
    }

    private var timer: Timer = Timer()
   private fun startTimer(currDelay: Long) {
        //timer = Timer()
        timer.schedule(LessonTask(), currDelay)
    }
    fun startTimerFromFragment() {
        //info_label.setText("Get ready!");
        //int ms = sound.code("...- ...- ...-");
        startTimer(ms + 1000L)
    }


    fun stopTimer() {
        if (timer != null) {
            timer.cancel()
        }
    }

    fun playQuestion(x: Int): Int {
        var q = storage.getCode(question.symbol)
        var code = q
        if (question.length() > 1) {
            for (i in 1 until x step 1) {
                code+="|" + q
            }
        }
        return 100  //sound.code(code)
    }
    fun keyTyped() {
        if (question == null) return

        //		if (key == ' ') {
        //			playQuestion(1);
        //			return;
        //		}


    }

}

