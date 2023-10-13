package view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import state.ApplicationState
import utils.image.file.dropFileTarget
import utils.image.file.legalSuffixList
import view.widget.CommonDialog
import java.io.File
import javax.imageio.ImageIO

@Composable
fun MainScreen(
    state: ApplicationState
) {
    state.window.contentPane.dropTarget = dropFileTarget {
        state.scope.launch(Dispatchers.IO) {
            val filePath = it.getOrNull(0)
            if (filePath != null) {
                val file = File(filePath)
                if (file.isFile && file.extension in legalSuffixList) {
                    state.rawImg = ImageIO.read(file)
                    state.showImg = state.rawImg
                    state.rawImgFile = file
                }
            }
        }
    }

    MaterialTheme {
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PreviewContent(state, Modifier.weight(1f))
            ControlContent(state, Modifier.weight(1f))
        }


        if (state.dialogMsg.isNotBlank()) {
            CommonDialog(state)
        }
    }
}