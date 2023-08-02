package me.otomir23.sootychimneys.core

import me.otomir23.sootychimneys.config.CommonConfig
import me.otomir23.sootychimneys.util.RandomOffset.nextDouble
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.world.level.Level
import kotlin.math.exp


object WindGetter {
    val wind = Wind(0.0, 0.0)

    fun getWorldTickHandler(): ClientTickEvents.StartWorldTick {
        return ClientTickEvents.StartWorldTick { level ->
            if (!CommonConfig.windEnabled)
                wind.set(0.0, 0.0)
            else if (level.isClientSide() && level.gameTime % 5L == 0L)
                updateWind(level)
        }
    }

    private fun updateWind(level: Level) {
        val random = level.random
        val addDegrees = exp(random.nextDouble() * 3.5) * (if (random.nextBoolean()) -1 else 1)
        wind.update(addDegrees, getWindStrengthChange(level))
    }

    private fun getWindStrengthChange(level: Level): Double {
        val timeOfDay = TimeOfDay.of(level)
        val random = level.getRandom()
        return if (level.isThundering)
            random.nextDouble(-0.10, 0.11) + 0.0.coerceAtLeast(0.4 - wind.strength)
        else if (level.isRaining)
            random.nextDouble(-0.08, 0.09) + 0.0.coerceAtLeast(0.2 - wind.strength) -
                    0.0.coerceAtLeast(wind.strength - 0.6)
        else if (timeOfDay !== TimeOfDay.DAY)
            random.nextDouble(
                -0.03,
                0.04
            ) + 0.0.coerceAtLeast(0.008 - wind.strength) - 0.0.coerceAtLeast(wind.strength - 0.2)
        else
            random.nextDouble(
                -0.04,
                0.05
            ) + 0.0.coerceAtLeast(0.008 - wind.strength) - 0.0.coerceAtLeast(wind.strength - 0.3)
    }
}