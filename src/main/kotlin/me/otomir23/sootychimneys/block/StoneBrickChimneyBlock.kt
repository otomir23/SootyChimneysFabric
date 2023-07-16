package me.otomir23.sootychimneys.block

import me.otomir23.sootychimneys.core.ChimneySmokeProperties.Companion.smokePropertiesOf
import me.otomir23.sootychimneys.core.SootyChimney
import me.otomir23.sootychimneys.setup.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape


@Suppress("OVERRIDE_DEPRECATION")
class StoneBrickChimneyBlock : ChimneyBlock(
    smokePropertiesOf(0.5f, 1.2f, 0.5f, 0.025f, 0.05f, 0.025f) {
        intensity = 0.5f
    },
    Properties.of()
        .mapColor(DyeColor.GRAY)
        .sound(SoundType.BASALT)
        .strength(2f, 2f)
        .destroyTime(2f)
        .requiresCorrectToolForDrops()
), SootyChimney {
    override val cleanVariant
        get() = ModBlocks.STONE_BRICK_CHIMNEY
    override val dirtyVariant
        get() = ModBlocks.DIRTY_STONE_BRICK_CHIMNEY

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape = Shapes.or(
        box(4.0,0.0,4.0, 12.0,11.0,12.0),
        box(3.0,11.0,3.0, 13.0,16.0,13.0)
    )
}
