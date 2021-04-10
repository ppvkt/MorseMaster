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

    private  var itemSelectedLessonInSpinner: String = ""
    private var itemSelectedSpeedInSpinner: String = ""
    private var itemSelectedAnswerTimeoutInSpinner: String = ""
    private var itemSelectedLevelInSpinner: String = ""
    private var itemSelectedMaxCharInSpinner: String = ""
    private var itemSelectedRepeatInSpinner: String = ""

 /*   init {
       // defaultLesson = viewModel.lessons.value!![0]
        itemSelectedLessonInSpinner = defaultLesson
        itemSelectedSpeedInSpinner = defaultSpeed
        itemSelectedAnswerTimeoutInSpinner = defaultAnswerTimeout
        itemSelectedLevelInSpinner = defaultLevel
        itemSelectedMaxCharInSpinner = defaultMaxChar
        itemSelectedRepeatInSpinner = defaultRepeat
    }*/

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
        showCurrentDataInSpinner()
        setAllSpinnerListeners()
        setSpinnersAdapters()

        Timber.e("onViewCreated  sharedPref    ===== ${ sharedPref.getString(KEY_LESSON, "")} ====================== itemSelectedLessonInSpinner $itemSelectedLessonInSpinner")

        btnRun.setOnClickListener { view ->
            showProgressBar()
            writeDataToSharedPref()
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

        btnClearStatistic.setOnClickListener {
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
    fun showCurrentDataInSpinner() {
        actLesson.setText(sharedPref.getString(KEY_LESSON, defaultLesson), false)
        actSpeed.setText(sharedPref.getString(KEY_SPEED, defaultSpeed), false)
        actAnswerTimeout.setText(sharedPref.getString(KEY_ANSWER_TIMEOUT, defaultAnswerTimeout), false)
        actLevel.setText(sharedPref.getString(KEY_LEVEL, defaultLevel), false)
        actMaxchar.setText(sharedPref.getString(KEY_MAX_CHAR, defaultLevel), false)
        actRepeat.setText(sharedPref.getString(KEY_REPEAT, defaultRepeat), false)
    }


    private fun setAllSpinnerListeners() {
        actLesson.setOnItemClickListener { _, _, position, _ ->
            itemSelectedLessonInSpinner = lessonAdapter.getItem(position).toString()
            sharedPref.edit()
                .putString(KEY_LESSON, itemSelectedLessonInSpinner)
                .apply()
            Timber.e("setAllSpinnerListeners()  sharedPref    ===== ${ sharedPref.getString(KEY_LESSON, "")} ====================== itemSelectedLessonInSpinner $itemSelectedLessonInSpinner")
        }

        actSpeed.setOnItemClickListener { _, _, position, _ ->
            itemSelectedSpeedInSpinner = speedAdapter.getItem(position) ?: defaultSpeed
            sharedPref.edit()
                .putString(KEY_SPEED, itemSelectedSpeedInSpinner)
                .apply()
        }

        actAnswerTimeout.setOnItemClickListener { _, _, position, _ ->
            itemSelectedAnswerTimeoutInSpinner = timeoutAdapter.getItem(position) ?: defaultAnswerTimeout
            sharedPref.edit()
                .putString(KEY_ANSWER_TIMEOUT, itemSelectedAnswerTimeoutInSpinner)
                .apply()
        }

        actLevel.setOnItemClickListener { _, _, position, _ ->
            itemSelectedLevelInSpinner = levelAdapter.getItem(position) ?: defaultLevel
            sharedPref.edit()
                .putString(KEY_LEVEL, itemSelectedLevelInSpinner)
                .apply()
        }

        actMaxchar.setOnItemClickListener { _, _, position, _ ->
            itemSelectedMaxCharInSpinner = maxcharAdapter.getItem(position) ?: defaultMaxChar
            sharedPref.edit()
                .putString(KEY_MAX_CHAR, itemSelectedMaxCharInSpinner)
                .apply()
        }

        actRepeat.setOnItemClickListener { _, _, position, _ ->
            itemSelectedRepeatInSpinner = repeatAdapter.getItem(position) ?: defaultRepeat
            sharedPref.edit()
                .putString(KEY_REPEAT, itemSelectedRepeatInSpinner)
                .apply()
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
                Timber.e("setSpinnersAdapters()  sharedPref    ===== ${ sharedPref.getString(KEY_LESSON, "")} ====================== itemSelectedLessonInSpinner $itemSelectedLessonInSpinner")
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
        actAnswerTimeout.apply {
            setAdapter(timeoutAdapter)
            setText(sharedPref.getString(KEY_ANSWER_TIMEOUT, ""), false)
        }

        val levelSpinnerData = arrayOfNulls<String>(51)
        var ii = 45
        for (i in levelSpinnerData.indices) {
            levelSpinnerData[i] = ii++.toString()
        }
        levelAdapter =  ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            levelSpinnerData
        )
        actLevel.apply {
            setAdapter(levelAdapter)
            setText(sharedPref.getString(KEY_LEVEL, ""), false)
        }

        maxcharAdapter =  ArrayAdapter.createFromResource(
            requireContext(),
            R.array.max_char_array,
            R.layout.dropdown_menu_popup_item
        ) as ArrayAdapter<String>
        actMaxchar.apply {
            setAdapter(maxcharAdapter)
            setText(sharedPref.getString(KEY_MAX_CHAR, ""), false)
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
       // showCurrentDataInSpinner()

        actLesson.setText(defaultLesson, false)
        actSpeed.setText(defaultSpeed, false)
        actAnswerTimeout.setText(defaultAnswerTimeout, false)
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

