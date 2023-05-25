package me.otomir23.sootychimneys.core

import me.otomir23.sootychimneys.util.RandomOffset.offset
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block


interface SootyChimney {
    val cleanVariant: Block
    val dirtyVariant: Block
    val isDirty: Boolean
        get() = cleanVariant !== this
    val isClean: Boolean
        get() = !isDirty
    val scrapingDropChance: Float
        get() = 0.5f

    fun makeSootParticles(level: Level, pos: BlockPos) {
        val random = level.getRandom()
        val x = pos.x + 0.5
        val y = pos.y + 0.5
        val z = pos.z + 0.5
        for (i in 0 until random.nextInt(12, 20)) {
            level.addParticle(
                ParticleTypes.LARGE_SMOKE,
                offset(x.toFloat(), 1.2.toFloat(), random).toDouble(),
                offset(y.toFloat(), 1.2.toFloat(), random).toDouble(),
                offset(z.toFloat(), 1.2.toFloat(), random).toDouble(),
                0.0, 0.0, 0.0
            )
        }
    }
}