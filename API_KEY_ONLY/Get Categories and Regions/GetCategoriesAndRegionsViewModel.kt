package examples.api_key_only.getcategoriesandregions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class GetCategoriesAndRegionsViewModel(private val feature: GetCategoriesAndRegions) : ViewModel() {
    private val _state = MutableStateFlow<GetCategoriesAndRegionsUiState>(GetCategoriesAndRegionsUiState.Idle)
    val state: StateFlow<GetCategoriesAndRegionsUiState> = _state.asStateFlow()

    fun load(regionCode: String = "US") = viewModelScope.launch {
        feature.categories(regionCode)
            .onStart { _state.value = GetCategoriesAndRegionsUiState.Loading }
            .catch { _state.value = GetCategoriesAndRegionsUiState.Error(it.message ?: "Could not load categories") }
            .collect { _state.value = GetCategoriesAndRegionsUiState.Categories(it) }
    }
}

sealed interface GetCategoriesAndRegionsUiState {
    data object Idle : GetCategoriesAndRegionsUiState
    data object Loading : GetCategoriesAndRegionsUiState
    data class Categories(val categories: List<VideoCategory>) : GetCategoriesAndRegionsUiState
    data class Error(val message: String) : GetCategoriesAndRegionsUiState
}
