package ru.hadron.morsemaster.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel


@AndroidEntryPoint
class MorseFragment : Fragment(R.layout.fragment_morse) {
    private val viewModel: MainViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

    }



}