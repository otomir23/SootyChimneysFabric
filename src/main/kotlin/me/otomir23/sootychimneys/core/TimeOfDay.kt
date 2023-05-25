package me.otomir23.sootychimneys.core

import net.minecraft.world.level.Level


enum class TimeOfDay {
    MORNING,
    DAY,
    EVENING,
    NIGHT;

    companion object {
        private fun fromSunAngle(degrees: Double): TimeOfDay {
            return if (degrees <= 290 && degrees > 270) MORNING else if (degrees <= 270 && degrees > 95) NIGHT else if (degrees <= 95 && degrees > 75) EVENING else DAY
        }

        fun of(level: Level): TimeOfDay {
            return fromSunAngle(level.getSunAngle(level.dayTime.toFloat()) * 180 / Math.PI)
        }
    }
}