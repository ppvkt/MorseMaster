package ru.hadron.morsemaster.ui.fragments

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import ru.hadron.morsemaster.util.Constants.KEY_ANSWER_TIMEOUT
import ru.hadron.morsemaster.util.Constants.KEY_ISCVSLOADED
import ru.hadron.morsemaster.util.Constants.KEY_LESSON
import ru.hadron.morsemaster.util.Constants.KEY_LEVEL
import ru.hadron.morsemaster.util.Constants.KEY_MAX_CHAR
import ru.hadron.morsemaster.util.Constants.KEY_REPEAT
import ru.hadron.morsemaster.util.Constants.KEY_SPEED
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    private var posSelectedLessonInSpinner = 0
    private var posSelectedSpeedInSpinner = 0
    private var posSelectedAnswerTimeoutInSpinner = 0
    private var posSelectedLevelInSpinner = 0
    private var posSelectedMaxCharInSpinner = 0
    private var posSelectedRepeatInSpinner = 0

    private var itemSelectedLessonInSpinner = ""
    private var itemSelectedSpeedInSpinner = ""
    private var itemSelectedAnswerTimeoutInSpinner = ""
    private var itemSelectedLevelInSpinner = 0
    private var itemSelectedMaxCharInSpinner = 0
    private var itemSelectedRepeatInSpinner = 0

    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        importAllCvsInDbIfNeed()

        btnRun.setOnClickListener { view ->
            this.writeDataToSharedPref()
            this.setItemSelected()
            val bundle = Bundle().apply {
                putString("lessonName", itemSelectedLessonInSpinner)
                putString("speedName", itemSelectedSpeedInSpinner)
                putString("timeoutName", itemSelectedAnswerTimeoutInSpinner)
                putInt("levelName", itemSelectedLevelInSpinner)
                putInt("maxcharName", itemSelectedMaxCharInSpinner)
                putInt("repeatName", itemSelectedRepeatInSpinner)
            }

            view.findNavController().navigate(
                R.id.action_settingsFragment_to_morseFragment,
                bundle)
        }

        btClearStatistic.setOnClickListener {
            viewModel.clearStat()
            setDefaultSpinnersPosition()
            Toast.makeText(activity, "Your statistic is cleared!", Toast.LENGTH_SHORT).show()

        }

        this.setSpinnersAdapters()
        this.setAllSpinnerListeners()
    }

    //------
    fun setSpinnersAdapters() {

        viewModel.lessons.observe(viewLifecycleOwner, Observer {spinnerData ->
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                spinnerData
            )
                .also { adapter ->
                    // adapter.setDropDownViewResource(android.R.)
                    spLesson.adapter = adapter
                    spLesson.setSelection(sharedPref.getInt(KEY_LESSON, 0))
                }
        })

        val speedSpinnerData = Array(40) { i -> i + 1 }
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            speedSpinnerData
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spSpeed.adapter = adapter
            spSpeed.setSelection(sharedPref.getInt(KEY_SPEED, 0))

        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.answer_timeout_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spAnswerTimeout.adapter = adapter
            spAnswerTimeout.setSelection(sharedPref.getInt(KEY_ANSWER_TIMEOUT, 0))
        }

        val levelSpinnerData = arrayOfNulls<Int>(51)
        var ii = 45
        for (i in levelSpinnerData.indices) {
            levelSpinnerData[i] = ii
            ii++
        }
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            levelSpinnerData
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spLevel.adapter = adapter
            spLevel.setSelection(sharedPref.getInt(KEY_LEVEL, 0))
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.max_char_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spMaxChar.adapter = adapter
            spMaxChar.setSelection(sharedPref.getInt(KEY_MAX_CHAR, 0))
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.repeat_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spRepeat.adapter = adapter
            spRepeat.setSelection(sharedPref.getInt(KEY_REPEAT, 0))
        }
    }

    fun writeDataToSharedPref() {
        sharedPref.edit()
            .putInt(KEY_LESSON, posSelectedLessonInSpinner)
            .putInt(KEY_SPEED, posSelectedSpeedInSpinner)
            .putInt(KEY_ANSWER_TIMEOUT, posSelectedAnswerTimeoutInSpinner)
            .putInt(KEY_LEVEL, posSelectedLevelInSpinner)
            .putInt(KEY_MAX_CHAR, posSelectedMaxCharInSpinner)
            .putInt(KEY_REPEAT, posSelectedRepeatInSpinner)
            .apply()
    }

    fun setAllSpinnerListeners() {
        spLesson.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/
            }

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                posSelectedLessonInSpinner = pos

            }
        }

        spSpeed.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/
            }

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                posSelectedSpeedInSpinner = pos
            }
        }
        spAnswerTimeout.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/
            }

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                posSelectedAnswerTimeoutInSpinner = pos
            }
        }
        spLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/
            }

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                posSelectedLevelInSpinner = pos
            }
        }
        spMaxChar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/
            }

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                posSelectedMaxCharInSpinner = pos
            }
        }
        spRepeat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/
            }

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                posSelectedRepeatInSpinner = pos
            }
        }
    }

    private  fun setItemSelected() {
        itemSelectedLessonInSpinner = spLesson.selectedItem.toString()
        itemSelectedSpeedInSpinner = spSpeed.selectedItem.toString()
        itemSelectedAnswerTimeoutInSpinner =spAnswerTimeout.selectedItem.toString()
        itemSelectedLevelInSpinner = spLevel.selectedItem.toString().toInt()
        itemSelectedMaxCharInSpinner = spMaxChar.selectedItem.toString().toInt()
        itemSelectedRepeatInSpinner = spRepeat.selectedItem.toString().toInt()
    }

    /**
     * wpm	18
     * adv_max	2
     * adv_level	75
     * adv_repeat	2
     * lession	Koch 1 (K M)
     * timeout	0
     */
    fun setDefaultSpinnersPosition() {
        spLesson.setSelection(0)
        spSpeed.setSelection(17)
        spAnswerTimeout.setSelection(0)
        spLevel.setSelection(30)
        spMaxChar.setSelection(1)
        spRepeat.setSelection(1)
    }

    //----------
    private var isCvsLoaded = false

    @RequiresApi(Build.VERSION_CODES.N)
    private fun importAllCvsInDbIfNeed() {
        isCvsLoaded = sharedPref.getBoolean(KEY_ISCVSLOADED, false)
        if (!isCvsLoaded) {
            viewModel.apply {
                importCvsLesson()
                importCvsCodes()
                importCvsCodesGroup()
            }
            sharedPref.edit().putBoolean(KEY_ISCVSLOADED, true).apply()
            Toast.makeText(activity, "all cvs downloaded!", Toast.LENGTH_SHORT).show()
        }
    }
}

