package me.otomir23.sootychimneys.setup

import me.otomir23.sootychimneys.SootyChimneys.resource
import me.otomir23.sootychimneys.block.ChimneyBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState


object ModBlockEntities {
    val CHIMNEY_BLOCK_ENTITY: BlockEntityType<ChimneyBlockEntity> = Registry.register(
        Registry.BLOCK_ENTITY_TYPE,
        resource("chimney_block_entity"),
        FabricBlockEntityTypeBuilder.create({ worldPosition: BlockPos, blockState: BlockState ->
            ChimneyBlockEntity(
                worldPosition,
                blockState
            )
        }, *ModBlocks.CHIMNEYS.toTypedArray()).build()
    )

    fun init() {}
}
