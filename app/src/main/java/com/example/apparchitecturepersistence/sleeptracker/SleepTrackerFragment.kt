package com.example.apparchitecturepersistence.sleeptracker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.apparchitecturepersistence.R
import com.example.apparchitecturepersistence.database.SleepDatabase_1
import com.example.apparchitecturepersistence.databinding.SleepTrackerFragmentBinding
import com.google.android.material.snackbar.Snackbar

class SleepTrackerFragment : Fragment() {
    private var _binding: SleepTrackerFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SleepTrackerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SleepTrackerFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root;

        val application = requireNotNull(this.activity).application
        val dataSource = SleepDatabase_1.getInstance(application).sleepDatabaseDao;
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SleepTrackerViewModel::class.java)

        val manager = GridLayoutManager(activity, 3)
        binding.sleepList.layoutManager  = manager
        val adapter = SleepNightAdapter(SleepNightListener { nightId ->
                viewModel.onSleepNightClicked(nightId)
        })
        binding.sleepList.adapter = adapter
        viewModel.nights.observe(viewLifecycleOwner, Observer { it ->
            it?.let {
                adapter.submitList(it)
            }
        })
        binding.startButton.setOnClickListener{
            viewModel.onStartTracking()
            binding.stopButton.visibility = View.VISIBLE
            binding.startButton.visibility = View.GONE
        }
        binding.stopButton.setOnClickListener {
            viewModel.onStopTracking()
            binding.startButton.visibility = View.VISIBLE
            binding.stopButton.visibility = View.GONE
        }
        binding.clearButton.setOnClickListener{
            viewModel.onClear()
            binding.startButton.visibility = View.VISIBLE
        }
        viewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer { night ->
            night?.let{
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                viewModel.doneNavigating()
            }
        })
        viewModel.navigateToSleepDataQuality.observe(viewLifecycleOwner, Observer { night ->
            night?.let {

                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepDetailFragment(night))
                viewModel.onSleepDataQualityNavigated()
            }
        })
        viewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                viewModel.doneShowingSnackbar()
            }
        })
        return root
    }
}