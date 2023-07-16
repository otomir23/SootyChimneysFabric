package me.otomir23.sootychimneys.integration.create

/*
TODO Create Support

import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import me.otomir23.sootychimneys.block.ChimneyBlock
import me.otomir23.sootychimneys.config.CommonConfig
import net.minecraft.core.BlockPos


class ChimneyMovementBehaviour : MovementBehaviour {
    override fun tick(context: MovementContext) {
        super.tick(context)
        val chimney = context.state.block
        if (
            context.world.isClientSide && chimney is ChimneyBlock &&
            chimney.shouldEmitSmoke(context.state, context.world, context.localPos) &&
            context.world.getRandom().nextDouble() < CommonConfig.smokeStrength
        ) {
            chimney.emitParticles(
                context.world,
                BlockPos(context.position.x, context.position.y, context.position.z)
            )
        }
    }
}*/
