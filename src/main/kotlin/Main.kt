import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.*
import state.rememberApplicationState
import ui.MinWindowSize
import view.MainScreen
import java.awt.Dimension
import kotlin.math.roundToInt


fun main() = application {

    val trayState = rememberTrayState()

    val applicationState = rememberApplicationState(
        rememberCoroutineScope(),
        trayState
    )

    Tray(
        state = trayState,
        icon = painterResource("launcher.ico"),
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "TiltShiftHelper",
        state = rememberWindowState().apply {
            position = WindowPosition(Alignment.BottomCenter)
        }
    ) {
        window.minimumSize = Dimension(MinWindowSize.width.value.roundToInt(), MinWindowSize.height.value.roundToInt())

        applicationState.window = window

        MainScreen(applicationState)
    }
}