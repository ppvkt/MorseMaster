package ru.hadron.morsemaster.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_morse.*
import ru.hadron.morsemaster.AppLifecycleObserver
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import ru.hadron.morsemaster.util.FlashLight
import timber.log.Timber
import java.lang.Integer.parseInt

@AndroidEntryPoint
class MorseFragment : Fragment(R.layout.fragment_morse) , View.OnClickListener {

    private val viewModel: MainViewModel by viewModels()

    val args: MorseFragmentArgs by navArgs()

    private lateinit var btnChar1: Button
    private lateinit var btnChar2: Button
    private lateinit var btnChar3: Button
    private lateinit var btnChar4: Button
    private lateinit var btnChar5: Button
    private lateinit var btnChar6: Button
    private lateinit var btnChar7: Button
    private lateinit var btnChar8: Button
    private lateinit var btnChar9: Button
    private lateinit var btnCharNull: Button

    private lateinit var btnCharQ: Button
    private lateinit var btnCharW: Button
    private lateinit var btnCharE: Button
    private lateinit var btnCharR: Button
    private lateinit var btnCharT: Button
    private lateinit var btnCharY: Button
    private lateinit var btnCharU: Button
    private lateinit var btnCharI: Button
    private lateinit var btnCharO: Button
    private lateinit var btnCharP: Button

    private lateinit var btnCharA: Button
    private lateinit var btnCharS: Button
    private lateinit var btnCharD: Button
    private lateinit var btnCharF: Button
    private lateinit var btnCharG: Button
    private lateinit var btnCharH: Button
    private lateinit var btnCharJ: Button
    private lateinit var btnCharK: Button
    private lateinit var btnCharL: Button
    private lateinit var btnCharEqually: Button

    private lateinit var btnCharZ: Button
    private lateinit var btnCharX: Button
    private lateinit var btnCharC: Button
    private lateinit var btnCharV: Button
    private lateinit var btnCharB: Button
    private lateinit var btnCharN: Button
    private lateinit var btnCharM: Button
    private lateinit var btnCharComma: Button
    private lateinit var btnCharDot: Button
    private lateinit var btnCharQuestionMark: Button

    private lateinit var btnRepeat: Button
    private lateinit var btnCharSlash: Button

    lateinit var clQewry: ConstraintLayout

    private var isSoundChecked: Boolean = true
    private var isFlashLightChecked: Boolean  = false

    private var menu: Menu? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_morse_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        when {
            isSoundChecked -> this.menu?.getItem(1)?.setIcon(R.drawable.ic_music_note_24_y)
            !isSoundChecked -> this.menu?.getItem(1)?.setIcon(R.drawable.ic_music_off_24_y)
            isFlashLightChecked -> this.menu?.getItem(0)?.setIcon(R.drawable.ic_lash_on_24_y)
            !isFlashLightChecked -> this.menu?.getItem(0)?.setIcon(R.drawable.ic_flash_off_24_y)

        }
    }
    private fun menuSoundOn(){
        this.menu?.getItem(1)?.setIcon(R.drawable.ic_music_note_24_y)
        isSoundChecked = true
    }
    private fun menuSoundOff(){
        this.menu?.getItem(1)?.setIcon(R.drawable.ic_music_off_24_y)
        isSoundChecked = false
    }
    private fun menuFlashOn() {
        this.menu?.getItem(0)?.setIcon(R.drawable.ic_lash_on_24_y)
        isFlashLightChecked = true
    }
    private fun menuFlashOff() {
        this.menu?.getItem(0)?.setIcon(R.drawable.ic_flash_off_24_y)
        isFlashLightChecked = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sound -> {
                if (isSoundChecked) {
                    if (!isFlashLightChecked && FlashLight.isDeviceHasCamera) {
                        menuSoundOff()
                        menuFlashOn()
                    }
                    if (isFlashLightChecked && FlashLight.isDeviceHasCamera) {
                        menuSoundOff()
                    }
                    if (!FlashLight.isDeviceHasCamera) {
                        Toast.makeText(requireContext(), " flash light not available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                   menuSoundOn()
                }
            }
            R.id.menu_flash -> {
                if (isFlashLightChecked) {
                  menuFlashOff()
                    if(!isSoundChecked) {
                        menuSoundOn()
                    }
                } else { //если была выключена
                    if (FlashLight.isDeviceHasCamera) {
                        menuFlashOn()
                    }
                    if (!FlashLight.isDeviceHasCamera) {
                        Toast.makeText(requireContext(), " flash light not available.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.menu_code -> {
                if (tvShowingMorse.visibility == View.VISIBLE) {
                    tvShowingMorse.visibility = View.INVISIBLE
                } else {
                    tvShowingMorse.visibility = View.VISIBLE
                }
            }
        }
        sendSwitchValueInViewModel()
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clQewry = view.findViewById(R.id.cl_qwery)

        btnChar1 = view.findViewById(R.id.btnChar1)
        btnChar2 = view.findViewById(R.id.btnChar2)
        btnChar3 = view.findViewById(R.id.btnChar3)
        btnChar4 = view.findViewById(R.id.btnChar4)
        btnChar5 = view.findViewById(R.id.btnChar5)
        btnChar6 = view.findViewById(R.id.btnChar6)
        btnChar7 = view.findViewById(R.id.btnChar7)
        btnChar8 = view.findViewById(R.id.btnChar8)
        btnChar9 = view.findViewById(R.id.btnChar9)
        btnCharNull = view.findViewById(R.id.btnCharNull)

        btnCharQ = view.findViewById(R.id.btnCharQ)
        btnCharW = view.findViewById(R.id.btnCharW)
        btnCharE = view.findViewById(R.id.btnCharE)
        btnCharR = view.findViewById(R.id.btnCharR)
        btnCharT = view.findViewById(R.id.btnCharT)
        btnCharY = view.findViewById(R.id.btnCharY)
        btnCharU = view.findViewById(R.id.btnCharU)
        btnCharI = view.findViewById(R.id.btnCharI)
        btnCharO = view.findViewById(R.id.btnCharO)
        btnCharP = view.findViewById(R.id.btnCharP)

        btnCharA = view.findViewById(R.id.btnCharA)
        btnCharS = view.findViewById(R.id.btnCharS)
        btnCharD = view.findViewById(R.id.btnCharD)
        btnCharF = view.findViewById(R.id.btnCharF)
        btnCharG = view.findViewById(R.id.btnCharG)
        btnCharH = view.findViewById(R.id.btnCharH)
        btnCharJ = view.findViewById(R.id.btnCharJ)
        btnCharK = view.findViewById(R.id.btnCharK)
        btnCharL = view.findViewById(R.id.btnCharL)
        btnCharEqually= view.findViewById(R.id.btnCharEqually)

        btnCharZ = view.findViewById(R.id.btnCharZ)
        btnCharX = view.findViewById(R.id.btnCharX)
        btnCharC = view.findViewById(R.id.btnCharC)
        btnCharV = view.findViewById(R.id.btnCharV)
        btnCharB = view.findViewById(R.id.btnCharB)
        btnCharN = view.findViewById(R.id.btnCharN)
        btnCharM = view.findViewById(R.id.btnCharM)
        btnCharComma = view.findViewById(R.id.btnCharComma)
        btnCharDot = view.findViewById(R.id.btnCharDot)
        btnCharQuestionMark = view.findViewById(R.id.btnCharQuestionMark)

        btnRepeat= view.findViewById(R.id.btnRepeat)
        btnCharSlash= view.findViewById(R.id.btnCharSlash)

        this.subscribeToObservers()
        this.setOnClickListenersToAllBtnChar()

        if (!isHelloShowedFlag) {
            sendDataInViewModel()
            isHelloShowedFlag = true
            viewModel.startTimerFromFragment()

            btnRepeat.isClickable = false

            view.postDelayed(
                {
                    clQewry.visibility = View.VISIBLE
                    isHelloShowedFlag = true
                    btnRepeat.isClickable = true
                },
                (viewModel.helloMs + 1000).toLong()
            )

        } else {
            isHelloShowedFlag = true
            clQewry.visibility = View.VISIBLE
            btnRepeat.isClickable = true
        }

        if (!isCurrentDataLoadedFlag) {
            viewModel.loadLesson()
            viewModel.startTimer(viewModel.helloMs + 1000)
            isCurrentDataLoadedFlag = true
        }

        btnStop.setOnClickListener {
            isHelloShowedFlag = false
            isCurrentDataLoadedFlag = false
            viewModel.whenStopBtnClickedPassTrue()
            viewModel.cancelPlayQuestion()
            findNavController().navigate(R.id.action_morseFragment_to_settingsFragment)
        }

   /*     switchNMorse.setOnClickListener {
            when(switchNMorse.isChecked) {
                false -> tvShowingMorse.visibility = View.INVISIBLE
                true -> tvShowingMorse.visibility = View.VISIBLE
            }
        }*/

     /*   when (switchNMorse.isChecked) {
            false -> {
                tvShowingMorse.visibility = View.GONE
            }
            true -> {
                tvShowingMorse.visibility = View.VISIBLE
            }
        }*/
    }

    fun sendSwitchValueInViewModel() {
        viewModel.whenSwitchLightClicked(isFlashLightChecked)
        viewModel.whenSwitchSoundClicked(isSoundChecked)
    }

    var isHelloShowedFlag  = false
    private var isCurrentDataLoadedFlag =  false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            isHelloShowedFlag = savedInstanceState.getBoolean("isHelloShowedFlag")
            isCurrentDataLoadedFlag = savedInstanceState.getBoolean("isCurrentDataLoadedFlag")
            isFlashLightChecked = savedInstanceState.getBoolean("isFlashlightChecked")
            isSoundChecked = savedInstanceState.getBoolean("isSoundChecked")
        }

        val callback = requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {
                // Handle the back button event
                isHelloShowedFlag = false
                isCurrentDataLoadedFlag = false
                viewModel.whenStopBtnClickedPassTrue()
                viewModel.cancelPlayQuestion()
                findNavController().navigate(R.id.action_morseFragment_to_settingsFragment)

            }
    }

    override fun onResume() {
        super.onResume()
        when (switchNMorse.isChecked) {
            false -> {
                tvShowingMorse.visibility = View.INVISIBLE
            }
            true -> {
                tvShowingMorse.visibility = View.VISIBLE
            }
        }

        if (AppLifecycleObserver.count != 0 ) {
            isHelloShowedFlag = false
            isCurrentDataLoadedFlag = false
            viewModel.whenStopBtnClickedPassTrue()
            viewModel.cancelPlayQuestion()
            findNavController().navigate(R.id.action_morseFragment_to_settingsFragment)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("isHelloShowedFlag", isHelloShowedFlag)
        outState.putBoolean("isCurrentDataLoadedFlag", isCurrentDataLoadedFlag)
        outState.putBoolean("isFlashlightChecked", isFlashLightChecked)
        outState.putBoolean("isSoundChecked", isSoundChecked)
        super.onSaveInstanceState(outState)
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
            it.let {

                when (it) {
                    1 -> {
                        tvShowingChar.setBackgroundColor(Color.parseColor("#121212"))
                        tvShowingChar.text = ""
                    }
                    2 -> {
                        tvShowingChar.setBackgroundColor(Color.RED)
                    }
                    3 -> {
                        tvShowingChar.setBackgroundColor(Color.parseColor("#229954"))
                        tvShowingChar.text = "YES"
                    }
                    else -> tvShowingChar.setBackgroundColor(Color.DKGRAY)
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

        viewModel.currentMorseCode?.observe(viewLifecycleOwner, Observer {
            it.let {
                tvShowingMorse.text = it
                if (it == null) {
                    tvShowingMorse.text = ""
                }
            }
        })

        viewModel.coutShowedSymbols?.observe(viewLifecycleOwner, Observer {
            it.let {
                tvCountShowedSymbols.text = it
            }
        })
    }

    private fun sendDataInViewModel() {
        viewModel.setDataName(
            lessonName = args.lessonName,
            speedName = args.speedName,
            timeoutName = args.timeoutName,
            levelName = parseInt(args.levelName),
            maxcharName = parseInt(args.maxcharName),
            repeatName = parseInt(args.repeatName)
        )
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
            R.id.btnCharI -> {
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
            R.id.btnCharSlash -> {
                answer = btnCharSlash.text.toString()
                viewModel.setAnswer(answer = answer)
            }

            R.id.btnRepeat -> {
                answer = btnRepeat.text.toString()
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

        btnCharSlash.setOnClickListener(this)
        btnRepeat.setOnClickListener(this)
    }

    override fun onPause() {
        super.onPause()
        if (AppLifecycleObserver.count != 0) {
            AppLifecycleObserver.count = -1
        }
    }
}

