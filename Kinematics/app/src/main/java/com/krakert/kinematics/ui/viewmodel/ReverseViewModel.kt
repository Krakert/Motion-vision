package com.krakert.kinematics.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.krakert.kinematics.models.Segment
import com.krakert.kinematics.ui.screens.number_of_segments_reverse
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

// Radius of the destination, when within X radius, stop calculating
const val radius: Double = 5.0

class ReverseViewModel : ViewModel() {

    // Live data used to draw arm
    private val _segments = MutableLiveData(getArmSegments())
    val segments: LiveData<List<Segment>>
        get() = _segments

    // Live data used to draw destination point of the arm
    private val _newEndpoint = MutableLiveData(arrayListOf(0.0F, 0.0F, 0.0F))    //X Y Z
    val newEndpoint: LiveData<ArrayList<Float>>
        get() = _newEndpoint

    // Build a arm of X segments
    private fun getArmSegments() = List(number_of_segments_reverse) {
        Segment(id = it)
    }

    // Forwards kinematics, calculate new end point of the arm
    fun calculateEndpoint() {
        val endPos = arrayListOf(0.0, 0.0, 0.0) // X Y Z
        var endAngle = 0.0
        _segments.value?.forEach {
            it.setMountPoint(endPos, endAngle)
            it.calcEndpoint()
            endPos[0] = it.getEndpoints()[0]
            endPos[1] = it.getEndpoints()[1]
            endAngle = it.getCombinedAngle()
        }
    }

    // Inverse kinematics
    fun calculateArm() {
        // Max amount of tries of calculations
        val limit = 25
        var index = 0
        val copySegments = _segments.value

        while (index < limit) {
            if (copySegments != null) {
                for (i in copySegments.lastIndex downTo 0) {
                    calculateEndpoint()

                    println("Going to: X ${_newEndpoint.value?.get(0)}, Y: ${_newEndpoint.value?.get(1)}")
                    println("--------------------------------------")
                    println("ID: ${copySegments[i].id}, index $i")

                    val endpointArm: Segment = copySegments[copySegments.lastIndex] // E
                    val prefJoint = if (i != 0)
                        copySegments[i - 1]                                         // Q
                    else
                        copySegments[0]                                             // Q (Mounting point)

                    // new X / Y point of E, shifting vectors, only when not at the mounting point (if i == 0)
                    val newXe = if (i != 0)
                        endpointArm.endX - prefJoint.endX
                    else
                        endpointArm.endX

                    val newYe = if (i != 0)
                        endpointArm.endY - prefJoint.endY
                    else
                        endpointArm.endY

                    // new X / Y point of D, shifting vectors, only when not at the mounting point (if i == 0)
                    val newXd = if (i != 0) {
                        _newEndpoint.value?.get(0)?.minus(prefJoint.endX)
                    } else {
                        _newEndpoint.value?.get(0)?.toDouble()
                    } ?: 0.0

                    val newYd = if (i != 0) {
                        _newEndpoint.value?.get(1)?.minus(prefJoint.endY)
                    } else {
                        _newEndpoint.value?.get(1)?.toDouble()
                    } ?: 0.0

                    // Length arm Q -> D
                    val lengthQD = sqrt((newXd * newXd) + (newYd * newYd))
                    println("Length arm Q -> D: $lengthQD")

                    // Length arm Q -> E
                    val lengthQE = sqrt((newXe * newXe) + (newYe * newYe))
                    println("Length arm Q -> E: $lengthQE")

                    // Dot product = ||E|| * ||D|| * cos(e) = dotProduct / (||E|| * ||D||)
                    val dotProduct = (newXe * newXd) + (newYe * newYd)
                    println("Dot product: $dotProduct")

                    // angle ==  invert cos (dotProduct / (||E|| * ||D||))
                    val angle = Math.toDegrees(acos(dotProduct / (lengthQD * lengthQE)))
                    println("angle: $angle")

                    // cross product
                    var crossProduct = (newXe * newYd) - (newYe * newXd)
                    crossProduct /= abs(crossProduct)
                    println("cross product: $crossProduct")

                    // With the help of it cross product, and of subtract the calculated angle
                    // from the angle that is already stored in the Segment
                    if (crossProduct < 0) {
                        // When the angle is greater then the set limit for the join, only rotate the limit, not the full angle
                        if (angle > copySegments[i].maxAngleRot){
                            Log.d("angle", "angle to big, want to rotate +$angle, is allowed: ${copySegments[i].maxAngleRot}")
                            copySegments[i].angle = copySegments[i].angle + copySegments[i].maxAngleRot
                        } else {
                            copySegments[i].angle = copySegments[i].angle + angle
                        }
                    } else {
                        // When the angle is greater then the set limit for the join, only rotate the limit, not the full angle
                        if (angle > copySegments[i].maxAngleRot){
                            Log.d("angle", "angle to big, want to rotate -$angle, is allowed: ${copySegments[i].maxAngleRot}")
                            copySegments[i].angle = copySegments[i].angle - copySegments[i].maxAngleRot
                        } else {
                            copySegments[i].angle = copySegments[i].angle - angle
                        }
                    }
                }
                _segments.value = copySegments
            }
            if (onTarget()){
                // No need to make more calculations, end point is close enough
                break
            }
            index += 1
        }
        // How many tries needed to complete calculation?
        println("done in $index iterations")
    }

    // Set the end points for the arm
    fun updateNewPoint(X: Float, Y: Float) {
        _newEndpoint.value?.set(0, X)
        _newEndpoint.value?.set(1, Y)
    }

    // Check if the endpoint of the arm is within a radius of the destination
    private fun onTarget(): Boolean{
        val segments = _segments.value
        val endX = segments?.get(segments.lastIndex)?.endX ?: 0.0
        val endY = segments?.get(segments.lastIndex)?.endY ?: 0.0
        val disX = _newEndpoint.value?.get(0) ?: 0f
        val disY = _newEndpoint.value?.get(1) ?: 0f
        return (((endX - disX) * (endX - disX))  + ((endY - disY) * (endY - disY)) < (radius * radius))
    }
}