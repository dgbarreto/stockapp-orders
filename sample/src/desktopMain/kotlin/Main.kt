import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.danilobarreto.stockapp.orders.sample.SampleApp

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Orders Sample") {
        SampleApp()
    }
}
