package view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
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
    var luminance by mutableStateOf(remember { 0f })
    var hue by mutableStateOf(remember { 0f })
    var blurRadius by mutableStateOf(remember { 10f })
    var topPercent by mutableStateOf(remember { 0.3f })
    var bottomPercent by mutableStateOf(remember { 0.3f })

    var isChangeSaturation by remember { mutableStateOf(true) }
    var isBlur by remember { mutableStateOf(true) }

    var isShowGuideLine by remember { mutableStateOf(true) }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier.weight(1f).padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        bitmap = showImg.toComposeImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.drawWithContent {
                            drawContent()
                            if (isShowGuideLine) {
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset(x = 0f, y = size.height * topPercent),
                                    end = Offset(x = size.width, y = size.height * topPercent),
                                    strokeWidth = 3f
                                )
                                drawLine(
                                    color = Color.Cyan,
                                    start = Offset(x = 0f, y = size.height * topPercent),
                                    end = Offset(x = size.width, y = size.height * topPercent)
                                )
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset(x = 0f, y = size.height * (1f - bottomPercent)),
                                    end = Offset(x = size.width, y = size.height * (1f - bottomPercent)),
                                    strokeWidth = 3f
                                )
                                drawLine(
                                    color = Color.Cyan,
                                    start = Offset(x = 0f, y = size.height * (1f - bottomPercent)),
                                    end = Offset(x = size.width, y = size.height * (1f - bottomPercent))
                                )
                            }
                        },
                    )
                }
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
                            text = "饱和度增益：",
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
                                modifier = Modifier.weight(9f),
                                valueRange = -1f..1f
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
                        Checkbox(isChangeSaturation, onCheckedChange = {
                            isChangeSaturation = it
                        })
                        Text(
                            text = "色相增益：",
                            color = if (isChangeSaturation) Color.Unspecified else Color.LightGray
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = hue,
                                onValueChange = {
                                    hue = it
                                },
                                enabled = isChangeSaturation,
                                modifier = Modifier.weight(9f),
                                valueRange = -1f..1f
                            )
                            Text(
                                text = hue.to2fStr(),
                                color = if (isChangeSaturation) Color.Unspecified else Color.LightGray,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(isChangeSaturation, onCheckedChange = {
                            isChangeSaturation = it
                        })
                        Text(
                            text = "亮度增益：",
                            color = if (isChangeSaturation) Color.Unspecified else Color.LightGray
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = luminance,
                                onValueChange = {
                                    luminance = it
                                },
                                enabled = isChangeSaturation,
                                modifier = Modifier.weight(9f),
                                valueRange = -1f..1f
                            )
                            Text(
                                text = luminance.to2fStr(),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("顶部位置：",)
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = topPercent,
                                onValueChange = {
                                    isShowGuideLine = true

                                    if (topPercent + bottomPercent < 1f) {
                                        topPercent = it
                                    }
                                    else {
                                        topPercent = 1f - bottomPercent - 0.01f
                                    }
                                },
                                modifier = Modifier.weight(9f)
                            )
                            Text(
                                topPercent.to2fStr(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("底部位置：",)
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = bottomPercent,
                                onValueChange = {
                                    isShowGuideLine = true

                                    bottomPercent = if (topPercent + bottomPercent < 1f) {
                                        it
                                    } else {
                                        1f - topPercent - 0.01f
                                    }
                                },
                                modifier = Modifier.weight(9f)
                            )
                            Text(
                                bottomPercent.to2fStr(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Button(onClick = {
                        scop.launch {
                            isShowGuideLine = false

                            if (isChangeSaturation) {
                                showImg = ImageFilter.hslImage(rawImg, saturation, 0f, 0f)
                            }

                            if (isBlur) {
                                if (isChangeSaturation) {
                                    showImg = showImg.blur(blurRadius, topPercent, bottomPercent)
                                }
                                else {
                                    showImg = rawImg.blur(blurRadius, topPercent, bottomPercent)
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