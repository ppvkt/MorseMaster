package ru.hadron.morsemaster.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.hadron.morsemaster.db.entity.Lesson
import ru.hadron.morsemaster.db.entity.Stat
import ru.hadron.morsemaster.db.entity.StatForStmCountAdv
import ru.hadron.morsemaster.db.entity.StatForStmNextSymbol
import ru.hadron.morsemaster.repositories.DefaultRepository
import ru.hadron.morsemaster.util.Question
import timber.log.Timber
import java.util.*

class MainViewModel @ViewModelInject constructor(
    val repository: DefaultRepository
) : ViewModel() {

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
                symbol = symbol, 0, 0,
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
        stm_count_adv = stm.getCompleted()
        return stm_count_adv.indexOf(0)  //?
    }

    fun getNextAdv(adv: Int): Question {
        val rs =
             viewModelScope.async(Dispatchers.IO) {
                 repository.getStmNextAdv(adv_level)
             }.getCompleted()
        var items: Vector<AdvItem> = Vector<AdvItem>()
        var question = ""
        var min_ratio = 99

        //while ()
        min_ratio = Math.min(min_ratio, rs.ratio)
        items.add(AdvItem(rs.symbol))

        val count = 2 + (adv_max - 1) * (min_ratio - adv_level) / (100 - adv_level)

        if (items.size > count) {
            items = Vector(items.subList(0, count))
        } else {
            for (i in items.size until count) {
                val item = items[i % adv]
                items.add(AdvItem(item.symbol))
            }
        }

        items.sortWith(AdvItemShuffle())

        for (i in 0 until count) question += items[i].symbol

        return Question(question, 999)
    }

    fun getLessons(): String? {           //?
        return repository.getInfoFromLesson().value
    }

    //------from ex lesson class --------------

    lateinit var symbols: Array<String>
    private var count = 0
    private var question: Question = Question("", 0)

    fun getQuestion(): Question {
        val adv = this.getCountAdv()
        val remain = symbols.size - adv

        if (adv > 0 && (remain == 0 || count++ % (remain + 1) == 0)) {
            question = getNextAdv(adv)
        } else {
            question= getNextSymbol(remain)
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

}

