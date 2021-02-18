package ru.hadron.morsemaster.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import ru.hadron.morsemaster.repositories.MainRepository

class MainViewModel @ViewModelInject constructor(
    val repository: MainRepository
) : ViewModel() {
}