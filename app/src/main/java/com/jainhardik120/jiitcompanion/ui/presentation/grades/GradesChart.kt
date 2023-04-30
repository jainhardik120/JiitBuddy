package com.jainhardik120.jiitcompanion.ui.presentation.grades

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
fun GraphExample() {
    val infoList = listOf(
        ResultEntity(
            9.3, 181.0, 65, 181.0, 19.5,
            19.5, 8.8, 1, 19.5, 19.5, 19.5, 65,
            181.0, 181.0, 19.5
        ),
        ResultEntity(
            9.0, 181.0, 65, 181.0, 19.5,
            19.5, 9.3, 2, 19.5, 19.5, 19.5, 65,
            181.0, 181.0, 19.5
        ),
        ResultEntity(
            9.2, 181.0, 65, 181.0, 19.5,
            19.5, 7.9, 3, 19.5, 19.5, 19.5, 65,
            181.0, 181.0, 19.5
        )
    )
    GradesChart(
        resultEntities = infoList, modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )

}

@Composable
fun GradesChart(
    modifier: Modifier = Modifier,
    resultEntities: List<ResultEntity> = emptyList(),
    graphColor: Color = MaterialTheme.colorScheme.primary
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val circleColor = MaterialTheme.colorScheme.secondary
    val spacing = 100f
    var upperValue = remember(resultEntities) {
        (resultEntities.maxOfOrNull { it.cgpa * 100 }?.plus(10))?.roundToInt() ?: 0
    }
    val lowerValue = remember(resultEntities) {
        resultEntities.minOfOrNull { it.cgpa * 100 }?.minus(10)?.toInt() ?: 0
    }
    if (((upperValue - lowerValue) % 50) != 0) {
        upperValue += 50 - ((upperValue - lowerValue) % 50)
    }
    Log.d("TAG", "GradesChart: $upperValue $lowerValue")
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = textColor.toArgb()
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }
    Canvas(modifier = modifier) {
        val canvasDrawScope = this
        val height = size.height
        val spacePerHour = (size.width - spacing) / resultEntities.size
        (resultEntities.indices).forEach { i ->
            val info = resultEntities[i]
            val hour = info.stynumber
            val xDist = spacing + i * spacePerHour + (spacePerHour / 3)
            canvasDrawScope.drawLine(
                color = textColor,
                start = Offset(xDist, height - spacing + 15f),
                end = Offset(xDist, height - spacing - 15f)
            )
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    hour.toString(),
                    xDist,
                    size.height - 20,
                    textPaint
                )
            }
        }
        val priceStep = (upperValue - lowerValue) / 5f
        (0..4).forEach { i ->
            val df = DecimalFormat("##.#")
            df.roundingMode = RoundingMode.CEILING
            val label = lowerValue + priceStep * i
            val label2 = (lowerValue + priceStep * i) / 100.0
            val leftRatio = (label - lowerValue) / (upperValue - lowerValue)
            val y1 = height - spacing - (leftRatio * (height - spacing))
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    df.format(label2).toString(),
                    50f,
                    y1,
                    textPaint
                )
            }
            if (i != 0) {
                drawLine(
                    color = textColor,
                    start = Offset(x = spacing - 15, y = y1),
                    end = Offset(y = y1, x = spacing + 15)
                )
            }
        }
        drawLine(
            color = textColor,
            start = Offset(x = spacing, y = height - spacing),
            end = Offset(x = size.width - (spacePerHour / 3), y = height - spacing)
        )
        drawLine(
            color = textColor,
            start = Offset(x = spacing, y = height - spacing),
            end = Offset(x = spacing, y = spacing / 2)
        )
        var lastX = spacing
        var circles : MutableList<Offset> = mutableListOf()
        val strokePath = Path().apply {
            for (i in resultEntities.indices) {
                val info = resultEntities[i]
                val leftRatio = ((info.cgpa * 100) - lowerValue) / (upperValue - lowerValue)
                val y1 = height - spacing - (leftRatio * (height - spacing)).toFloat()
                if (i == 0) {
                    lastX += (spacePerHour / 3)
                    moveTo(lastX, y1)
                } else {
                    lastX += spacePerHour
                    lineTo(lastX, y1)
                }
                circles.add(Offset(lastX, y1))
            }
        }
        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
        for (i in circles){
            canvasDrawScope.drawCircle(circleColor, radius = 4.dp.toPx(), center = i)
        }
    }
}