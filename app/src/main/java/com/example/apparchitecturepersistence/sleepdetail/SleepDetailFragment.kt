package com.example.apparchitecturepersistence.sleepdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.apparchitecturepersistence.R
import com.example.apparchitecturepersistence.convertDurationToFormatted
import com.example.apparchitecturepersistence.convertNumericQualityToString
import com.example.apparchitecturepersistence.database.SleepDatabase_1
import com.example.apparchitecturepersistence.databinding.SleepDetailFragmentBinding

class SleepDetailFragment :
    Fragment() {
    private var _binding: SleepDetailFragmentBinding? = null
    private val binding get () = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        _binding = SleepDetailFragmentBinding.inflate(
            inflater, container, false)
        val root: View = binding.root
        val application = requireNotNull(this.activity).application
        val arguments = SleepDetailFragmentArgs.fromBundle(requireArguments()!!)

        // Create an instance of the ViewModel Factory.
        val dataSource = SleepDatabase_1.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepDetailViewModelFactory(arguments.sleepNightKey, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val sleepDetailViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(SleepDetailViewModel::class.java)
        sleepDetailViewModel.getNight().observe(viewLifecycleOwner, Observer { night ->
            binding.apply {
                qualityImage.setImageResource(
                    when (night.sleepQuality) {
                        0 -> R.drawable.ic_sleep_0
                        1 -> R.drawable.ic_sleep_1
                        2 -> R.drawable.ic_sleep_2
                        3 -> R.drawable.ic_sleep_3
                        4 -> R.drawable.ic_sleep_4
                        5 -> R.drawable.ic_sleep_5
                        else -> R.drawable.is_sleep_active
                    }
                )
                qualityString.text = convertNumericQualityToString(night.sleepQuality, requireContext().resources)
                sleepLength.text =
                    convertDurationToFormatted(night.startTimeMilli, night.endTimeMilli, requireContext().resources)
            }
        })
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.close.setOnClickListener{
            sleepDetailViewModel.onClose()
        }
        sleepDetailViewModel.navigateToSleepTracker.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    SleepDetailFragmentDirections.actionSleepDetailFragmentToSleepTrackerFragment())
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                sleepDetailViewModel.doneNavigating()
            }
        })
        // Add an Observer to the state variable for Navigating when a Quality icon is tapped.


        return root
    }
}