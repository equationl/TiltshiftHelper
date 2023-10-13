package view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import state.ApplicationState
import utils.image.file.showFileSelector
import utils.to2fStr
import javax.swing.JFileChooser

@Composable
fun ControlContent(
    state: ApplicationState,
    modifier: Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isFilterImg, onCheckedChange = {
                    state.isFilterImg = it
                })
                Text("图像处理：")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "饱和度增益：",
                    color = if (state.isFilterImg) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.saturation,
                        onValueChange = {
                            state.saturation = it
                        },
                        enabled = state.isFilterImg,
                        modifier = Modifier.weight(8f),
                        valueRange = -1f..1f
                    )
                    Text(
                        text = state.saturation.to2fStr(),
                        color = if (state.isFilterImg) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "色相增益：",
                    color = if (state.isFilterImg) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.hue,
                        onValueChange = {
                            state.hue = it
                        },
                        enabled = state.isFilterImg,
                        modifier = Modifier.weight(8f),
                        valueRange = -1f..1f
                    )
                    Text(
                        text = state.hue.to2fStr(),
                        color = if (state.isFilterImg) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "亮度增益：",
                    color = if (state.isFilterImg) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.luminance,
                        onValueChange = {
                            state.luminance = it
                        },
                        enabled = state.isFilterImg,
                        modifier = Modifier.weight(8f),
                        valueRange = -1f..1f
                    )
                    Text(
                        text = state.luminance.to2fStr(),
                        color = if (state.isFilterImg) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(state.isBlur, onCheckedChange = {
                    state.isBlur = it
                })
                Text("模拟镜头景深：")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "模糊度：",
                    color = if (state.isBlur) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.blurRadius,
                        onValueChange = {
                            state.blurRadius = it
                        },
                        enabled = state.isBlur,
                        valueRange = 0f..50f,
                        modifier = Modifier.weight(8f)
                    )
                    Text(
                        state.blurRadius.to2fStr(),
                        color = if (state.isBlur) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "顶部位置：",
                    color = if (state.isBlur) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.topPercent,
                        onValueChange = {
                            state.isShowGuideLine = true

                            if (state.topPercent + state.bottomPercent < 1f) {
                                state.topPercent = it
                            }
                            else {
                                state.topPercent = 1f - state.bottomPercent - 0.01f
                            }
                        },
                        modifier = Modifier.weight(8f),
                        enabled = state.isBlur,
                    )
                    Text(
                        state.topPercent.to2fStr(),
                        color = if (state.isBlur) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "底部位置：",
                    color = if (state.isBlur) Color.Unspecified else Color.LightGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = state.bottomPercent,
                        onValueChange = {
                            state.isShowGuideLine = true

                            state.bottomPercent = if (state.topPercent + state.bottomPercent < 1f) {
                                it
                            } else {
                                1f - state.topPercent - 0.01f
                            }
                        },
                        modifier = Modifier.weight(8f),
                        enabled = state.isBlur,
                    )
                    Text(
                        state.bottomPercent.to2fStr(),
                        color = if (state.isBlur) Color.Unspecified else Color.LightGray,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("保存位置：")
                OutlinedTextField(
                    value = state.outputPath,
                    onValueChange = { state.outputPath = it },
                    enabled = !state.isUsingSourcePath,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                Button(
                    onClick = {
                        showFileSelector(
                            isMultiSelection = false,
                            selectionMode = JFileChooser.DIRECTORIES_ONLY,
                            selectionFileFilter = null
                        ) {
                            state.outputPath = it[0].absolutePath
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = !state.isUsingSourcePath
                ) {
                    Text("选择")
                }
                Checkbox(
                    checked = state.isUsingSourcePath,
                    onCheckedChange = {
                        state.isUsingSourcePath = it
                        state.outputPath = if (it) "原路径" else ""
                    }
                )
                Text("输出至原路径", fontSize = 12.sp)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        state.isShowGuideLine = false
                        state.onClickBuildImg()
                    },
                    enabled = state.rawImg != null
                ) {
                    Text("预览")
                }

                Button(
                    onClick = {
                        state.isShowGuideLine = false
                        state.onClickSave()
                    },
                    enabled = state.rawImg != null
                ) {
                    Text("保存")
                }
            }
        }
    }
}