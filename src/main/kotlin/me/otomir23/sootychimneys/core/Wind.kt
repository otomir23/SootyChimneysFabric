package me.otomir23.sootychimneys.core

import net.minecraft.util.Mth
import kotlin.math.cos
import kotlin.math.sin


data class Wind(
    var angleInDegrees: Double,
    var strength: Double
) {
    var xCoordinate = 0.0
        private set
    var yCoordinate = 0.0
        private set
    private val angleInRadians: Double
        get() = angleInDegrees * (Math.PI / 180)

    fun set(angleDegrees: Double, strength: Double) {
        angleInDegrees = angleDegrees
        this.strength = strength
        xCoordinate = cos(angleInRadians)
        yCoordinate = sin(angleInRadians)
    }

    fun update(addDegrees: Double, addStrength: Double) {
        angleInDegrees = (angleInDegrees + addDegrees) % 360.0
        strength = Mth.clamp(strength + addStrength, 0.0, 1.0)
        xCoordinate = cos(angleInRadians)
        yCoordinate = sin(angleInRadians)
    }

}

