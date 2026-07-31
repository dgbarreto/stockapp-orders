package com.danilobarreto.stockapp.orders.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.danilobarreto.stockapp.auth.data.AuthApiClient
import com.danilobarreto.stockapp.auth.data.AuthRepositoryImpl
import com.danilobarreto.stockapp.auth.data.TokenStorage
import com.danilobarreto.stockapp.auth.presentation.LoginScreen
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
import com.danilobarreto.stockapp.designsystem.theme.StockAppTheme
import com.danilobarreto.stockapp.orders.data.OrdersApiClient
import com.danilobarreto.stockapp.orders.data.OrdersRepositoryImpl
import com.danilobarreto.stockapp.orders.presentation.OrderFormScreen
import com.danilobarreto.stockapp.orders.presentation.OrderFormViewModel
import com.danilobarreto.stockapp.orders.presentation.OrdersScreen
import com.danilobarreto.stockapp.orders.presentation.OrdersViewModel

// Sample isolado do módulo orders: login (via auth) + as telas reais de Order.
// Sem NavHost de verdade aqui (o sample não tem essa cerimônia) - só um estado local
// alternando entre listagem e formulário, mesmo espírito do :sample do stockapp-portfolio.
@Composable
fun SampleApp() {
    val tokenStorage = remember { TokenStorage() }
    val httpClient = remember { createSampleHttpClient(tokenStorage) }

    val authRepository = remember {
        AuthRepositoryImpl(AuthApiClient(httpClient, sampleBaseUrl()), tokenStorage)
    }
    val loginViewModel = remember { LoginViewModel(authRepository) }

    val ordersRepository = remember {
        OrdersRepositoryImpl(OrdersApiClient(httpClient, sampleBaseUrl()))
    }
    val ordersViewModel = remember { OrdersViewModel(ordersRepository) }
    val orderFormViewModel = remember { OrderFormViewModel(ordersRepository) }

    val isLoggedIn by authRepository.isLoggedIn.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    StockAppTheme {
        if (isLoggedIn) {
            if (showForm) {
                OrderFormScreen(
                    viewModel = orderFormViewModel,
                    onBack = { showForm = false },
                    onSaved = {
                        showForm = false
                        ordersViewModel.load()
                    },
                )
            } else {
                OrdersScreen(
                    viewModel = ordersViewModel,
                    onNewOrder = { showForm = true },
                )
            }
        } else {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { /* isLoggedIn muda e recompõe pra tela de ordens sozinho */ },
                onNavigateToRegister = { /* sample é só login, de propósito */ }
            )
        }
    }
}