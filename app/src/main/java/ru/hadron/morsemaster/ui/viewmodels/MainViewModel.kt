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
import ru.hadron.morsemaster.util.Question
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

class MainViewModel @ViewModelInject constructor(
    val repository: DefaultRepository,
    application: Application
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    private val adv_level = 75
    private val adv_max = 3

    class AdvItem(
        var initSymbol: String
    ) {
        var symbol: String
        var shuffle: Double

        init {
            this.symbol = initSymbol
            this.shuffle = Math.random()
        }
    }

    private class AdvItemShuffle
        : Comparator<AdvItem> {
        override fun compare(a: AdvItem, b: AdvItem): Int {
            return if (a.shuffle < b.shuffle) 1
            else -1
        }
    }

    fun getCode(text: String): String {
        var res = ""
        for (c: Char in text.toCharArray()) {
            if (c.equals(" ")) {
                res += "|"
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    val code = repository.getStmCode(symbol = c.toString())
                    if (code.isNotEmpty()) {
                        res += "$code "
                    }
                }

            }
        }
        return res
    }

    fun loadLesson(info: String): Lesson? {
        var s: String? = null
        viewModelScope.launch(Dispatchers.IO) {
            s = repository.getStmSymbolsFromLesson(info = info)
            Timber.e("--------------------------------$s")
        }
        return if (symbols.isNotEmpty()) {
            Lesson(info = info, symbols = s?.split(" ").toString())
        } else {
            null
        }


    }

    fun clearStat() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStat()
        }
    }

    fun initStat(symbols: Array<String>) {

        for (symbol in symbols) {
            var stat: Stat  = Stat(
                symbol = symbol, 1, 1,
                lastseen = System.currentTimeMillis() / (30 * 1000)
            )

            stat.symbol = symbol
            stat.lastseen = System.currentTimeMillis() / (30 * 1000)

            viewModelScope.launch(Dispatchers.IO) {
                repository.insertStat(stat = stat)
            }
        }
    }

    fun updateStat(symbol: String, correct: Boolean) {
        if (correct) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateStatIfAnswerCorrect(
                    lastseen = System.currentTimeMillis() / (30*1000) - (Math.random() * 3.0f).toInt(), //?
                    symbol = symbol
                )
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateStatIfAnswerNotCorrect(
                    lastseen = System.currentTimeMillis() / (30*1000) - (Math.random() * 3.0f).toInt(),
                    symbol = symbol
                )
            }
        }
    }
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun getNextSymbol(remain: Int): Question {
        var rs: List<StatForStmNextSymbol> =
            viewModelScope.async(Dispatchers.IO) {
                repository.getStmNextSymbol(adv_level)
            }.getCompleted()

        if (remain > 1 && Math.random() > 0.5) {
            //rs.next
            //rs.next
            // to do smth
        }
        return Question(symbol = rs[0].symbol, correct = rs[0].correct) //?
        //return Question(symbol = "", correct = 1) //test
    }


    fun getCountAdv(): Int {
        val stm_count_adv: List<StatForStmCountAdv>
        val stm =  viewModelScope.async(Dispatchers.IO) {
            repository.getStmCountAdv(ratio = adv_level)
        }
        // stm_count_adv = stm.getCompleted()
        // return stm_count_adv.indexOf(0)  //?
        return 10
    }
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun getNextAdv(adv: Int): Question {
        var rs = StatForStmNextAdv("x", 0)

        runBlocking {
            val stm_next_adv =
                viewModelScope.async(Dispatchers.IO) {
                    repository.getStmNextAdv(adv_level)
                }
            runBlocking { rs = stm_next_adv.await() }
        }

        Timber.e("======>>> ${rs.symbol}")

        var items: Vector<AdvItem> = Vector<AdvItem>()
        var question = ""
        var min_ratio = 99

        rs.symbol.forEach {
            min_ratio = Math.min(min_ratio, rs.ratio)
            items.add(AdvItem(rs.symbol))
        }

        val count = 2 + (adv_max - 1) * (min_ratio - adv_level) / (100 - adv_level)

        if (items.size > count) {
            items = Vector(items.subList(0, count))
        } else {
            var ii = items.size - 1
            while (ii < count) {
                val item = items[ii % adv]
                items.add(AdvItem(item.symbol))
                ii++
            }
        }

        items.sortWith(AdvItemShuffle())

        for (i in 0 until count) question += items[i].symbol

        return Question(question, 999)
    }

    private val _lessons  = repository.getInfoFromLesson()//MutableLiveData<List<String>>()
    val lessons: LiveData<List<String>> get() = _lessons

    //------from ex lesson class --------------

    private var symbols = mutableListOf<String>()
    private var count = 0
    private var question: Question = Question("x", 0)

    fun getQuestion(): Question {
        val adv = this.getCountAdv()
        val remain = symbols.size - adv

        if (adv > 0 && (remain == 0 || count++ % (remain + 1) == 0)) {
            question = getNextAdv(adv)
            Timber.e("====question = getNextAdv(adv)=====$question")
        } else {
            question = getNextSymbol(remain)
            Timber.e("=====question = getNextSymbol(remain)=====$question")
        }
        return question
    }

    fun setAnswer(answer: String): Boolean {
        var correct = true
        for (i in 0 until Math.min(answer.length, question.length())) {
            val a = answer[i]
            val q = question.symbol[i]

            if (a == q) {
                this.updateStat(symbol = q.toString(), correct = true)

            } else {
                correct = false
                this.updateStat(symbol = a.toString(), correct = false)
            }
        }
        return correct
    }

    //-----
    val worth = repository.getStmWorth()

    fun insertStat(stat: Stat){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertStat(stat)
        }
    }

    ///----import cvcs files--------////
    fun insertLesson(lesson: Lesson) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCvsLesson(lesson)
        }
    }
    fun insertCodes(codes: Codes) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCvsCodes(codes)
        }
    }
    fun insertCodesGroup(codesGroup: CodesGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCvsCodesGroup(codesGroup)
        }
    }

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

/*    fun importCvsOpts() {
        val filePath = getFileFromAssets(context, "opts.cvs").absolutePath
    }*/

}

