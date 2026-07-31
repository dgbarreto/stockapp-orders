package com.danilobarreto.stockapp.orders.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.components.StockAppPrimaryButton
import com.danilobarreto.stockapp.designsystem.components.StockAppTextField
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.todayIsoDate
import com.danilobarreto.stockapp.orders.domain.AssetType
import com.danilobarreto.stockapp.orders.domain.OrderSide

@Composable
fun OrderFormScreen(
    viewModel: OrderFormViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StockAppColors.surface1)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(StockAppColors.surface2)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text("←", color = StockAppColors.textPrimary)
            }
            Text(
                "Nova ordem",
                style = StockAppTypography.titleLarge,
                color = StockAppColors.textPrimary,
                modifier = Modifier.padding(start = 12.dp),
            )
        }

        OrderFormFields(viewModel = viewModel, onSaved = onSaved, topPadding = 20.dp)
    }
}

// Compartilhado entre OrderFormScreen (tela cheia) e OrderBottomSheet (lançamento rápido) -
// só o "invólucro" muda entre os dois, os campos e a lógica são os mesmos.
@Composable
internal fun OrderFormFields(
    viewModel: OrderFormViewModel,
    onSaved: () -> Unit,
    topPadding: Dp = 0.dp,
) {
    val uiState by viewModel.uiState.collectAsState()

    var ticker by remember { mutableStateOf("") }
    var assetType by remember { mutableStateOf(AssetType.STOCK) }
    var side by remember { mutableStateOf(OrderSide.BUY) }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var showMoreDetails by remember { mutableStateOf(false) }
    var fees by remember { mutableStateOf("") }
    var executedAt by remember { mutableStateOf(todayIsoDate()) }

    LaunchedEffect(uiState) {
        if (uiState is OrderFormUiState.Success) onSaved()
    }

    StockAppTextField(
        label = "Ticker",
        value = ticker,
        onValueChange = { ticker = it.uppercase() },
        placeholder = "PETR4",
        modifier = Modifier.padding(top = topPadding),
    )

    Text("Tipo de ativo", style = StockAppTypography.labelMedium, color = StockAppColors.textSecondary, modifier = Modifier.padding(top = 16.dp, bottom = 6.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(StockAppColors.border, RoundedCornerShape(10.dp))
            .padding(3.dp)
    ) {
        FormSegment("Ação", assetType == AssetType.STOCK, Modifier.weight(1f)) { assetType = AssetType.STOCK }
        FormSegment("FII", assetType == AssetType.FII, Modifier.weight(1f)) { assetType = AssetType.FII }
    }

    Text("Operação", style = StockAppTypography.labelMedium, color = StockAppColors.textSecondary, modifier = Modifier.padding(top = 16.dp, bottom = 6.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(StockAppColors.border, RoundedCornerShape(10.dp))
            .padding(3.dp)
    ) {
        FormSegment("Compra", side == OrderSide.BUY, Modifier.weight(1f)) { side = OrderSide.BUY }
        FormSegment("Venda", side == OrderSide.SELL, Modifier.weight(1f)) { side = OrderSide.SELL }
    }

    StockAppTextField(
        label = "Quantidade",
        value = quantity,
        onValueChange = { quantity = it.filter(Char::isDigit) },
        keyboardType = KeyboardType.Number,
        modifier = Modifier.padding(top = 16.dp),
    )

    StockAppTextField(
        label = "Preço",
        value = price,
        onValueChange = { price = it },
        placeholder = "32.40",
        keyboardType = KeyboardType.Decimal,
        modifier = Modifier.padding(top = 16.dp),
    )

    Text(
        if (showMoreDetails) "Ocultar detalhes" else "Mais detalhes",
        style = StockAppTypography.labelMedium,
        color = StockAppColors.textAccent,
        modifier = Modifier
            .padding(top = 16.dp)
            .clickable { showMoreDetails = !showMoreDetails },
    )

    if (showMoreDetails) {
        StockAppTextField(
            label = "Taxas (corretagem, emolumentos)",
            value = fees,
            onValueChange = { fees = it },
            placeholder = "0.00",
            keyboardType = KeyboardType.Decimal,
            modifier = Modifier.padding(top = 12.dp),
        )
        StockAppTextField(
            label = "Data da operação",
            value = executedAt,
            onValueChange = { executedAt = it },
            placeholder = "yyyy-MM-dd",
            modifier = Modifier.padding(top = 12.dp),
        )
    }

    if (uiState is OrderFormUiState.Error) {
        StockAppErrorBanner(
            (uiState as OrderFormUiState.Error).message,
            modifier = Modifier.padding(top = 16.dp),
        )
    }

    StockAppPrimaryButton(
        text = "Salvar",
        loading = uiState is OrderFormUiState.Loading,
        enabled = ticker.isNotBlank() && quantity.isNotBlank() && price.isNotBlank(),
        onClick = {
            viewModel.save(
                ticker = ticker,
                assetType = assetType,
                side = side,
                quantity = quantity.toIntOrNull() ?: 0,
                price = price.toDoubleOrNull() ?: 0.0,
                fees = fees.toDoubleOrNull() ?: 0.0,
                executedAt = executedAt,
            )
        },
        modifier = Modifier.padding(top = 24.dp),
    )
}

@Composable
private fun FormSegment(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        label,
        style = StockAppTypography.bodyMedium,
        color = if (selected) StockAppColors.textPrimary else StockAppColors.textSecondary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .background(if (selected) StockAppColors.surface2 else StockAppColors.border, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp)
    )
}