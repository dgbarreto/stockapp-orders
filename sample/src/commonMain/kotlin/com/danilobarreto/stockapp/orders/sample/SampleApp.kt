package com.danilobarreto.stockapp.orders.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.danilobarreto.stockapp.auth.data.AuthApiClient
import com.danilobarreto.stockapp.auth.data.AuthRepositoryImpl
import com.danilobarreto.stockapp.auth.data.TokenStorage
import com.danilobarreto.stockapp.auth.presentation.LoginScreen
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
import com.danilobarreto.stockapp.designsystem.theme.StockAppTheme

// Sample isolado do módulo orders: só valida login (via auth) + build da árvore de módulos.
// Ainda não existe domain/data/presentation de Order aqui - assim que isso for implementado,
// a tela de placeholder abaixo vira o lançamento/listagem de ordens de verdade.
@Composable
fun SampleApp() {
    val tokenStorage = remember { TokenStorage() }
    val httpClient = remember { createSampleHttpClient(tokenStorage) }

    val authRepository = remember {
        AuthRepositoryImpl(AuthApiClient(httpClient, sampleBaseUrl()), tokenStorage)
    }
    val loginViewModel = remember { LoginViewModel(authRepository) }

    val isLoggedIn by authRepository.isLoggedIn.collectAsState()

    StockAppTheme {
        if (isLoggedIn) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Orders — em construção", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { /* isLoggedIn muda e recompõe pro placeholder sozinho */ },
                onNavigateToRegister = { /* sample é só login, de propósito */ }
            )
        }
    }
}
