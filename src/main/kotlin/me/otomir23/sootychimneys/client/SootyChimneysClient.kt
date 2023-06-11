package me.otomir23.sootychimneys.client

import me.otomir23.sootychimneys.core.WindGetter
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

@Suppress("unused")
object SootyChimneysClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientTickEvents.START_WORLD_TICK.register(WindGetter.getWorldTickHandler())
    }
}
