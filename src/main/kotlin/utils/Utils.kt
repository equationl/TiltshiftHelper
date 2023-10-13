package utils

import java.text.DecimalFormat

fun Float.to2fStr(): String = DecimalFormat("#.##").format(this)