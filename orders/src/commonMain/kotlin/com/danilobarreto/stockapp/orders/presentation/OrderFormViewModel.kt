package com.danilobarreto.stockapp.orders.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danilobarreto.stockapp.orders.data.parseOrderErrorMessage
import com.danilobarreto.stockapp.orders.domain.AssetType
import com.danilobarreto.stockapp.orders.domain.OrderSide
import com.danilobarreto.stockapp.orders.domain.OrdersRepository
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface OrderFormUiState {
    data object Idle : OrderFormUiState
    data object Loading : OrderFormUiState
    data object Success : OrderFormUiState
    data class Error(val message: String) : OrderFormUiState
}

class OrderFormViewModel(
    private val repository: OrdersRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<OrderFormUiState>(OrderFormUiState.Idle)
    val uiState: StateFlow<OrderFormUiState> = _uiState.asStateFlow()

    fun save(
        ticker: String,
        assetType: AssetType,
        side: OrderSide,
        quantity: Int,
        price: Double,
        fees: Double,
        executedAt: String,
    ) {
        viewModelScope.launch {
            _uiState.value = OrderFormUiState.Loading
            _uiState.value = try {
                repository.createOrder(
                    ticker.uppercase(),
                    assetType,
                    side,
                    quantity,
                    price,
                    fees,
                    executedAt
                )
                OrderFormUiState.Success
            } catch (e: ClientRequestException) {
                OrderFormUiState.Error(parseOrderErrorMessage(e))
            } catch (e: Exception) {
                OrderFormUiState.Error(e.message ?: "Erro ao salvar a ordem")
            }
        }
    }

    fun reset() {
        _uiState.value = OrderFormUiState.Idle
    }
}