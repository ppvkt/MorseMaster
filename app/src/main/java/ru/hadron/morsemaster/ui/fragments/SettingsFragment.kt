package ru.hadron.morsemaster.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val viewModel: MainViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRun.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_settingsFragment_to_morseFragment)
        }
        this.setSpinnersAdapters()

    }

    fun setSpinnersAdapters() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lesson_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spLesson.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.speed_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spSpeed.adapter = adapter
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
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.max_char_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spMaxChar.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.repeat_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(android.R.)
            spRepeat.adapter = adapter
        }
    }

}