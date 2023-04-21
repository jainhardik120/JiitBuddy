package com.jainhardik120.jiitcompanion.ui.presentation.grades

import android.graphics.Paint
import android.graphics.Path
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import kotlin.math.round
import kotlin.math.roundToInt

@Preview
@Composable
fun GraphExample() {
    val infoList = listOf<ResultEntity>(
        ResultEntity(
            9.3, 181.0, 65, 181.0, 19.5,
            19.5, 8.8, 1, 19.5, 19.5, 19.5, 65,
            181.0, 181.0, 19.5
        ),
        ResultEntity(
            9.3, 181.0, 65, 181.0, 19.5,
            19.5, 9.3, 1, 19.5, 19.5, 19.5, 65,
            181.0, 181.0, 19.5
        ),
        ResultEntity(
            9.3, 181.0, 65, 181.0, 19.5,
            19.5, 7.9, 1, 19.5, 19.5, 19.5, 65,
            181.0, 181.0, 19.5
        )
    )
    GradesChart(
        infoList, modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    )

}

@Composable
fun GradesChart(
    infos: List<ResultEntity> = emptyList(),
    modifier: Modifier = Modifier,
    graphColor: Color = Color.Green
) {
    val spacing = 100f
    val transparentGraphColor = remember {
        graphColor.copy(alpha = 0.5f)
    }
    val upperValue = 100
    val lowerValue = 0
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }
    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - spacing) / infos.size
//        (0 until infos.size - 1 step 2).forEach { i ->
//            val info = infos[i]
//            val hour = info.stynumber
//            drawContext.canvas.nativeCanvas.apply {
//                drawText(
//                    hour.toString(),
//                    spacing + i * spacePerHour,
//                    size.height - 5,
//                    textPaint
//                )
//            }
//        }
        val priceStep = (upperValue - lowerValue) / 5f
//        (0..4).forEach { i ->
//            drawContext.canvas.nativeCanvas.apply {
//                drawText(
//                    round(lowerValue + priceStep * i).toString(),
//                    30f,
//                    size.height - spacing - i * size.height / 5f,
//                    textPaint
//                )
//            }
//        }
        var lastX = 0f
        val strokePath = androidx.compose.ui.graphics.Path().apply {
            val height = size.height
            for (i in infos.indices) {
                val info = infos[i]
                val nextInfo = infos.getOrNull(i + 1) ?: infos.last()
                val leftRatio = ((info.sgpa * 10) - lowerValue) / (upperValue - lowerValue)
                val rightRatio = ((nextInfo.sgpa * 10) - lowerValue) / (upperValue - lowerValue)

                val x1 = spacing + i * spacePerHour
                val y1 = height - spacing - (leftRatio * height).toFloat()
                val x2 = spacing + (i + 1) * spacePerHour
                val y2 = height - spacing - (rightRatio * height).toFloat()
                if (i == 0) {
                    moveTo(x1, y1)
                }
                lastX = (x1 + x2) / 2f
                quadraticBezierTo(
                    x1, y1, lastX, (y1 + y2) / 2f
                )
            }
        }
//        val fillPath = Path(strokePath.asAndroidPath())
//            .asComposePath()
//            .apply {
//                lineTo(lastX, size.height - spacing)
//                lineTo(spacing, size.height - spacing)
//                close()
//            }
//        drawPath(
//            path = fillPath,
//            brush = Brush.verticalGradient(
//                colors = listOf(
//                    transparentGraphColor,
//                    Color.Transparent
//                ),
//                endY = size.height - spacing
//            )
//        )
        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}