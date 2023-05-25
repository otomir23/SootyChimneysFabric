package me.otomir23.sootychimneys.block

import me.otomir23.sootychimneys.core.ChimneySmokeProperties.Companion.smokePropertiesOf
import me.otomir23.sootychimneys.core.SootyChimney
import me.otomir23.sootychimneys.setup.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.MaterialColor
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape


@Suppress("OVERRIDE_DEPRECATION")
class TerracottaChimneyBlock : ChimneyBlock(
    smokePropertiesOf(0.5f, 0.75f, 0.5f, 0.02f, 0.05f, 0.02f) {
        intensity = 0.2f
        speed = 0.65f
    },
    Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE)
        .sound(SoundType.DRIPSTONE_BLOCK)
        .strength(2f, 2f)
        .destroyTime(0.6f)
        .requiresCorrectToolForDrops()
), SootyChimney {
    override val cleanVariant
        get() = ModBlocks.TERRACOTTA_CHIMNEY
    override val dirtyVariant
        get() = ModBlocks.DIRTY_TERRACOTTA_CHIMNEY

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape = Shapes.or(
        box(5.0,0.0,5.0, 11.0,8.0,11.0)
    )
}
