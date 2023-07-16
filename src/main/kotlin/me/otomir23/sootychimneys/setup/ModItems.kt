package me.otomir23.sootychimneys.setup

import me.otomir23.sootychimneys.SootyChimneys.resource
import me.otomir23.sootychimneys.block.ChimneyBlock
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.ItemStack


@Suppress("unused")
object ModItems {
    @Suppress("MemberVisibilityCanBePrivate")
    val CHIMNEYS: ArrayList<BlockItem> = arrayListOf()

    val BRICK_CHIMNEY = registerChimneyBlockItem("brick_chimney", ModBlocks.BRICK_CHIMNEY)
    val DIRTY_BRICK_CHIMNEY = registerChimneyBlockItem("dirty_brick_chimney", ModBlocks.DIRTY_BRICK_CHIMNEY)

    val STONE_BRICK_CHIMNEY = registerChimneyBlockItem("stone_brick_chimney", ModBlocks.STONE_BRICK_CHIMNEY)
    val DIRTY_STONE_BRICK_CHIMNEY = registerChimneyBlockItem("dirty_stone_brick_chimney", ModBlocks.DIRTY_STONE_BRICK_CHIMNEY)

    val TERRACOTTA_CHIMNEY = registerChimneyBlockItem("terracotta_chimney", ModBlocks.TERRACOTTA_CHIMNEY)
    val DIRTY_TERRACOTTA_CHIMNEY = registerChimneyBlockItem("dirty_terracotta_chimney", ModBlocks.DIRTY_TERRACOTTA_CHIMNEY)

    val COPPER_CHIMNEY = registerChimneyBlockItem("copper_chimney", ModBlocks.COPPER_CHIMNEY)
    val DIRTY_COPPER_CHIMNEY = registerChimneyBlockItem("dirty_copper_chimney", ModBlocks.DIRTY_COPPER_CHIMNEY)

    fun init() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS)
            .register(ModifyEntries { content: FabricItemGroupEntries ->
                content.acceptAll(CHIMNEYS.map { c -> ItemStack(c) })
            })
    }

    private fun <T : ChimneyBlock> registerChimneyBlockItem(
        registryName: String,
        block: T
    ): BlockItem {
        val registeredItem = Registry.register(
            BuiltInRegistries.ITEM,
            resource(registryName),
            BlockItem(
                block,
                FabricItemSettings()
            )
        )
        CHIMNEYS.add(registeredItem)
        return registeredItem
    }
}