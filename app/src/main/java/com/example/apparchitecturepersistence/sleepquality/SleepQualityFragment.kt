package com.example.apparchitecturepersistence.sleepquality

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.apparchitecturepersistence.R
import com.example.apparchitecturepersistence.database.SleepDatabase_1
import com.example.apparchitecturepersistence.databinding.SleepQualityFragmentBinding
import com.example.apparchitecturepersistence.databinding.SleepTrackerFragmentBinding

class SleepQualityFragment : Fragment() {
    private var _binding: SleepQualityFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SleepQualityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SleepQualityFragmentBinding.inflate(inflater, container, false)
        val root : View = binding.root
        return root;

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireNotNull(this.activity).application

        val arguments = SleepQualityFragmentArgs.fromBundle(requireArguments()!!)
        val dataSource = SleepDatabase_1.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepQualityViewModelFactory(arguments.sleepNightKey, dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SleepQualityViewModel::class.java)
        binding.apply {
            qualityZeroImage.setOnClickListener{
                viewModel.onSetSleepQuality(0)
            }
            qualityOneImage.setOnClickListener{
                viewModel.onSetSleepQuality(1)
            }
            qualityTwoImage.setOnClickListener{
                viewModel.onSetSleepQuality(2)
            }
            qualityThreeImage.setOnClickListener{
                viewModel.onSetSleepQuality(3)
            }
            qualityFourImage.setOnClickListener{
                viewModel.onSetSleepQuality(4)
            }
            qualityFiveImage.setOnClickListener{
                viewModel.onSetSleepQuality(5)
            }
        }
        viewModel.navigateToSleepTracker.observe(viewLifecycleOwner, Observer {
            if ( it == true) {
                this.findNavController().navigate(SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
                viewModel.doneNavigating()
            }
        })

    }

}