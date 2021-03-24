package ru.hadron.morsemaster.ui.fragments

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
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
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    private var defaultLesson: String = "Koch 1 (K M)"
    private var defaultSpeed: String = "15"
    private var defaultAnswerTimeout: String = "2 sec"
    private var defaultLevel: String = "50"
    private var defaultMaxChar: String = "2"
    private var defaultRepeat: String = "1"

    private var itemSelectedLessonInSpinner: String
    private var itemSelectedSpeedInSpinner: String
    private var itemSelectedAnswerTimeoutInSpinner: String
    private var itemSelectedLevelInSpinner: String
    private var itemSelectedMaxCharInSpinner: String
    private var itemSelectedRepeatInSpinner: String

    init {
       // defaultLesson = viewModel.lessons.value!![0]
        itemSelectedLessonInSpinner = defaultLesson
        itemSelectedSpeedInSpinner = defaultSpeed
        itemSelectedAnswerTimeoutInSpinner = defaultAnswerTimeout
        itemSelectedLevelInSpinner = defaultLevel
        itemSelectedMaxCharInSpinner = defaultMaxChar
        itemSelectedRepeatInSpinner = defaultRepeat
    }

    private lateinit var lessonAdapter: ArrayAdapter<String>
    private lateinit var speedAdapter: ArrayAdapter<String>
    private lateinit var timeoutAdapter: ArrayAdapter<String>
    private lateinit var levelAdapter: ArrayAdapter<String>
    private lateinit var maxcharAdapter: ArrayAdapter<String>
    private lateinit var repeatAdapter: ArrayAdapter<String>

    @RequiresApi(Build.VERSION_CODES.N)
    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgressBar()
        importAllCvsInDbIfNeedAndInitSpinners()

        setSpinnersAdapters()
        setAllSpinnerListeners()

        btnRun.setOnClickListener { view ->
            showProgressBar()
            val bundle = Bundle().apply {
                putString("lessonName", itemSelectedLessonInSpinner)
                putString("speedName", itemSelectedSpeedInSpinner)
                putString("timeoutName", itemSelectedAnswerTimeoutInSpinner)
                putString("levelName", itemSelectedLevelInSpinner)
                putString("maxcharName", itemSelectedMaxCharInSpinner)
                putString("repeatName", itemSelectedRepeatInSpinner)
            }
            view.findNavController().navigate(
                R.id.action_settingsFragment_to_morseFragment,
                bundle)
        }

        btClearStatistic.setOnClickListener {
            viewModel.clearStat()
            setDefaultSpinnersPosition()
            writeDataToSharedPref()
            Toast.makeText(activity, "Your statistic is cleared!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        setSpinnersAdapters()
    }

    private fun setAllSpinnerListeners() {
        actLesson.setOnItemClickListener { _, _, position, _ ->
            itemSelectedLessonInSpinner = lessonAdapter.getItem(position) ?: defaultLesson
            writeDataToSharedPref()
        }

        actSpeed.setOnItemClickListener { _, _, position, _ ->
            itemSelectedSpeedInSpinner = speedAdapter.getItem(position) ?: defaultSpeed
            writeDataToSharedPref()
        }

        actAnswertimeout.setOnItemClickListener { _, _, position, _ ->
            itemSelectedAnswerTimeoutInSpinner = timeoutAdapter.getItem(position) ?: defaultAnswerTimeout
            writeDataToSharedPref()
        }

        actLevel.setOnItemClickListener { _, _, position, _ ->
            itemSelectedLevelInSpinner = levelAdapter.getItem(position) ?: defaultLevel
            writeDataToSharedPref()
        }

        actMaxchar.setOnItemClickListener { _, _, position, _ ->
            itemSelectedMaxCharInSpinner = maxcharAdapter.getItem(position) ?: defaultMaxChar
            writeDataToSharedPref()
        }

        actRepeat.setOnItemClickListener { _, _, position, _ ->
            itemSelectedRepeatInSpinner = repeatAdapter.getItem(position) ?: defaultRepeat
            writeDataToSharedPref()
        }
    }

    //------

    fun setSpinnersAdapters() {

        viewModel.lessons.observe(viewLifecycleOwner, Observer {spinnerData ->
            lessonAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                spinnerData
            )
            actLesson.apply {
                setAdapter(lessonAdapter)
                setText(sharedPref.getString(KEY_LESSON, ""), false)
            }
        })


        val speedSpinnerData = arrayOfNulls<String>(26)
        var i = 15
        for ( index in speedSpinnerData.indices) {
            speedSpinnerData[index] = i++.toString()
        }
        speedAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            speedSpinnerData
        )
        actSpeed.apply {
            setAdapter(speedAdapter)
            setText(sharedPref.getString(KEY_SPEED, ""), false)
        }

        timeoutAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.answer_timeout_array,
            R.layout.dropdown_menu_popup_item
        ) as ArrayAdapter<String>
        actAnswertimeout.apply {
            setAdapter(timeoutAdapter)
            setText(sharedPref.getString(KEY_ANSWER_TIMEOUT, ""), false)
        }

        val levelSpinnerData = arrayOfNulls<Int>(51)
        var ii = 45
        for (i in levelSpinnerData.indices) {
            levelSpinnerData[i] = ii
            ii++
        }
        levelAdapter =  ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            speedSpinnerData
        )
        actLevel.apply {
            setAdapter(levelAdapter)
            setText(sharedPref.getString(KEY_LEVEL, ""), false)
        }

        maxcharAdapter =  ArrayAdapter.createFromResource(
            requireContext(),
            R.array.max_char_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spMaxChar.adapter = adapter
            spMaxChar.setSelection(sharedPref.getInt(KEY_MAX_CHAR, 0))
        }

        repeatAdapter =  ArrayAdapter.createFromResource(
            requireContext(),
            R.array.repeat_array,
            R.layout.dropdown_menu_popup_item
        ) as ArrayAdapter<String>
        actRepeat.apply {
            setAdapter(repeatAdapter)
            setText(sharedPref.getString(KEY_REPEAT, ""), false)
        }
    }

    fun writeDataToSharedPref() {
        sharedPref.edit()
            .putString(KEY_LESSON, itemSelectedLessonInSpinner)
            .putString(KEY_SPEED, itemSelectedSpeedInSpinner)
            .putString(KEY_ANSWER_TIMEOUT, itemSelectedAnswerTimeoutInSpinner)
            .putString(KEY_LEVEL, itemSelectedLevelInSpinner)
            .putString(KEY_MAX_CHAR, itemSelectedMaxCharInSpinner)
            .putString(KEY_REPEAT, itemSelectedRepeatInSpinner)
            .apply()
    }

    private fun setDefaultSpinnersPosition() {
        itemSelectedLessonInSpinner = defaultLesson
        itemSelectedSpeedInSpinner = defaultSpeed
        itemSelectedAnswerTimeoutInSpinner = defaultAnswerTimeout
        itemSelectedLevelInSpinner = defaultLevel
        itemSelectedMaxCharInSpinner = defaultMaxChar
        itemSelectedRepeatInSpinner = defaultRepeat

        actLesson.setText(defaultLesson, false)
        actSpeed.setText(defaultSpeed, false)
        actAnswertimeout.setText(defaultAnswerTimeout, false)
        actLevel.setText(defaultLevel, false)
        actMaxchar.setText(defaultMaxChar, false)
        actRepeat.setText(defaultRepeat, false)
    }

    //----------
    private var isCvsLoaded = false

    @RequiresApi(Build.VERSION_CODES.N)
    private fun importAllCvsInDbIfNeedAndInitSpinners() {
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
        setDefaultSpinnersPosition()
        writeDataToSharedPref()
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

}

