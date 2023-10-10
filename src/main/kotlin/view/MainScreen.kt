package view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import utils.image.blur.LensBlurFilter
import utils.image.color.ImageFilter
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Composable
fun MainScreen() {
    val scop = rememberCoroutineScope()

    var rawImg = remember { ImageIO.read(File("C:\\Users\\DELL\\Desktop\\test\\test.jpg")) }
    var showImg by mutableStateOf(remember { rawImg })
    var saturation by mutableStateOf(remember { "0.8" })
    var radius by mutableStateOf(remember { "10" })

    var isChangeSaturation by remember { mutableStateOf(true) }
    var isBlur by remember { mutableStateOf(true) }

    MaterialTheme {
        Column {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
            ) {
                Image(
                    showImg.toComposeImageBitmap(),
                    null
                )
            }

            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
            ) {
                Column {
                    Row {
                        Text("饱和度：")
                        TextField(
                            value = saturation,
                            onValueChange = {
                                saturation = it
                            }
                        )
                        Checkbox(isChangeSaturation, onCheckedChange = {
                            isChangeSaturation = it
                        })
                    }
                    Row {
                        Text("模糊度：")
                        TextField(
                            value = radius,
                            onValueChange = {
                                radius = it
                            }
                        )
                        Checkbox(isBlur, onCheckedChange = {
                            isBlur = it
                        })
                    }
                    Button(onClick = {
                        scop.launch(Dispatchers.IO) {
                            if (isChangeSaturation) {
                                showImg = ImageFilter.hslImage(rawImg, saturation.toFloat(), 0f, 0f)
                            }

                            if (isBlur) {
                                val filter = LensBlurFilter().apply {
                                    this.radius = radius.toFloat()
                                }
                                showImg = filter.filter(showImg, BufferedImage(rawImg.width, rawImg.height, BufferedImage.TYPE_INT_RGB))
                            }
                        }
                    }) {
                        Text("生成")
                    }
                }
            }
        }
    }
}