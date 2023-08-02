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
class BrickChimneyBlock : ChimneyBlock(
    smokePropertiesOf(1.0, 0.25, 0.1, 0.25) {
        speed = 1.2
    },
    Properties.of()
        .mapColor(DyeColor.ORANGE)
        .sound(SoundType.DEEPSLATE_BRICKS)
        .strength(2f, 2f)
        .destroyTime(2.2f)
        .requiresCorrectToolForDrops()
), SootyChimney {
    override val cleanVariant
        get() = ModBlocks.BRICK_CHIMNEY
    override val dirtyVariant
        get() = ModBlocks.DIRTY_BRICK_CHIMNEY
    override val scrapingDropChance = 0.75

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape = Shapes.or(
        box(1.0, 0.0, 1.0, 15.0, 11.0, 15.0),
        box(0.0, 11.0, 0.0, 16.0, 16.0, 16.0)
    )
}
