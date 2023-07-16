package me.otomir23.sootychimneys.setup

import me.otomir23.sootychimneys.SootyChimneys.resource
import me.otomir23.sootychimneys.block.*
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries

object ModBlocks {
    val CHIMNEYS: ArrayList<ChimneyBlock> = arrayListOf()

    val BRICK_CHIMNEY = registerChimneyBlock("brick_chimney", BrickChimneyBlock())
    val DIRTY_BRICK_CHIMNEY = registerChimneyBlock("dirty_brick_chimney", BrickChimneyBlock())

    val STONE_BRICK_CHIMNEY = registerChimneyBlock("stone_brick_chimney", StoneBrickChimneyBlock())
    val DIRTY_STONE_BRICK_CHIMNEY = registerChimneyBlock("dirty_stone_brick_chimney", StoneBrickChimneyBlock())

    val TERRACOTTA_CHIMNEY = registerChimneyBlock("terracotta_chimney", TerracottaChimneyBlock())
    val DIRTY_TERRACOTTA_CHIMNEY = registerChimneyBlock("dirty_terracotta_chimney", TerracottaChimneyBlock())

    val COPPER_CHIMNEY = registerChimneyBlock("copper_chimney", CopperChimneyBlock())
    val DIRTY_COPPER_CHIMNEY = registerChimneyBlock("dirty_copper_chimney", CopperChimneyBlock())

    fun init() {}

    private fun <T : ChimneyBlock> registerChimneyBlock(
        registryName: String,
        block: T
    ): T {
        val registeredBlock = Registry.register(BuiltInRegistries.BLOCK, resource(registryName), block)
        CHIMNEYS.add(registeredBlock)
        return registeredBlock
    }
}