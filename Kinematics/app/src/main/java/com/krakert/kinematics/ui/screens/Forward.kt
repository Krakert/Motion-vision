package com.krakert.kinematics.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.krakert.kinematics.ui.viewmodel.ForwardViewModel

const val number_of_segments_forwards = 4

@Composable
fun Forward(viewModel: ForwardViewModel) {
    viewModel.calculateEndpoint()
    val segments = viewModel.segments
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                segments.forEach { segment ->
                    item {
                        Row(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)) {
                            Text(text = buildString {
                                append("Segment ${segment.id} ")
                                    .append(String.format("Angle: %.2f° ",
                                        segment.angle))
                                    .append(String.format("Length: %.2f cm",
                                        segment.length))
                            })
                        }
                        Row(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)) {
                            Slider(
                                valueRange = -90f..90f,
                                value = segment.angle.toFloat(),
                                onValueChange = { newAngle ->
                                    viewModel.setAngle(segment, newAngle)
                                    viewModel.calculateEndpoint()
                                })
                        }
                        Row(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)) {
                            Slider(
                                valueRange = 100f..300f,
                                value = segment.length.toFloat(),
                                onValueChange = { newLength ->
                                    viewModel.setLength(segment, newLength)
                                    viewModel.calculateEndpoint()
                                })
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format("E = X: %2.2f, Y: %2.1f",
                            segments[segments.lastIndex].endX,
                            segments[segments.lastIndex].endY)
                    )
                }
                DrawBot(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun DrawBot(viewModel: ForwardViewModel) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .height(105.dp)
            .padding(bottom = 8.dp)
    ) {

        val segments = viewModel.segments
        val canvasWidth = size.width
        val canvasHalveWidth = canvasWidth / 2
        val canvasHeight = size.height

        segments.forEach {
            drawLine(
                start =
                Offset(
                    x = it.startX.toFloat() + canvasHalveWidth,
                    y = canvasHeight - it.startY.toFloat()
                ),
                end =
                Offset(
                    x = it.endX.toFloat() + canvasHalveWidth,
                    y = canvasHeight - it.endY.toFloat()
                ),
                color = Color.Red,
                strokeWidth = 20F
            )
        }
        segments.forEach {
            drawCircle(
                center =
                Offset(
                    x = it.endX.toFloat() + canvasHalveWidth,
                    y = canvasHeight - it.endY.toFloat()
                ),
                color = Color.Blue,
                radius = 25F
            )
        }

        drawCircle(
            center =
            Offset(
                x = segments[0].startX.toFloat() + canvasHalveWidth,
                y = canvasHeight - segments[0].startY.toFloat()
            ),
            color = Color.Blue,
            radius = 25F
        )
    }
}