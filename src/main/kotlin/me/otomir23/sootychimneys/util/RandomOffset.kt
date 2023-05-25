package me.otomir23.sootychimneys.util

import net.minecraft.util.RandomSource

object RandomOffset {
    fun offset(input: Float, range: Float, random: RandomSource): Float {
        return if (range <= 0.0) input else input + random.nextFloat(range * -1, range)
    }

    fun RandomSource.nextFloat(min: Float, max: Float): Float {
        return this.nextFloat() * (max - min) + min
    }
}
