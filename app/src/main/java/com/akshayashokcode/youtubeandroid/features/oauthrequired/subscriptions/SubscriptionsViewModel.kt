package com.akshayashokcode.youtubeandroid.features.oauthrequired.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshayashokcode.youtubeandroid.core.model.Subscription
import com.akshayashokcode.youtubeandroid.core.result.AppResult
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubscriptionsState(
    val subscribeState: UiState<Subscription> = UiState.Idle,
    val unsubscribeState: UiState<Unit> = UiState.Idle,
    val subscriptionId: String? = null,
)

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val repository: SubscriptionsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionsState())
    val state: StateFlow<SubscriptionsState> = _state.asStateFlow()

    fun subscribe(channelId: String) {
        viewModelScope.launch {
            repository.subscribe(channelId)
                .onStart { _state.value = _state.value.copy(subscribeState = UiState.Loading) }
                .collect { result ->
                    _state.value = when (result) {
                        is AppResult.Success -> _state.value.copy(
                            subscribeState = UiState.Data(result.data),
                            subscriptionId = result.data.id,
                        )
                        is AppResult.Error -> _state.value.copy(
                            subscribeState = UiState.Error(result.message.ifBlank { "Failed to subscribe." }),
                        )
                    }
                }
        }
    }

    fun unsubscribe(subscriptionId: String) {
        viewModelScope.launch {
            repository.unsubscribe(subscriptionId)
                .onStart { _state.value = _state.value.copy(unsubscribeState = UiState.Loading) }
                .collect { result ->
                    _state.value = when (result) {
                        is AppResult.Success -> _state.value.copy(
                            unsubscribeState = UiState.Data(Unit),
                            subscriptionId = null,
                        )
                        is AppResult.Error -> _state.value.copy(
                            unsubscribeState = UiState.Error(result.message.ifBlank { "Failed to unsubscribe." }),
                        )
                    }
                }
        }
    }
}
