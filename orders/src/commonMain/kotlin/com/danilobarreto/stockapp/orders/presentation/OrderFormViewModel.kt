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
    data class Success(
        val ticker: String,
        val side: OrderSide,
        val quantity: Int,
        val price: Double,
    ) : OrderFormUiState
    data class Error(val message: String) : OrderFormUiState
}

class OrderFormViewModel(
    private val repository: OrdersRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<OrderFormUiState>(OrderFormUiState.Idle)
    val uiState: StateFlow<OrderFormUiState> = _uiState.asStateFlow()

    private val _recentTickers = MutableStateFlow<List<String>>(emptyList())
    val recentTickers: StateFlow<List<String>> = _recentTickers.asStateFlow()

    fun loadRecentTickers() {
        viewModelScope.launch {
            runCatching { repository.getOrders() }
                .onSuccess { orders ->
                    _recentTickers.value = orders
                        .sortedByDescending { it.executedAt }
                        .map { it.ticker }
                        .distinct()
                        .take(8)
                }
        }
    }

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
                OrderFormUiState.Success(ticker.uppercase(), side, quantity, price)
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