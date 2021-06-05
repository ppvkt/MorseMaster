package ru.hadron.morsemaster.ui.fragments

import android.Manifest
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter

import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ru.hadron.morsemaster.AppLifecycleObserver
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import ru.hadron.morsemaster.util.Constants
import ru.hadron.morsemaster.util.Constants.KEY_ANSWER_TIMEOUT
import ru.hadron.morsemaster.util.Constants.KEY_ISCVSLOADED
import ru.hadron.morsemaster.util.Constants.KEY_LESSON
import ru.hadron.morsemaster.util.Constants.KEY_LEVEL
import ru.hadron.morsemaster.util.Constants.KEY_MAX_CHAR
import ru.hadron.morsemaster.util.Constants.KEY_REPEAT
import ru.hadron.morsemaster.util.Constants.KEY_SPEED
import ru.hadron.morsemaster.util.MorseUtility
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings), EasyPermissions.PermissionCallbacks {
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

        requestPermissions()

        setTextSizeInAutocompleteTextView(textSize = 14F)
        hideProgressBar()
        importAllCvsInDbIfNeedAndInitSpinners()

        setSpinnersAdapters()
        setAllSpinnerListeners()

        btnRun.setOnClickListener { view ->
            AppLifecycleObserver.count = 0
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
            showClearStatisticDialog()
        }
    }

    private fun setTextSizeInAutocompleteTextView(textSize: Float) {
        actLesson.textSize = textSize
        actSpeed.textSize = textSize
        actAnswerTimeout.textSize = textSize
        actLevel.textSize = textSize
        actMaxchar.textSize = textSize
        actRepeat.textSize = textSize
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemSelectedLessonInSpinner = sharedPref.getString(KEY_LESSON, defaultLesson).toString()
        itemSelectedSpeedInSpinner = sharedPref.getString(KEY_SPEED, defaultSpeed).toString()
        itemSelectedAnswerTimeoutInSpinner = sharedPref.getString(KEY_ANSWER_TIMEOUT, defaultAnswerTimeout). toString()
        itemSelectedLevelInSpinner = sharedPref.getString(KEY_LEVEL, defaultLevel). toString()
        itemSelectedMaxCharInSpinner = sharedPref.getString(KEY_MAX_CHAR, defaultMaxChar). toString()
        itemSelectedRepeatInSpinner = sharedPref.getString(KEY_REPEAT, defaultRepeat). toString()

        val callback = requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {
                requireActivity().finish()
                exitProcess(0)
            }
    }

    fun showCurrentDataInSpinner() {
        actLesson.setText(itemSelectedLessonInSpinner, false)
        actSpeed.setText(itemSelectedSpeedInSpinner, false)
        actAnswerTimeout.setText(itemSelectedAnswerTimeoutInSpinner, false)
        actLevel.setText(itemSelectedLevelInSpinner, false)
        actMaxchar.setText(itemSelectedMaxCharInSpinner, false)
        actRepeat.setText(itemSelectedRepeatInSpinner, false)
    }


    private fun setAllSpinnerListeners() {
        actLesson.setOnItemClickListener { _, _, position, _ ->
            itemSelectedLessonInSpinner = lessonAdapter.getItem(position).toString()
            sharedPref.edit()
                .putString(KEY_LESSON, itemSelectedLessonInSpinner)
                .apply()
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

    override fun onResume() {
        setSpinnersAdapters()
        super.onResume()
    }

    fun setSpinnersAdapters() {
        viewModel.lessons.observe(viewLifecycleOwner, Observer {spinnerData ->
            lessonAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                spinnerData
            )

          Timber.e("=================================$spinnerData")

            actLesson.apply {
                setAdapter(lessonAdapter)
                setText(itemSelectedLessonInSpinner, false)
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
            setText(itemSelectedSpeedInSpinner, false)
        }

        timeoutAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.answer_timeout_array,
            R.layout.dropdown_menu_popup_item
        ) as ArrayAdapter<String>
        actAnswerTimeout.apply {
            setAdapter(timeoutAdapter)
            setText(itemSelectedAnswerTimeoutInSpinner, false)
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
            setText(itemSelectedLevelInSpinner, false)
        }

        maxcharAdapter =  ArrayAdapter.createFromResource(
            requireContext(),
            R.array.max_char_array,
            R.layout.dropdown_menu_popup_item
        ) as ArrayAdapter<String>
        actMaxchar.apply {
            setAdapter(maxcharAdapter)
            setText(itemSelectedMaxCharInSpinner, false)
        }

        repeatAdapter =  ArrayAdapter.createFromResource(
            requireContext(),
            R.array.repeat_array,
            R.layout.dropdown_menu_popup_item
        ) as ArrayAdapter<String>
        actRepeat.apply {
            setAdapter(repeatAdapter)
            setText(itemSelectedRepeatInSpinner, false)
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

        showCurrentDataInSpinner()
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

           setDefaultSpinnersPosition()
           writeDataToSharedPref()
        }

    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun showClearStatisticDialog() {
        val dialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.AlertDialogTheme
        )
            .setTitle("Clear your Statistic?")
            .setMessage("Are you sure you want to clear all Statistic with your answers?")
            .setIcon(R.drawable.ic_clear)
            .setPositiveButton("YES") {_, _ ->
                viewModel.clearStat()
                setDefaultSpinnersPosition()
                writeDataToSharedPref()
                Toast.makeText(activity, "Your statistic is cleared!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("NO") {dialogInterface, _ -> dialogInterface.cancel()}

            .create()
        dialog.show()
    }

//----
    private fun requestPermissions() {
        if (MorseUtility.hasPermissions(requireContext())) { return }
        EasyPermissions.requestPermissions(
            this,
            "You need to accept camera permission to use flashlight code!",
            Constants.REQUEST_CODE_CAMERA_PERMISSION,
            Manifest.permission.CAMERA
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {/*NO-OP*/}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }
}

