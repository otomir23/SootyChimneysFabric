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
class CopperChimneyBlock : ChimneyBlock(
    smokePropertiesOf(0.5f, 1.25f, 0.5f, 0.025f, 0.05f, 0.025f) {
        intensity = 0.5f
        speed = 1.2f
    },
    Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
        .sound(SoundType.COPPER)
        .strength(2f, 2f)
        .destroyTime(2f)
        .requiresCorrectToolForDrops()
), SootyChimney {
    override val cleanVariant
        get() = ModBlocks.COPPER_CHIMNEY
    override val dirtyVariant
        get() = ModBlocks.DIRTY_COPPER_CHIMNEY

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape = Shapes.or(
        box(5.0,0.0,5.0, 11.0,4.0,11.0),
        box(6.0,4.0,6.0, 10.0,16.0,10.0),
        box(5.0,10.0,5.0, 11.0,14.0,11.0)
    )
}
