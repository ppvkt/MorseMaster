package ru.hadron.morsemaster.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import ru.hadron.morsemaster.ui.viewmodels.SettingsViewModel
import ru.hadron.morsemaster.util.Constants.KEY_ANSWER_TIMEOUT
import ru.hadron.morsemaster.util.Constants.KEY_LESSON
import ru.hadron.morsemaster.util.Constants.KEY_LEVEL
import ru.hadron.morsemaster.util.Constants.KEY_MAX_CHAR
import ru.hadron.morsemaster.util.Constants.KEY_REPEAT
import ru.hadron.morsemaster.util.Constants.KEY_SPEED
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRun.setOnClickListener { view ->
            this.writeDataToSharedPref()
            val info = spLesson.getItemAtPosition(posSelectedLessonInSpinner).toString()

            Timber.e("--------${viewModel.initStat(arrayOf("a")).hashCode()}")

            viewModel.clearStat()

            view.findNavController().navigate(R.id.action_settingsFragment_to_morseFragment)
        }
        this.setSpinnersAdapters()
        this.setAllSpinnerListeners()

    }

    //------
    private var posSelectedLessonInSpinner = 0
    private var posSelectedSpeedInSpinner = 0
    private var posSelectedAnswerTimeoutInSpinner = 0
    private var posSelectedLevelInSpinner = 0
    private var posSelectedMaxCharInSpinner = 0
    private var posSelectedRepeatInSpinner = 0

    fun setSpinnersAdapters() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lesson_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spLesson.adapter = adapter
            spLesson.setSelection(sharedPref.getInt(KEY_LESSON, 0))
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.speed_array,
            android.R.layout.simple_spinner_item
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
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.level_array,
            android.R.layout.simple_spinner_item
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
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/ }
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
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/ }
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
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/ }
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
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/ }
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
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/ }
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
            override fun onNothingSelected(p0: AdapterView<*>?) { /*NO-OP*/ }
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

}