package com.danilobarreto.stockapp.orders.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danilobarreto.stockapp.designsystem.components.StockAppChip
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.components.StockAppPrimaryButton
import com.danilobarreto.stockapp.designsystem.components.StockAppSegmentedControl
import com.danilobarreto.stockapp.designsystem.components.StockAppStepper
import com.danilobarreto.stockapp.designsystem.components.StockAppTextField
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppShapes
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.todayIsoDate
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.orders.domain.AssetType
import com.danilobarreto.stockapp.orders.domain.OrderSide

@Composable
fun OrderFormScreen(
    viewModel: OrderFormViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadRecentTickers() }
    LaunchedEffect(uiState) {
        if (uiState is OrderFormUiState.Success) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StockAppColors.surface1)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(StockAppColors.surface2)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(StockAppIcons.ArrowLeft, contentDescription = "Voltar", tint = StockAppColors.textPrimary, modifier = Modifier.size(18.dp))
        }

        OrderFormFields(viewModel = viewModel, onCancel = onBack, topPadding = 20.dp)
    }
}

// Compartilhado entre OrderFormScreen (tela cheia) e OrderBottomSheet (lançamento rápido) -
// só o "invólucro" muda entre os dois, os campos e a lógica são os mesmos.
@Composable
internal fun OrderFormFields(
    viewModel: OrderFormViewModel,
    onCancel: () -> Unit,
    topPadding: Dp = 0.dp,
) {
    val uiState by viewModel.uiState.collectAsState()
    val recentTickers by viewModel.recentTickers.collectAsState()

    var ticker by remember { mutableStateOf("") }
    var assetType by remember { mutableStateOf(AssetType.STOCK) }
    var side by remember { mutableStateOf(OrderSide.BUY) }
    var quantity by remember { mutableStateOf(100) }
    var price by remember { mutableStateOf("") }
    var showMoreDetails by remember { mutableStateOf(false) }
    var fees by remember { mutableStateOf("") }
    var executedAt by remember { mutableStateOf(todayIsoDate()) }

    val priceValue = price.replace(",", ".").toDoubleOrNull() ?: 0.0
    val total = priceValue * quantity

    Column(modifier = Modifier.padding(top = topPadding)) {
        Text(
            "Nova ordem",
            style = StockAppTypography.titleLarge.copy(fontSize = 24.sp),
            color = StockAppColors.textPrimary,
        )
        Text(
            "Registre uma compra ou venda em segundos.",
            style = StockAppTypography.bodySmall,
            color = StockAppColors.textSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
        )

        StockAppSegmentedControl(
            options = listOf("Compra", "Venda"),
            selectedIndex = if (side == OrderSide.BUY) 0 else 1,
            onOptionSelected = { side = if (it == 0) OrderSide.BUY else OrderSide.SELL },
        )

        // Não está no protótipo (que assume o tipo do ativo), mas o backend exige - mantido
        // como um segundo segmentado, sem o mesmo destaque visual do Compra/Venda.
        Text(
            "Tipo de ativo",
            style = StockAppTypography.labelMedium,
            color = StockAppColors.textSecondary,
            modifier = Modifier.padding(top = 16.dp, bottom = 6.dp),
        )
        StockAppSegmentedControl(
            options = listOf("Ação", "FII"),
            selectedIndex = if (assetType == AssetType.STOCK) 0 else 1,
            onOptionSelected = { assetType = if (it == 0) AssetType.STOCK else AssetType.FII },
        )

        StockAppTextField(
            label = "Ticker",
            value = ticker,
            onValueChange = { ticker = it.uppercase() },
            placeholder = "Digite o ticker (ex.: PETR4)",
            leadingIcon = StockAppIcons.Search,
            modifier = Modifier.padding(top = 16.dp),
        )

        if (recentTickers.isNotEmpty()) {
            Text(
                "Últimas ordens",
                style = StockAppTypography.labelSmall,
                color = StockAppColors.textSecondary,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                recentTickers.forEach { recent ->
                    StockAppChip(text = recent, selected = recent == ticker, onClick = { ticker = recent })
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f).background(StockAppColors.surface2, StockAppShapes.cardRadius).padding(14.dp),
            ) {
                Text("Quantidade", style = StockAppTypography.labelMedium, color = StockAppColors.textSecondary, modifier = Modifier.padding(bottom = 8.dp))
                StockAppStepper(value = quantity, onValueChange = { quantity = it }, min = 10, step = 10)
            }
            Column(modifier = Modifier.weight(1f)) {
                StockAppTextField(
                    label = "Preço (R$)",
                    value = price,
                    onValueChange = { price = it },
                    placeholder = "0,00",
                    keyboardType = KeyboardType.Decimal,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .background(StockAppColors.primaryTint, StockAppShapes.cardRadius)
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Total da ordem", style = StockAppTypography.labelMedium, color = StockAppColors.primaryDeep)
                Text(
                    "R$ ${total.toDecimalString()}",
                    style = StockAppTypography.headerTitle,
                    color = StockAppColors.primaryDeep,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Text(
                if (side == OrderSide.BUY) "Sai do caixa e entra na carteira" else "Entra no caixa e baixa a posição",
                style = StockAppTypography.labelSmall,
                color = StockAppColors.primaryDeep,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f, fill = false).padding(start = 10.dp),
            )
        }

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
            text = "Confirmar ${if (side == OrderSide.BUY) "compra" else "venda"}${ticker.takeIf { it.isNotBlank() }?.let { " de $it" } ?: ""}",
            loading = uiState is OrderFormUiState.Loading,
            enabled = ticker.isNotBlank() && quantity > 0 && priceValue > 0,
            onClick = {
                viewModel.save(
                    ticker = ticker,
                    assetType = assetType,
                    side = side,
                    quantity = quantity,
                    price = priceValue,
                    fees = fees.replace(",", ".").toDoubleOrNull() ?: 0.0,
                    executedAt = executedAt,
                )
            },
            modifier = Modifier.padding(top = 22.dp),
        )

        Text(
            "Cancelar",
            style = StockAppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = StockAppColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .clickable(onClick = onCancel),
        )
    }
}