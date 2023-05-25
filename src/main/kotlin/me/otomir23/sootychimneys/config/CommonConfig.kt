package me.otomir23.sootychimneys.config

import eu.midnightdust.lib.config.MidnightConfig

class CommonConfig : MidnightConfig() {
    companion object {
        @JvmField
        @Entry(min = 0.0, max = 1.0)
        var smokeStrength = 1.0

        @JvmField
        @Entry(min = 0.0, max = 1.0)
        var dirtyChance = 0.05

        @JvmField
        @Entry
        var windEnabled = true

        @JvmField
        @Entry(min = 0.0, max = 1.0)
        var windStrengthMultiplier = 0.05
    }
}