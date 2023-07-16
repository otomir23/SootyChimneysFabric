package me.otomir23.sootychimneys.core

import org.joml.Vector3f

data class ChimneySmokeProperties(
    val particleOrigin: Vector3f,
    val particleSpread: Vector3f,
    val intensity: Float,
    val speed: Float
) {
    companion object {
        private fun from(builder: Builder) = ChimneySmokeProperties(
            builder.particleOrigin,
            builder.particleSpread,
            builder.intensity,
            builder.speed
        )

        fun smokePropertiesOf(
            particleOriginX: Float, particleOriginY: Float, particleOriginZ: Float,
            particleSpreadX: Float, particleSpreadY: Float, particleSpreadZ: Float,
            init: Builder.() -> Unit
        ): ChimneySmokeProperties {
            val builder = Builder(
                Vector3f(particleOriginX, particleOriginY, particleOriginZ),
                Vector3f(particleSpreadX, particleSpreadY, particleSpreadZ)
            )
            builder.init()
            return from(builder)
        }

        data class Builder internal constructor(
            internal var particleOrigin: Vector3f,
            internal var particleSpread: Vector3f
        ) {
            internal var intensity = 1f
            internal var speed = 1f
        }
    }
}