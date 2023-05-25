package me.otomir23.sootychimneys.setup

import me.otomir23.sootychimneys.SootyChimneys.resource
import net.minecraft.core.Registry
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block


@Suppress("unused")
object ModTags {
    object Items {
        val CHIMNEYS: TagKey<Item> =
            TagKey.create(Registry.ITEM.key(), resource("chimneys"))
    }

    object Blocks {
        val CHIMNEYS: TagKey<Block> =
            TagKey.create(Registry.BLOCK.key(), resource("chimneys"))
    }
}