package view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import utils.image.color.ImageFilter
import utils.image.tilt_shift.TiltShift.blur
import utils.to2fStr
import java.io.File
import javax.imageio.ImageIO

@Composable
fun MainScreen() {
    val scop = rememberCoroutineScope()

    val rawImg = remember { ImageIO.read(File("C:\\Users\\DELL\\Desktop\\test\\test.jpg")) }
    var showImg by mutableStateOf(remember { rawImg })
    var saturation by mutableStateOf(remember { 0.8f })
    var blurRadius by mutableStateOf(remember { 10f })

    var isChangeSaturation by remember { mutableStateOf(true) }
    var isBlur by remember { mutableStateOf(true) }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier.weight(1f).padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
            ) {
                Image(
                    bitmap = showImg.toComposeImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Card(
                modifier = Modifier.weight(1f).padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(isChangeSaturation, onCheckedChange = {
                            isChangeSaturation = it
                        })
                        Text(
                            text = "饱和度：",
                            color = if (isChangeSaturation) Color.Unspecified else Color.LightGray
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = saturation,
                                onValueChange = {
                                    saturation = it
                                },
                                enabled = isChangeSaturation,
                                modifier = Modifier.weight(9f)
                            )
                            Text(
                                text = saturation.to2fStr(),
                                color = if (isChangeSaturation) Color.Unspecified else Color.LightGray,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(isBlur, onCheckedChange = {
                            isBlur = it
                        })
                        Text(
                            "模糊度：",
                            color = if (isBlur) Color.Unspecified else Color.LightGray
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = blurRadius,
                                onValueChange = {
                                    blurRadius = it
                                },
                                enabled = isBlur,
                                valueRange = 0f..50f,
                                modifier = Modifier.weight(9f)
                            )
                            Text(
                                blurRadius.to2fStr(),
                                color = if (isBlur) Color.Unspecified else Color.LightGray,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Button(onClick = {
                        scop.launch {
                            if (isChangeSaturation) {
                                showImg = ImageFilter.hslImage(rawImg, saturation, 0f, 0f)
                            }

                            if (isBlur) {
//                                val filter = LensBlurFilter().apply {
//                                    this.radius = blurRadius
//                                }
//                                showImg = filter.filter(if (isChangeSaturation) showImg else rawImg, BufferedImage(rawImg.width, rawImg.height, BufferedImage.TYPE_INT_RGB))

                                if (isChangeSaturation) {
                                    showImg = showImg.blur(blurRadius, 0.3f, 0.3f)
                                }
                                else {
                                    showImg = rawImg.blur(blurRadius, 0.3f, 0.3f)
                                }
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