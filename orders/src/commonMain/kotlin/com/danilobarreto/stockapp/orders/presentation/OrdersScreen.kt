package com.danilobarreto.stockapp.orders.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.orders.domain.Order
import com.danilobarreto.stockapp.orders.domain.OrderSide

@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel,
    onNewOrder: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StockAppColors.surface1)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Ordens", style = StockAppTypography.titleLarge, color = StockAppColors.textPrimary)
            Button(onClick = onNewOrder, shape = MaterialTheme.shapes.medium) {
                Text("+ Nova ordem")
            }
        }

        when (val state = uiState) {
            is OrdersUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
            }
            is OrdersUiState.Error -> {
                StockAppErrorBanner(state.message, modifier = Modifier.padding(top = 24.dp))
            }
            is OrdersUiState.Success -> {
                if (state.orders.isEmpty()) {
                    Text(
                        "Nenhuma ordem lançada ainda.",
                        style = StockAppTypography.bodyMedium,
                        color = StockAppColors.textMuted,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .background(StockAppColors.surface2, shape = RoundedCornerShape(14.dp))
                    ) {
                        state.orders.forEach { OrderRow(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderRow(order: Order) {
    val sideColor = if (order.side == OrderSide.BUY) StockAppColors.textSuccess else StockAppColors.textDanger
    val sideLabel = if (order.side == OrderSide.BUY) "Compra" else "Venda"

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(order.ticker, style = StockAppTypography.bodyMedium, color = StockAppColors.textPrimary)
                Text(
                    sideLabel,
                    style = StockAppTypography.labelSmall,
                    color = sideColor,
                    modifier = Modifier
                        .background(sideColor.copy(alpha = 0.12f), shape = RoundedCornerShape(100))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
            Text(
                order.executedAt,
                style = StockAppTypography.labelSmall,
                color = StockAppColors.textMuted,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${order.quantity} un · R$ ${order.price.toDecimalString()}",
                style = StockAppTypography.bodyMedium,
                color = StockAppColors.textPrimary,
            )
        }
    }
}