package com.danilobarreto.stockapp.orders.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danilobarreto.stockapp.designsystem.components.StockAppBottomSheetHandle
import com.danilobarreto.stockapp.designsystem.components.StockAppPrimaryButton
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.orders.domain.OrderSide
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderBottomSheet(
    viewModel: OrderFormViewModel,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadRecentTickers() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StockAppColors.surface1,
        dragHandle = { StockAppBottomSheetHandle(modifier = Modifier.padding(vertical = 10.dp)) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(bottom = 12.dp)
        ) {
            val successState = uiState as? OrderFormUiState.Success
            if (successState != null) {
                OrderSuccessContent(
                    state = successState,
                    onViewCarteira = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onSaved() }
                    },
                    onClose = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                )
            } else {
                OrderFormFields(viewModel = viewModel, onCancel = onDismiss)
            }
        }
    }
}

@Composable
private fun OrderSuccessContent(
    state: OrderFormUiState.Success,
    onViewCarteira: () -> Unit,
    onClose: () -> Unit,
) {
    val sideLabel = if (state.side == OrderSide.BUY) "Compra" else "Venda"
    val total = state.price * state.quantity

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
    ) {
        Box(
            modifier = Modifier.size(66.dp).background(StockAppColors.primary, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(StockAppIcons.Check, contentDescription = null, tint = StockAppColors.onPrimary, modifier = Modifier.size(30.dp))
        }
        Text(
            "Ordem registrada",
            style = StockAppTypography.titleLarge.copy(fontSize = 26.sp),
            color = StockAppColors.textPrimary,
            modifier = Modifier.padding(top = 18.dp),
        )
        Text(
            "$sideLabel de ${state.quantity} ${state.ticker} a R$ ${state.price.toDecimalString()} — total R$ ${total.toDecimalString()}.",
            style = StockAppTypography.bodyMedium,
            color = StockAppColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        StockAppPrimaryButton(
            text = "Ver na carteira",
            onClick = onViewCarteira,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        )
        Text(
            "Fechar",
            style = StockAppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = StockAppColors.textSecondary,
            modifier = Modifier.padding(top = 14.dp).clickable(onClick = onClose),
        )
    }
}