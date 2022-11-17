package com.krakert.kinematics.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.cos
import kotlin.math.sin

class Segment(
    val id: Int,
    val maxAngleRot: Double = 45.0,
    _length:  MutableState<Double> = mutableStateOf(115.0),
    _angle: MutableState<Double> = mutableStateOf(0.0),
    _mountAngle: MutableState<Double> = mutableStateOf(0.0),
    _mountPoint: MutableState<ArrayList<Double>> = mutableStateOf(arrayListOf(0.0, 0.0, 0.0)),// X Y Z
    _endPoint: MutableState<ArrayList<Double>> = mutableStateOf(arrayListOf(0.0, 0.0, 0.0))   // X Y Z
) {
    var angle by mutableStateOf(_angle.value)
    private var mountAngle by mutableStateOf(_mountAngle.value)
    var length by mutableStateOf(_length.value)
    var startX = mutableStateOf(_mountPoint.value[0]).value
    var startY = mutableStateOf(_mountPoint.value[1]).value
    var endX = mutableStateOf(_endPoint.value[0]).value
    var endY = mutableStateOf(_endPoint.value[1]).value


    fun calcEndpoint(){
        endX = startX + length * sin(Math.toRadians(angle + mountAngle))
        endY = startY + length * cos(Math.toRadians(angle + mountAngle))
    }

    fun setMountPoint(NewMountPoint: List<Double>, NewMountAngle: Double){
        startX = NewMountPoint[0]
        startY = NewMountPoint[1]
        mountAngle = NewMountAngle
    }

    fun getEndpoints(): Array<Double> {
        return arrayOf(endX, endY)
    }

    fun getCombinedAngle(): Double {
        return mountAngle + angle
    }

    override fun toString(): String {
        return String.format("segment: %d | start = X: %06.2f, Y: %06.2f | End = X: %06.2f, Y: %06.2f",
                id,
                startX,
                startY,
                endX,
                endY)
    }
}