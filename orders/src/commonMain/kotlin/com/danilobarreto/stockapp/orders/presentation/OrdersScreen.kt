package com.danilobarreto.stockapp.orders.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppBadge
import com.danilobarreto.stockapp.designsystem.components.StockAppBadgeStyle
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppShapes
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.orders.domain.Order
import com.danilobarreto.stockapp.orders.domain.OrderSide

@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel,
    onNewOrder: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(StockAppColors.surface1)) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StockAppColors.surface2)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(StockAppIcons.ArrowLeft, contentDescription = "Voltar", tint = StockAppColors.textPrimary, modifier = Modifier.size(20.dp))
                }
                Row(
                    modifier = Modifier
                        .background(StockAppColors.primary, shape = StockAppShapes.pillRadius)
                        .clickable(onClick = onNewOrder)
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(StockAppIcons.Plus, contentDescription = null, tint = StockAppColors.onPrimary, modifier = Modifier.size(16.dp))
                    Text("Nova ordem", style = StockAppTypography.labelMedium, color = StockAppColors.onPrimary)
                }
            }

            val orderCount = (uiState as? OrdersUiState.Success)?.orders?.size
            Text(
                "Ordens",
                style = StockAppTypography.titleLarge,
                color = StockAppColors.textPrimary,
                modifier = Modifier.padding(top = 22.dp),
            )
            Text(
                if (orderCount != null) "$orderCount no histórico" else "Suas ordens lançadas",
                style = StockAppTypography.bodyMedium,
                color = StockAppColors.textMuted,
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        when (val state = uiState) {
            is OrdersUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(top = 24.dp), contentAlignment = Alignment.TopCenter) {
                    CircularProgressIndicator()
                }
            }
            is OrdersUiState.Error -> {
                StockAppErrorBanner(state.message, modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp))
            }
            is OrdersUiState.Success -> {
                if (state.orders.isEmpty()) {
                    Text(
                        "Nenhuma ordem lançada ainda.",
                        style = StockAppTypography.bodyMedium,
                        color = StockAppColors.textMuted,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.orders, key = { it.id }) { order ->
                            OrderRow(order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderRow(order: Order) {
    val sideLabel = if (order.side == OrderSide.BUY) "Compra" else "Venda"
    val sideStyle = if (order.side == OrderSide.BUY) StockAppBadgeStyle.Success else StockAppBadgeStyle.Danger

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(StockAppColors.surface2, shape = StockAppShapes.cardRadius)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(StockAppShapes.avatarRadius)
                .background(StockAppColors.primaryTint),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                order.ticker.take(4),
                style = StockAppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = StockAppColors.primaryDeep,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(order.ticker, style = StockAppTypography.titleMedium, color = StockAppColors.textPrimary)
            Text(
                "${order.executedAt} · ${order.quantity} un · R$ ${order.price.toDecimalString()}",
                style = StockAppTypography.labelSmall,
                color = StockAppColors.textMuted,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        StockAppBadge(sideLabel, sideStyle)
    }
}
