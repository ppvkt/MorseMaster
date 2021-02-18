package ru.hadron.morsemaster.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import javax.inject.Inject


@AndroidEntryPoint
class MorseFragment : Fragment(R.layout.fragment_morse) {
    private val viewModel: MainViewModel by viewModels()

}