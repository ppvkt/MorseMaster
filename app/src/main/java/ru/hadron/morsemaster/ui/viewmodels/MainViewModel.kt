package ru.hadron.morsemaster.ui.viewmodels

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.hadron.morsemaster.db.entity.*
import ru.hadron.morsemaster.repositories.DefaultRepository
import ru.hadron.morsemaster.repositories.Storage
import ru.hadron.morsemaster.util.Question
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.sql.SQLException
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
}

