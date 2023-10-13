package state

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import utils.image.color.ImageFilter
import utils.image.file.getUniqueFile
import utils.image.file.saveImg
import utils.image.file.showFileSelector
import utils.image.tiltshift.TiltShift.blur
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser

@Composable
fun rememberApplicationState(
    scope: CoroutineScope,
    trayState: TrayState
) = remember {
    ApplicationState(scope, trayState)
}


class ApplicationState(
    val scope: CoroutineScope,
    val trayState: TrayState
) {
    lateinit var window: ComposeWindow

    var rawImg: BufferedImage? by mutableStateOf(null)
    var showImg: BufferedImage? by mutableStateOf( rawImg )
    var saturation by mutableStateOf(0.8f )
    var luminance by mutableStateOf(0f )
    var hue by mutableStateOf(0f )
    var blurRadius by mutableStateOf(10f)
    var topPercent by mutableStateOf(0.3f)
    var bottomPercent by mutableStateOf(0.3f)

    var isFilterImg by mutableStateOf(true)
    var isBlur by mutableStateOf(true)

    var isShowGuideLine by mutableStateOf(false)

    var outputPath by mutableStateOf("")
    var isUsingSourcePath by mutableStateOf(false)

    var rawImgFile: File? = null


    fun onClickImgChoose() {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.FILES_ONLY,
            onFileSelected = {
                scope.launch(Dispatchers.IO) {
                    val file = it.getOrNull(0)
                    if (file != null) {
                        rawImg = ImageIO.read(file)
                        showImg = rawImg
                        rawImgFile = file
                    }
                }
            }
        )
    }

    fun onClickBuildImg() {
        scope.launch {
            if (isFilterImg) {
                showImg = ImageFilter.hslImage(rawImg!!, saturation, hue, luminance)
            }

            if (isBlur) {
                if (isFilterImg) {
                    showImg = showImg!!.blur(blurRadius, topPercent, bottomPercent)
                }
                else {
                    showImg = rawImg!!.blur(blurRadius, topPercent, bottomPercent)
                }
            }
        }
    }

    fun onClickSave() {
        scope.launch {
            if (isFilterImg) {
                showImg = ImageFilter.hslImage(rawImg!!, saturation, hue, luminance)
            }

            if (isBlur) {
                if (isFilterImg) {
                    showImg = showImg!!.blur(blurRadius, topPercent, bottomPercent)
                }
                else {
                    showImg = rawImg!!.blur(blurRadius, topPercent, bottomPercent)
                }
            }

            val saveFile = if (isUsingSourcePath) rawImgFile!!.getUniqueFile() else File(outputPath).getUniqueFile(rawImgFile!!)
            showImg!!.saveImg(saveFile, 0.8f)
            showTray(
                msg = "保存成功（${saveFile.absolutePath}）"
            )
        }
    }

    // FIXME not work!
    fun showTray(
        msg: String,
        title: String = "通知",
        type: Notification.Type = Notification.Type.Info
    ) {
        val notification = Notification(title, msg, type)
        trayState.sendNotification(notification)
    }
}