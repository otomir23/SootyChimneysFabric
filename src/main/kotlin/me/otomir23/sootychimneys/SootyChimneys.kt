package me.otomir23.sootychimneys

import eu.midnightdust.lib.config.MidnightConfig
import me.otomir23.sootychimneys.config.CommonConfig
import me.otomir23.sootychimneys.setup.ModRegistry
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation


object SootyChimneys : ModInitializer {
    @Suppress("MemberVisibilityCanBePrivate")
    const val MOD_ID = "sootychimneys"

    override fun onInitialize() {
        MidnightConfig.init(MOD_ID, CommonConfig::class.java)

        ModRegistry.init()

        /*TODO Create Support

        if (FabricLoader.getInstance().isModLoaded("create")) {
            CreateIntegration.registerMovingBehaviors()
        }*/
    }

    fun resource(path: String) = ResourceLocation(MOD_ID, path)
}
