package ru.hadron.morsemaster.ui.fragments

import android.graphics.Color
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
import ru.hadron.morsemaster.db.entity.Stat
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import timber.log.Timber

@AndroidEntryPoint
class MorseFragment : Fragment(R.layout.fragment_morse) {

    private val viewModel: MainViewModel by viewModels()

    val args: MorseFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.subscribeToObservers()

        sendDataInViewModel()


        viewModel.loadLesson()
     //   viewModel.startTimerFromFragment()
        viewModel.startLessonTask()

        btnStop.setOnClickListener {
            //todo
            // stopTimer()
            viewModel.stopTimer()

            findNavController().navigate(R.id.action_morseFragment_to_settingsFragment)
        }
    }


    fun subscribeToObservers() {
        viewModel.worth?.observe(viewLifecycleOwner, Observer {
            it.let {
                tvWorth.text = "worth : $it %"
                if (it == null) {
                    tvWorth.text = "worth : 0 %"
                }
            }
        })

        viewModel.isBackgroundChange?.observe(viewLifecycleOwner, Observer {
            it.let {if (it == true) {
                tvShowingChar.setBackgroundColor(Color.DKGRAY)
            } else {
                tvShowingChar.setBackgroundColor(Color.RED)
            }
            }
        })

        viewModel.questionSymbol?.observe(viewLifecycleOwner, Observer {
            it.let {
                tvShowingChar.text = it
                if (it == null) {
                    tvShowingChar.text = "--null--"
                }
            }
        })
    }

    private fun sendDataInViewModel() {
        viewModel.setDataName(
            lessonName = args.lessonName,
            speedName = args.speedName,
            timeoutName = args.timeoutName,
            levelName = args.levelName,
            maxcharName = args.maxcharName,
            repeatName = args.repeatName
        )

        Timber.e("send data in view model...")
    }
}

