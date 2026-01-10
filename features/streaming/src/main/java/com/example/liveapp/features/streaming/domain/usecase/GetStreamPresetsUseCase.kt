package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.model.StreamPreset
import com.example.liveapp.features.streaming.domain.repository.StreamPresetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStreamPresetsUseCase @Inject constructor(
    private val repository: StreamPresetRepository
) {
    operator fun invoke(): Flow<List<StreamPreset>> {
        return repository.getAllStreamPresets()
    }
}