package ru.hadron.morsemaster.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import ru.hadron.morsemaster.repositories.DefaultRepository

class MorseViewModel  @ViewModelInject constructor(
    val repository: DefaultRepository
) : ViewModel() {
}

