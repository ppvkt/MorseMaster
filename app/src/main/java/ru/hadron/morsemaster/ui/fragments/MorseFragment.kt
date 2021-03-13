package ru.hadron.morsemaster.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_morse.*
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import timber.log.Timber

@AndroidEntryPoint
class MorseFragment : Fragment(R.layout.fragment_morse) , View.OnClickListener {

    private val viewModel: MainViewModel by viewModels()

    val args: MorseFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.subscribeToObservers()

        sendDataInViewModel()


        viewModel.loadLesson()
        viewModel.startTimerFromFragment()
        viewModel.startLessonTask()

        this.setOnClickListenersToAllBtnChar()



        btnStop.setOnClickListener {
            //todo
            // stopTimer()
            viewModel.stopTimerFromFragment()
            findNavController().navigate(R.id.action_morseFragment_to_settingsFragment)

        }
    }


    fun subscribeToObservers() {
        viewModel.worth?.observe(viewLifecycleOwner, Observer {
            it.let {
                tvWorth.text = "worth : $it %"
                if (it == null) {
                    tvWorth.text = "worth :  - %"
                }
            }
        })

        viewModel.isBackgroundChange?.observe(viewLifecycleOwner, Observer {
            it.let {if (it == true) {
                tvShowingChar.setBackgroundColor(Color.DKGRAY)
            } else {
                tvShowingChar.setBackgroundColor(Color.RED)
                //block typed
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

    lateinit var answer: String

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnChar1 -> {
                answer = btnChar1.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnChar2 -> {
                answer = btnChar2.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnChar3 -> {
                answer = btnChar3.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnChar4 -> {
                answer = btnChar4.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnChar5 -> {
                answer = btnChar5.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnChar6 -> {
                answer = btnChar6.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnChar7 -> {
                answer = btnChar7.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnChar8 -> {
                answer = btnChar8.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnChar9 -> {
                answer = btnChar9.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharNull -> {
                answer = btnCharNull.text.toString()
                viewModel.setAnswer(answer = answer)
            }

            R.id.btnCharQ -> {
                answer = btnCharQ.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharW -> {
                answer = btnCharW.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharE -> {
                answer = btnCharE.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharR -> {
                answer = btnCharR.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharT -> {
                answer = btnCharT.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharY -> {
                answer = btnCharY.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharU -> {
                answer = btnCharU.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharI ->{
                answer = btnCharI.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharO -> {
                answer = btnCharO.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharP -> {
                answer = btnCharP.text.toString()
                viewModel.setAnswer(answer = answer)
            }

            R.id.btnCharA -> {
                answer = btnCharA.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharS -> {
                answer = btnCharS.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharD -> {
                answer = btnCharD.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharF -> {
                answer = btnCharF.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharG -> {
                answer = btnCharG.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharH -> {
                answer = btnCharH.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharJ -> {
                answer = btnCharJ.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharK -> {
                answer = btnCharK.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharL -> {
                answer = btnCharL.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharEqually -> {
                answer = btnCharEqually.text.toString()
                viewModel.setAnswer(answer = answer)
            }

            R.id.btnCharZ -> {
                answer = btnCharZ.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharX -> {
                answer = btnCharX.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharC -> {
                answer = btnCharC.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharV -> {
                answer = btnCharV.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharB -> {
                answer = btnCharB.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharN -> {
                answer = btnCharN.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharM -> {
                answer = btnCharM.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharComma -> {
                answer = btnCharComma.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharDot -> {
                answer = btnCharDot.text.toString()
                viewModel.setAnswer(answer = answer)
            }
            R.id.btnCharQuestionMark -> {
                answer = btnCharQuestionMark.text.toString()
                viewModel.setAnswer(answer = answer)
            }
        }
    }

  private  fun setOnClickListenersToAllBtnChar() {
        btnChar1.setOnClickListener(this)
        btnChar2.setOnClickListener(this)
        btnChar3.setOnClickListener(this)
        btnChar4.setOnClickListener(this)
        btnChar5.setOnClickListener(this)
        btnChar6.setOnClickListener(this)
        btnChar7.setOnClickListener(this)
        btnChar8.setOnClickListener(this)
        btnChar9.setOnClickListener(this)
        btnCharNull.setOnClickListener(this)

        btnCharQ.setOnClickListener(this)
        btnCharW.setOnClickListener(this)
        btnCharE.setOnClickListener(this)
        btnCharR.setOnClickListener(this)
        btnCharT.setOnClickListener(this)
        btnCharY.setOnClickListener(this)
        btnCharU.setOnClickListener(this)
        btnCharI.setOnClickListener(this)
        btnCharO.setOnClickListener(this)
        btnCharP.setOnClickListener(this)

        btnCharA.setOnClickListener(this)
        btnCharS.setOnClickListener(this)
        btnCharD.setOnClickListener(this)
        btnCharF.setOnClickListener(this)
        btnCharG.setOnClickListener(this)
        btnCharH.setOnClickListener(this)
        btnCharJ.setOnClickListener(this)
        btnCharK.setOnClickListener(this)
        btnCharL.setOnClickListener(this)
        btnCharEqually.setOnClickListener(this)

        btnCharZ.setOnClickListener(this)
        btnCharX.setOnClickListener(this)
        btnCharC.setOnClickListener(this)
        btnCharV.setOnClickListener(this)
        btnCharB.setOnClickListener(this)
        btnCharN.setOnClickListener(this)
        btnCharM.setOnClickListener(this)
        btnCharComma.setOnClickListener(this)
        btnCharDot.setOnClickListener(this)
        btnCharQuestionMark.setOnClickListener(this)
    }
}

