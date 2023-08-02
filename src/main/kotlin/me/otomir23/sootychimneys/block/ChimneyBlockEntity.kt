package me.otomir23.sootychimneys.block

import me.otomir23.sootychimneys.config.CommonConfig
import me.otomir23.sootychimneys.setup.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState

class ChimneyBlockEntity (
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(
    ModBlockEntities.CHIMNEY_BLOCK_ENTITY,
    worldPosition,
    blockState
) {
    companion object {
        fun <T : BlockEntity> constructParticleTicker(): BlockEntityTicker<T> {
            return BlockEntityTicker<T> { level, blockPos, blockState, _ ->
                val chimney = blockState.block
                if (level.getRandom().nextDouble() < CommonConfig.smokeStrength && chimney is ChimneyBlock
                    && chimney.shouldEmitSmoke(blockState)
                ) chimney.emitParticles(level, blockPos.center, blockState)
            }
        }
    }
}