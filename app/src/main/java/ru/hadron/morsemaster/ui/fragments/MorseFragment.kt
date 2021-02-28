package ru.hadron.morsemaster.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_morse.*
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.db.StorageDao
import ru.hadron.morsemaster.db.entity.Stat
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import ru.hadron.morsemaster.util.Constants.MORSE_DATABASE_NAME
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MorseFragment : Fragment(R.layout.fragment_morse) {

    private val viewModel: MainViewModel by viewModels()

val args: MorseFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.insertStat(Stat("q", 10, 1, 2131232))
        viewModel.insertStat(Stat("asdfasd", 100, 1, 21345646532))

        this.subscribeToObservers()

        runLessonTask()

        btnStop.setOnClickListener {
            //todo
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

    private var answer_buf = ""
    private var question_wait = 0
    fun runLessonTask() {
        var question = viewModel.getQuestion()
        var help: Boolean = question.correct <= 3
        // int ms... from playQuestion
        var ms  = 1000
        if (help) {
            tvShowingChar.text = question.symbol
            //startTimer
        } else {
            tvShowingChar.text = question.getSecret("")
            if (question_wait > 0) {
                //startTimer
            }
        }
    }
}