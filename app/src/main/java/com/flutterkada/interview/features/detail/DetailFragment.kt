package com.flutterkada.interview.features.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.flutterkada.interview.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeState()
        observeEvents()

        // Load IP info with device args
        viewModel.onAction(DetailAction.LoadIpInfo(args.deviceName, args.deviceIp))
    }

    private fun setupViews() {
        binding.retryButton.setOnClickListener {
            viewModel.onAction(DetailAction.Retry)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    // Display device info
                    binding.deviceNameText.text = state.deviceName
                    binding.deviceIpText.text = state.deviceIp

                    // Handle loading state
                    when {
                        state.isLoading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.ipInfoContainer.visibility = View.GONE
                            binding.errorContainer.visibility = View.GONE
                        }
                        state.error != null -> {
                            binding.progressBar.visibility = View.GONE
                            binding.ipInfoContainer.visibility = View.GONE
                            binding.errorContainer.visibility = View.VISIBLE
                            binding.errorText.text = state.error
                        }
                        state.ipInfo != null -> {
                            binding.progressBar.visibility = View.GONE
                            binding.ipInfoContainer.visibility = View.VISIBLE
                            binding.errorContainer.visibility = View.GONE

                            binding.publicIpText.text = state.ipInfo.ip
                            binding.cityText.text = state.ipInfo.city
                            binding.regionText.text = state.ipInfo.region
                            binding.countryText.text = state.ipInfo.country
                            binding.orgText.text = state.ipInfo.organization
                        }
                    }
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is DetailEvent.ShowError -> {
                            // Handle if needed
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
