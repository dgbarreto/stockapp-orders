package com.danilobarreto.stockapp.orders.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
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
import com.danilobarreto.stockapp.orders.presentation.OrderBottomSheet
import com.danilobarreto.stockapp.orders.presentation.OrderFormViewModel
import com.danilobarreto.stockapp.orders.presentation.OrdersScreen
import com.danilobarreto.stockapp.orders.presentation.OrdersViewModel
import kotlinx.coroutines.launch

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

    val isLoggedIn by authRepository.isLoggedIn.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    StockAppTheme {
        if (isLoggedIn) {
            // Só criados depois do login: OrdersViewModel dispara a primeira chamada de rede
            // no próprio init{} (ver OrdersViewModel.kt), e se isso acontecesse antes do login
            // (como era com remember incondicional lá em cima, antes desse ajuste), o Ktor
            // tentava autenticar sem token nessa primeira chamada e o cache do plugin de Auth
            // ficava capenga mesmo depois do token válido já estar salvo no TokenStorage —
            // era a causa do 401 na tela de Ordens mesmo já logado.
            val ordersViewModel = remember { OrdersViewModel(ordersRepository) }
            val orderFormViewModel = remember { OrderFormViewModel(ordersRepository) }

            // OrdersScreen fica sempre visível por baixo — o formulário abre como bottom
            // sheet por cima (mesmo padrão do app real, ver OrderBottomSheet.kt usado em
            // AppNavHost.kt), não trocando de tela inteira como era com OrderFormScreen antes.
            OrdersScreen(
                viewModel = ordersViewModel,
                onNewOrder = { showForm = true },
                // Só existe no sample: a tela de Ordens de verdade não tem botão de
                // voltar ainda (nenhuma rota do app real usa OrdersScreen por enquanto,
                // ver AppNavHost.kt) — aqui serve pra deslogar e voltar pro Login, mesmo
                // padrão usado no sample do stockapp-quotes.
                onBack = { coroutineScope.launch { authRepository.logout() } },
            )

            if (showForm) {
                OrderBottomSheet(
                    viewModel = orderFormViewModel,
                    onDismiss = { showForm = false },
                    onSaved = {
                        showForm = false
                        ordersViewModel.load()
                    },
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