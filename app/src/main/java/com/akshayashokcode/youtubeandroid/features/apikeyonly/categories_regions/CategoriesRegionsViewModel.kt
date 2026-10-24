package com.akshayashokcode.youtubeandroid.features.apikeyonly.categories_regions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.RegionListResponse
import com.akshayashokcode.youtubeandroid.core.model.VideoCategoryListResponse
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriesRegionsState(
    val categories: UiState<VideoCategoryListResponse> = UiState.Idle,
    val regions: UiState<RegionListResponse> = UiState.Idle,
)

@HiltViewModel
class CategoriesRegionsViewModel @Inject constructor(
    private val repository: CategoriesRegionsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriesRegionsState())
    val state: StateFlow<CategoriesRegionsState> = _state.asStateFlow()

    fun load(regionCode: String, language: String?) {
        val code = regionCode.trim().ifBlank { "US" }
        val lang = language?.trim()?.ifBlank { null }

        viewModelScope.launch {
            repository.getVideoCategories(code)
                .onStart { _state.value = _state.value.copy(categories = UiState.Loading) }
                .collect { result ->
                    _state.value = _state.value.copy(
                        categories = when (result) {
                            is AppResult.Success -> UiState.Data(result.data)
                            is AppResult.Error -> UiState.Error(result.message.ifBlank { "Failed to load categories." })
                        }
                    )
                }
        }

        viewModelScope.launch {
            repository.getSupportedRegions(lang)
                .onStart { _state.value = _state.value.copy(regions = UiState.Loading) }
                .collect { result ->
                    _state.value = _state.value.copy(
                        regions = when (result) {
                            is AppResult.Success -> UiState.Data(result.data)
                            is AppResult.Error -> UiState.Error(result.message.ifBlank { "Failed to load regions." })
                        }
                    )
                }
        }
    }
}
