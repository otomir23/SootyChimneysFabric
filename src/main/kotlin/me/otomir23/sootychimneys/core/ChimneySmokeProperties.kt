package me.otomir23.sootychimneys.core

import net.minecraft.world.phys.Vec3

data class ChimneySmokeProperties(
    val particleOriginY: Double,
    val particleSpread: Vec3,
    val intensity: Double,
    val speed: Double
) {
    companion object {
        private fun from(builder: Builder) = ChimneySmokeProperties(
            builder.particleOriginY,
            builder.particleSpread,
            builder.intensity,
            builder.speed
        )

        fun smokePropertiesOf(
            particleOriginY: Double,
            particleSpreadX: Double, particleSpreadY: Double, particleSpreadZ: Double,
            init: Builder.() -> Unit
        ): ChimneySmokeProperties {
            val builder = Builder(
                particleOriginY,
                Vec3(particleSpreadX, particleSpreadY, particleSpreadZ)
            )
            builder.init()
            return from(builder)
        }

        data class Builder internal constructor(
            internal var particleOriginY: Double,
            internal var particleSpread: Vec3
        ) {
            internal var intensity = 1.0
            internal var speed = 1.0
        }
    }
}