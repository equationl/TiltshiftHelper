package view.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import state.ApplicationState

@Composable
fun CommonDialog(state: ApplicationState) {
    Dialog(
        onCloseRequest = { state.onDialogCloseRequest?.invoke() },
        title = state.dialogTitle,
        resizable = false
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(
                text = state.dialogMsg,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                maxLines = 3,
                modifier = Modifier.padding(
                    top = 12.dp, bottom = 25.dp,
                    start = 20.dp, end = 20.dp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Divider()
            Row {
                if (state.dialogCancelBtnText.isNotBlank()) {
                    TextButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        onClick = {
                            state.onDialogCloseRequest?.invoke()
                        }
                    ) {
                        Text(
                            text = state.dialogCancelBtnText,
                            fontSize = 16.sp,
                            maxLines = 1,
                            color = Color(red = 53, green = 128, blue = 186)
                        )
                    }
                }
                if (state.dialogCancelBtnText.isNotBlank() && state.dialogSureBtnText.isNotBlank()) {
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(45.dp)
                    )
                }
                if (state.dialogSureBtnText.isNotBlank()) {
                    TextButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        onClick = {
                            state.onDialogSure?.invoke()
                        }
                    ) {
                        Text(
                            text = state.dialogSureBtnText,
                            fontSize = 16.sp,
                            maxLines = 1,
                            color = Color(red = 53, green = 128, blue = 186)
                        )
                    }
                }
            }
        }
    }
}