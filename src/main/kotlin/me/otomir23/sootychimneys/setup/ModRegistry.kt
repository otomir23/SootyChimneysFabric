package me.otomir23.sootychimneys.setup


object ModRegistry {
    fun init() {
        ModBlocks.init()
        ModItems.init()
        ModBlockEntities.init()
    }
}