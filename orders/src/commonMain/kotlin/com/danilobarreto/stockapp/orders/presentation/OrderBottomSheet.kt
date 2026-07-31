package com.danilobarreto.stockapp.orders.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import kotlinx.coroutines.launch

// Lançamento rápido de ordem, pensado pra ser chamado de qualquer tela (hoje: Dashboard).
// Reaproveita OrderFormFields (mesmos campos/lógica do OrderFormScreen) - só troca a casca
// de tela cheia por um ModalBottomSheet. Quem chama controla show/hide e deve chamar
// viewModel.reset() antes de abrir, pra não herdar um uiState "Success" da vez anterior.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderBottomSheet(
    viewModel: OrderFormViewModel,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "Nova ordem rápida",
                style = StockAppTypography.titleLarge,
                color = StockAppColors.textPrimary,
            )

            OrderFormFields(
                viewModel = viewModel,
                topPadding = 16.dp,
                onSaved = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onSaved() }
                },
            )
        }
    }
}