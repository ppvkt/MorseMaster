package ru.hadron.morsemaster.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_morse.*
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.db.entity.Lesson
import ru.hadron.morsemaster.db.entity.Stat
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class MorseFragment : Fragment(R.layout.fragment_morse) {

    private val viewModel: MainViewModel by viewModels()

    val args: MorseFragmentArgs by navArgs()

    init {
        //  viewModel.initStat()


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadLesson(args.lessonName)

        Timber.e("lesson name ====> ${args.lessonName}")
        Timber.e("lesson symbols loaded ====> ${currentLessonSymbols}")

        //viewModel.insertStat(Stat("q", 10, 1, 2131232))
        //   viewModel.insertStat(Stat("asdfasd", 100, 1, 21345646532))

        this.subscribeToObservers()


        var ms = 500L
        startTimer(ms + 1000L)

        btnStop.setOnClickListener {
            //todo
            // stopTimer()

            findNavController().navigate(R.id.action_morseFragment_to_settingsFragment)
        }
    }

    fun subscribeToObservers() {
        viewModel.worth?.observe(viewLifecycleOwner, Observer {
            it.let {
                tvWorth.text = "worth : $it %"
            }
        })
    }

    // var  lesson: Lesson = Lesson("test", "test")
    lateinit var currentLessonSymbols: String
    fun loadLesson(info: String) {
        var lesson = viewModel.loadLesson(info)!!
        currentLessonSymbols = lesson.symbols

        Timber.e("======currentLessonSymbols ===>${currentLessonSymbols}")
        viewModel.initStat(currentLessonSymbols)

        /*   if (lesson != null) {

           }*/
    }



    fun runLessonTask() {


    }

    lateinit var timer: Timer

    private fun startTimer(currentDelay: Long) {
        /*  runBlocking {
              GlobalScope.launch(Dispatchers.IO) {
                  delay(currentDelay)
                 // runLessonTask()
                  LessonTask().run()
                  withContext(Dispatchers.Main) {
                      Toast.makeText(activity,"timer end", Toast.LENGTH_SHORT).show()
                  }
              }
          }*/

        Timer().schedule(LessonTask(), currentDelay)

        Toast.makeText(activity,"timer end", Toast.LENGTH_SHORT).show()

    }

    private var answer_buf: String = ""
    private var question_wait: Long = 0
    private var help_wait: Long = 0

    inner class LessonTask : TimerTask() {
        override fun run() {
            answer_buf = ""
            question_wait = 1000L
            help_wait = 3000L
            var question = viewModel.getQuestion()

            var help: Boolean = question.correct <= 3
            Timber.e("help ====> ${help}")
            // int ms... from playQuestion
            Timber.e("args.repeatName====> ${args.repeatName}")
            var ms  = 1000L

            if (help) {
                tvShowingChar.text = question.symbol
                //startTimer
                startTimer(ms + help_wait)
            } else {
                tvShowingChar.text = question.getSecret("")
                if (question_wait > 0) {
                    //startTimer
                    startTimer(ms + question_wait)
                }
            }
        }

    }
}

