package com.flutterkada.interview.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flutterkada.interview.core.ipinfo.domain.repository.IpInfoRepository
import com.flutterkada.interview.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val ipInfoRepository: IpInfoRepository
) : ViewModel() {

    // UI State
    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    // Events
    private val _event = MutableSharedFlow<DetailEvent>()
    val event = _event.asSharedFlow()

    /**
     * Single entry point for all user actions
     */
    fun onAction(action: DetailAction) {
        when (action) {
            is DetailAction.LoadIpInfo -> {
                _state.update { 
                    it.copy(
                        deviceName = action.deviceName,
                        deviceIp = action.deviceIp
                    ) 
                }
                fetchIpDetails()
            }

            is DetailAction.Retry -> {
                fetchIpDetails()
            }
        }
    }

    private fun fetchIpDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = ipInfoRepository.getPublicIpDetails()) {
                is Resource.Loading -> {
                    // Already showing loading
                }

                is Resource.Success -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            ipInfo = result.data,
                            error = null
                        ) 
                    }
                }

                is Resource.Error -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message
                        ) 
                    }
                }
            }
        }
    }
}
