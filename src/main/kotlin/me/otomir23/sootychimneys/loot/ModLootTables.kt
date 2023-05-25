package me.otomir23.sootychimneys.loot

import com.mojang.logging.LogUtils
import me.otomir23.sootychimneys.SootyChimneys.resource
import me.otomir23.sootychimneys.core.SootyChimney
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet
import net.minecraft.world.level.storage.loot.parameters.LootContextParams


object ModLootTables {
    private val SOOT_SCRAPING = resource("soot_scraping")
    private val SOOT_SCRAPING_PARAM_SET = LootContextParamSet.builder().required(LootContextParams.BLOCK_STATE).build()

    fun getSootScrapingLootFor(state: BlockState, level: ServerLevel): List<ItemStack> {
        val chimney = state.block
        if (chimney !is SootyChimney) {
            LogUtils.getLogger().error("Soot Scraping is only for SootyChimney blocks.")
            return emptyList()
        }
        val blockId = Registry.BLOCK.getResourceKey(chimney.dirtyVariant).get().location().path
        return try {
            val lootContext = LootContext.Builder(level)
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .create(SOOT_SCRAPING_PARAM_SET)
            val table = level.server.lootTables[ResourceLocation("$SOOT_SCRAPING/$blockId")]
            table.getRandomItems(lootContext)
        } catch (ex: Exception) {
            LogUtils.getLogger()
                .error("Failed to get 'soot_scraping' loot table for '{}'. - {}", blockId, ex.stackTraceToString())
            emptyList()
        }
    }
}