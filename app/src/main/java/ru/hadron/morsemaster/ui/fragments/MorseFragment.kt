package ru.hadron.morsemaster.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_morse.*
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.db.StorageDao
import ru.hadron.morsemaster.db.entity.Stat
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import ru.hadron.morsemaster.util.Constants.MORSE_DATABASE_NAME
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MorseFragment : Fragment(R.layout.fragment_morse) {

    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.insertStat(Stat("q", 10, 1, 2131232))
        viewModel.insertStat(Stat("asdfasd", 100, 1, 21345646532))
       Timber.e("-----------------------${context?.getDatabasePath(MORSE_DATABASE_NAME)}")
        this.subscribeToObservers()

        btnStop.setOnClickListener {
            //todo
            findNavController().navigate(R.id.action_morseFragment_to_settingsFragment)
        }
    }

    fun subscribeToObservers() {
        viewModel.worth?.observe(viewLifecycleOwner, Observer {
            //it?.let
            it.let {
                Timber.e ("---------------------------------------------------it = $it")
                tvWorth.text = "worth : $it %"
            }
        })
    }

    fun initDb() {
       // viewModel.initStat()
    }
}