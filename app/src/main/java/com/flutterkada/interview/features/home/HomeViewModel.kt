package com.flutterkada.interview.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flutterkada.interview.core.device.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    // UI State
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    // Events
    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    init {
        observeDevices()
        onAction(HomeAction.StartDiscovery)
    }

    /**
     * Single entry point for all user actions
     */
    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.StartDiscovery -> {
                viewModelScope.launch {
                    _state.update { it.copy(isDiscovering = true) }
                    deviceRepository.startDiscovery()
                }
            }

            is HomeAction.StopDiscovery -> {
                deviceRepository.stopDiscovery()
                _state.update { it.copy(isDiscovering = false) }
            }

            is HomeAction.RefreshDevices -> {
                viewModelScope.launch {
                    deviceRepository.stopDiscovery()
                    _state.update { it.copy(isDiscovering = true, devices = emptyList(), isEmpty = true) }
                    _event.emit(HomeEvent.ShowMessage("Refreshing device list..."))
                    deviceRepository.startDiscovery()
                }
            }

            is HomeAction.DeviceClicked -> {
                viewModelScope.launch {
                    _event.emit(HomeEvent.NavigateToDetail(action.device))
                }
            }
        }
    }

    private fun observeDevices() {
        viewModelScope.launch {
            deviceRepository.getAllDevices().collect { devices ->
                _state.update { 
                    it.copy(
                        devices = devices,
                        isEmpty = devices.isEmpty(),
                        isDiscovering = devices.isEmpty()
                    ) 
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        deviceRepository.stopDiscovery()
    }
}
