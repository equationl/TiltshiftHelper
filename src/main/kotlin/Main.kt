import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.MinWindowSize
import view.MainScreen
import java.awt.Dimension
import kotlin.math.roundToInt


fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "TiltShiftHelper",
        state = rememberWindowState().apply {
            position = WindowPosition(Alignment.BottomCenter)
        }
    ) {
        window.minimumSize = Dimension(MinWindowSize.width.value.roundToInt(), MinWindowSize.height.value.roundToInt())

        MainScreen()
    }
}