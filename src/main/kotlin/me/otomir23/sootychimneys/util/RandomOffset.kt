package me.otomir23.sootychimneys.util

import net.minecraft.util.RandomSource

object RandomOffset {
    fun offset(input: Double, range: Double, random: RandomSource): Double {
        return if (range <= 0.0) input else input + random.nextDouble(range * -1, range)
    }

    fun RandomSource.nextDouble(min: Double, max: Double): Double {
        return this.nextDouble() * (max - min) + min
    }
}
