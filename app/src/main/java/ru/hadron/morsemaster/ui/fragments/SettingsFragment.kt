package ru.hadron.morsemaster.ui.fragments

import android.os.Bundle
import android.view.View
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
    }

}