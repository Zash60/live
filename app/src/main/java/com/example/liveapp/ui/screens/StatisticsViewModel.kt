package com.example.liveapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveapp.features.streaming.domain.model.ScheduledStream
import com.example.liveapp.features.streaming.domain.model.StreamHistory
import com.example.liveapp.features.streaming.domain.usecase.GetScheduledStreamsUseCase
import com.example.liveapp.features.streaming.domain.usecase.GetStreamHistoryUseCase
import com.example.liveapp.features.streaming.domain.usecase.GetStreamStatisticsUseCase
import com.example.liveapp.features.streaming.domain.usecase.SaveScheduledStreamUseCase
import com.example.liveapp.features.streaming.domain.usecase.StreamStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getStreamHistoryUseCase: GetStreamHistoryUseCase,
    private val getStreamStatisticsUseCase: GetStreamStatisticsUseCase,
    private val getScheduledStreamsUseCase: GetScheduledStreamsUseCase,
    private val saveScheduledStreamUseCase: SaveScheduledStreamUseCase
) : ViewModel() {

    private val _streamHistory = MutableStateFlow<List<StreamHistory>>(emptyList())
    val streamHistory: StateFlow<List<StreamHistory>> = _streamHistory.asStateFlow()

    private val _statistics = MutableStateFlow<StreamStatistics?>(null)
    val statistics: StateFlow<StreamStatistics?> = _statistics.asStateFlow()

    private val _scheduledStreams = MutableStateFlow<List<ScheduledStream>>(emptyList())
    val scheduledStreams: StateFlow<List<ScheduledStream>> = _scheduledStreams.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getStreamHistoryUseCase().collect { history ->
                _streamHistory.value = history
            }
        }
        viewModelScope.launch {
            _statistics.value = getStreamStatisticsUseCase()
        }
        viewModelScope.launch {
            getScheduledStreamsUseCase().collect { scheduled ->
                _scheduledStreams.value = scheduled
            }
        }
    }

    fun saveScheduledStream(scheduledStream: ScheduledStream) {
        viewModelScope.launch {
            saveScheduledStreamUseCase(scheduledStream)
        }
    }
}