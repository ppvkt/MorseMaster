package ru.hadron.morsemaster.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_morse.*
import ru.hadron.morsemaster.R
import ru.hadron.morsemaster.db.entity.Stat
import ru.hadron.morsemaster.ui.viewmodels.MainViewModel
import timber.log.Timber

@AndroidEntryPoint
class MorseFragment : Fragment(R.layout.fragment_morse) {

    private val viewModel: MainViewModel by viewModels()

    val args: MorseFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.e("lesson name ====> ${args.lessonName}")

        //viewModel.insertStat(Stat("q", 10, 1, 2131232))
      viewModel.insertStat(Stat("asdfasd", 100, 1, 21345646532))

        this.subscribeToObservers()

        btnStop.setOnClickListener {
            //todo
            // stopTimer()

            findNavController().navigate(R.id.action_morseFragment_to_settingsFragment)
        }
    }

    fun subscribeToObservers() {
        viewModel.worth?.observe(viewLifecycleOwner, Observer {
            it.let {
                tvWorth.text = "worth : $it %"
            }
        })
    }
}

