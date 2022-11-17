package com.krakert.kinematics.ui.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.krakert.kinematics.models.Segment
import com.krakert.kinematics.ui.screens.number_of_segments_forwards

class ForwardViewModel : ViewModel() {


    private val _segments = getArmSegments().toMutableStateList()
    val segments: List<Segment>
        get() = _segments

    fun setAngle(segment: Segment, angle: Float) {
        segment.angle = angle.toDouble()
    }

    fun setLength(segment: Segment, length: Float) {
        segment.length = length.toDouble()
    }

    private fun getArmSegments() = List(number_of_segments_forwards) {
        Segment(id = it)
    }

    fun calculateEndpoint() {

        val endPos = arrayListOf(0.0, 0.0, 0.0) // X Y Z
        var endAngle = 0.0
        segments.forEach {
            it.setMountPoint(endPos, endAngle)
            it.calcEndpoint()
            endPos[0] = it.getEndpoints()[0]
            endPos[1] = it.getEndpoints()[1]
            endAngle = it.getCombinedAngle()
        }
    }
}