package com.krakert.kinematics.ui.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.krakert.kinematics.ui.viewmodel.ReverseViewModel

const val number_of_segments_reverse = 6


@Composable
fun Reverse(viewModel: ReverseViewModel) {
    viewModel.calculateEndpoint()
    DrawBot(viewModel = viewModel)
}

@Composable
private fun DrawBot(viewModel: ReverseViewModel) {

    val newArm by remember { mutableStateOf(viewModel.segments.value) }
    val endpoint = viewModel.newEndpoint.observeAsState().value

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    viewModel.updateNewPoint(offset.x - (size.width / 2),
                        (size.height / 2) - offset.y)
                    viewModel.calculateArm()
                }
            }
    ) {
        val canvasHalveWidth = size.width / 2
        try {
            drawCircle(
                center =
                Offset(
                    x = (endpoint?.get(0) ?: 0f) + canvasHalveWidth,
                    y = size.height / 2 - (endpoint?.get(1) ?: 0f)
                ),
                color = Color.Cyan,
                radius = 40F
            )
            newArm?.forEach {
                drawLine(
                    start =
                    Offset(
                        x = it.startX.toFloat() + canvasHalveWidth,
                        y = size.height / 2 - it.startY.toFloat()
                    ),
                    end =
                    Offset(
                        x = it.endX.toFloat() + canvasHalveWidth,
                        y = size.height / 2 - it.endY.toFloat()
                    ),
                    color = Color.Red,
                    strokeWidth = 20F
                )
            }
            newArm?.forEach {
                drawCircle(
                    center =
                    Offset(
                        x = it.endX.toFloat() + canvasHalveWidth,
                        y = size.height / 2 - it.endY.toFloat()
                    ),
                    color = Color.Blue,
                    radius = 25F
                )
            }
            newArm?.forEach {
                drawCircle(
                    center =
                    Offset(
                        x = it.endX.toFloat() + canvasHalveWidth,
                        y = size.height / 2 - it.endY.toFloat()
                    ),
                    color = Color.Blue,
                    radius = 25F
                )
            }
            drawCircle(
                center =
                Offset(
                    x = (newArm?.get(0)?.startX?.toFloat() ?: 0.0f) + canvasHalveWidth,
                    y = size.height / 2 - (newArm?.get(0)?.startY?.toFloat() ?: 0.0f)
                ),
                color = Color.Blue,
                radius = 25F
            )
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            Log.d(TAG, newArm.toString())
        }
    }
}