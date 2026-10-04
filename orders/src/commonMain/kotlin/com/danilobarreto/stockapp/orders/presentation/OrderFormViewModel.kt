package com.danilobarreto.stockapp.orders.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danilobarreto.stockapp.orders.data.parseOrderErrorMessage
import com.danilobarreto.stockapp.orders.domain.AssetType
import com.danilobarreto.stockapp.orders.domain.OrderSide
import com.danilobarreto.stockapp.orders.domain.OrdersRepository
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

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

/** Valores iniciais do formulário quando a ordem nasce de um contexto (ex.: Detalhe do ativo). */
data class OrderPrefill(
    val ticker: String,
    val assetType: AssetType,
    val price: Double?,
)

private const val PRICE_LOOKUP_DEBOUNCE_MS = 400L
private val FULL_TICKER = Regex("^[A-Z]{4}\\d{1,2}$")

class OrderFormViewModel(
    private val repository: OrdersRepository,
    private val priceLookup: suspend (ticker: String, assetType: AssetType) -> Double? = { _, _ -> null },
) : ViewModel() {
    private val _uiState = MutableStateFlow<OrderFormUiState>(OrderFormUiState.Idle)
    val uiState: StateFlow<OrderFormUiState> = _uiState.asStateFlow()

    private val _recentTickers = MutableStateFlow<List<String>>(emptyList())
    val recentTickers: StateFlow<List<String>> = _recentTickers.asStateFlow()

    private val _prefill = MutableStateFlow<OrderPrefill?>(null)
    val prefill: StateFlow<OrderPrefill?> = _prefill.asStateFlow()

    private val _suggestedPrice = MutableStateFlow<Double?>(null)
    val suggestedPrice: StateFlow<Double?> = _suggestedPrice.asStateFlow()

    private var priceJob: Job? = null

    fun onTickerChanged(ticker: String, assetType: AssetType) {
        priceJob?.cancel()
        _suggestedPrice.value = null
        if (!FULL_TICKER.matches(ticker)) return

        priceJob = viewModelScope.launch {
            delay(PRICE_LOOKUP_DEBOUNCE_MS)
            _suggestedPrice.value = try {
                priceLookup(ticker, assetType)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null // sem sugestão; o usuário digita o preço normalmente
            }
        }
    }

    fun prefill(ticker: String, assetType: AssetType, price: Double?) {
        reset()
        _prefill.value = OrderPrefill(ticker.uppercase(), assetType, price)
        _suggestedPrice.value = price
    }

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
        _prefill.value = null
        priceJob?.cancel()
        _suggestedPrice.value = null
    }
}