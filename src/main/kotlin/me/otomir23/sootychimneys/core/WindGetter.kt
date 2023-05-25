package me.otomir23.sootychimneys.core

import me.otomir23.sootychimneys.config.CommonConfig
import me.otomir23.sootychimneys.util.RandomOffset.nextFloat
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.world.level.Level
import kotlin.math.exp


object WindGetter {
    val wind = Wind(0.0, 0f)

    fun getWorldTickHandler(): ClientTickEvents.StartWorldTick {
        return ClientTickEvents.StartWorldTick { level ->
            if (!CommonConfig.windEnabled)
                wind.set(0.0, 0f)
            else if (level.isClientSide() && level.gameTime % 5L == 0L)
                updateWind(level)
        }
    }

    private fun updateWind(level: Level) {
        val random = level.random
        val addDegrees = exp(random.nextDouble() * 3.5f) * (if (random.nextBoolean()) -1 else 1)
        wind.update(addDegrees, getWindStrengthChange(level))
    }

    private fun getWindStrengthChange(level: Level): Float {
        val timeOfDay = TimeOfDay.of(level)
        val random = level.getRandom()
        return if (level.isThundering)
            random.nextFloat(-0.10f, 0.11f) + 0.0f.coerceAtLeast(0.4f - wind.strength)
        else if (level.isRaining)
            random.nextFloat(-0.08f, 0.09f) + 0.0f.coerceAtLeast(0.2f - wind.strength) -
                    0.0f.coerceAtLeast(wind.strength - 0.6f)
        else if (timeOfDay !== TimeOfDay.DAY)
            random.nextFloat(
                -0.03f,
                0.04f
            ) + 0.0f.coerceAtLeast(0.008f - wind.strength) - 0.0f.coerceAtLeast(wind.strength - 0.2f)
        else
            random.nextFloat(
                -0.04f,
                0.05f
            ) + 0.0f.coerceAtLeast(0.008f - wind.strength) - 0.0f.coerceAtLeast(wind.strength - 0.3f)
    }
}