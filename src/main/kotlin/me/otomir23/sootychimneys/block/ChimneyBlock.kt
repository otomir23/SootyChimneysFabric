package me.otomir23.sootychimneys.block

import me.otomir23.sootychimneys.config.CommonConfig
import me.otomir23.sootychimneys.core.ChimneySmokeProperties
import me.otomir23.sootychimneys.core.SootyChimney
import me.otomir23.sootychimneys.core.WindGetter
import me.otomir23.sootychimneys.loot.ModLootTables
import me.otomir23.sootychimneys.setup.ModBlockEntities
import me.otomir23.sootychimneys.setup.ModTags
import me.otomir23.sootychimneys.util.RandomOffset
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.ItemTags
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CampfireBlock
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3


@Suppress("OVERRIDE_DEPRECATION")
open class ChimneyBlock(
    private val smokeProperties: ChimneySmokeProperties,
    properties: Properties
) : Block(properties), EntityBlock {
    companion object {
        val LIT: BooleanProperty = CampfireBlock.LIT
        val BLOCKED: BooleanProperty = BooleanProperty.create("blocked")
        val SIGNAL = BooleanProperty.create("signal")
    }

    init {
        this.registerDefaultState(
            defaultBlockState()
                .setValue(LIT, true)
                .setValue(BLOCKED, false)
                .setValue(SIGNAL, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder
            .add(LIT)
            .add(BLOCKED)
            .add(SIGNAL)
    }

    /**
     * Expects a ChimneyBlock block state. May fail if not Chimney.
     */
    fun shouldEmitSmoke(blockState: BlockState): Boolean {
        return blockState.getValue(LIT) && !blockState.getValue(BLOCKED)
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult? {
        // getToolModifiedState moved here
        val tool = player.getItemInHand(hand)
        val block = state.block
        if (!tool.isEmpty && tool.`is`(ItemTags.AXES) && block is SootyChimney && block.isDirty) {
            block.makeSootParticles(level, pos)
            if (!level.isClientSide) {
                level as ServerLevel
                // Offset item spawning pos, depending on clicked face, to spawn items closer to the player.
                // Items shooting in opposite direction is not fun.
                val faceNormal = blockHitResult.direction.normal
                val itemSpawnPosition = Vec3(
                    pos.x + 0.5 + faceNormal.x * 0.65,
                    pos.y + 0.6 + faceNormal.y * 0.65,
                    pos.z + 0.5 + faceNormal.z * 0.65
                )
                val items: List<ItemStack> = ModLootTables.getSootScrapingLootFor(state, level)
                spawnSootScrapingItems(itemSpawnPosition, level, items)
            }
            level.setBlockAndUpdate(pos, block.cleanVariant.withPropertiesOf(state))
            return InteractionResult.SUCCESS
        }
        // End

        if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty) return InteractionResult.PASS
        val newBlockedValue: Boolean = !state.getValue(BLOCKED)

        if (level.isClientSide) {
            val messageTranslationKey = "message.sootychimneys." + if (newBlockedValue) "blocked" else "open"
            player.displayClientMessage(Component.translatable(messageTranslationKey), true)
        } else {
            val random = level.getRandom()
            for (i in 0 until random.nextInt(5)) {
                (level as ServerLevel).sendParticles(
                    ParticleTypes.SMOKE,
                    pos.center.x,
                    pos.y + smokeProperties.particleOriginY - 0.1,
                    pos.center.z,
                    1,
                    random.nextGaussian() * 0.1,
                    random.nextGaussian() * 0.1,
                    random.nextGaussian() * 0.1,
                    0.0
                )
            }
            level.playSound(
                null,
                pos,
                if (newBlockedValue) SoundEvents.LANTERN_FALL else SoundEvents.LANTERN_HIT,
                SoundSource.BLOCKS,
                0.8f,
                0.85f + random.nextFloat() * 0.05f
            )
            level.setBlock(pos, state.setValue(BLOCKED, newBlockedValue), UPDATE_ALL)
        }
        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    private fun hasExternalBlockers(level: Level, pos: BlockPos): Boolean {
        val hasRedstoneSignal = level.hasNeighborSignal(pos)
        val hasChimneyAbove = level.getBlockState(pos.above()).`is`(ModTags.Blocks.CHIMNEYS)
        return hasChimneyAbove || hasRedstoneSignal
    }
    
    private fun shouldBeSignal(level: Level, pos: BlockPos) = level.getBlockState(pos.below()).`is`(ModTags.Blocks.CHIMNEYS)

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        oldState: BlockState,
        isMoving: Boolean
    ) {
        if (level.isClientSide) return

        val hasExternalBlockers = hasExternalBlockers(level, blockPos)
        level.setBlock(
            blockPos,
            blockState
                .setValue(LIT, !hasExternalBlockers)
                .setValue(SIGNAL, shouldBeSignal(level, blockPos)),
            UPDATE_ALL
        )
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(LIT, !hasExternalBlockers(blockPlaceContext.level, blockPlaceContext.clickedPos))
            .setValue(SIGNAL, shouldBeSignal(blockPlaceContext.level, blockPlaceContext.clickedPos))
    }

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        if (!level.isClientSide) level.setBlock(
            blockPos,
            blockState
                .setValue(LIT, !hasExternalBlockers(level, blockPos))
                .setValue(SIGNAL, shouldBeSignal(level, blockPos)),
            UPDATE_CLIENTS
        )
    }

    fun emitParticles(level: Level, pos: Vec3, state: BlockState) {
        val random = level.getRandom()
        if (random.nextDouble() > smokeProperties.intensity) return
        val wind = WindGetter.wind
        val windStrengthModifier = CommonConfig.windStrengthMultiplier
        val xSpeed: Double = wind.xCoordinate * wind.strength * windStrengthModifier
        val zSpeed: Double = wind.yCoordinate * wind.strength * windStrengthModifier
        val ySpeed: Double = 0.05 * smokeProperties.speed
        val particleSpread = smokeProperties.particleSpread
        val maxParticles = (4 * smokeProperties.intensity.coerceAtLeast(0.5)).toInt()
        for (i in 0 until random.nextInt(maxParticles)) {
            val particleType = if (state.getValue(SIGNAL)) ParticleTypes.CAMPFIRE_SIGNAL_SMOKE else ParticleTypes.CAMPFIRE_COSY_SMOKE
            level.addAlwaysVisibleParticle(
                particleType, true,
                RandomOffset.offset(pos.x, particleSpread.x(), random),
                RandomOffset.offset(pos.y - 0.5 + smokeProperties.particleOriginY, particleSpread.y(), random),
                RandomOffset.offset(pos.z, particleSpread.z(), random),
                xSpeed, ySpeed, zSpeed
            )
        }
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return ChimneyBlockEntity(blockPos, blockState)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (level.isClientSide() && type === ModBlockEntities.CHIMNEY_BLOCK_ENTITY)
            ChimneyBlockEntity.constructParticleTicker()
        else
            null
    }

    override fun isRandomlyTicking(blockState: BlockState): Boolean {
        val chimney = blockState.block
        return chimney is SootyChimney && chimney.isClean
    }

    override fun randomTick(blockState: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        val chimney = blockState.block
        if (chimney is SootyChimney
            && chimney.isClean
            && shouldEmitSmoke(blockState) && random.nextDouble() < CommonConfig.dirtyChance
        ) {
            level.setBlock(pos, chimney.dirtyVariant.defaultBlockState(), UPDATE_CLIENTS)
        }
    }

    private fun spawnSootScrapingItems(pos: Vec3, level: ServerLevel, items: List<ItemStack>) {
        for (itemStack in items) {
            val entity = ItemEntity(level, pos.x(), pos.y(), pos.z(), itemStack)
            entity.spawnAtLocation(itemStack)
        }
    }

    override fun playerDestroy(
        level: Level,
        player: Player,
        blockPos: BlockPos,
        blockState: BlockState,
        blockEntity: BlockEntity?,
        itemStack: ItemStack
    ) {
        super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack)
        val chimney = blockState.block
        if (chimney is SootyChimney && chimney.isDirty) chimney.makeSootParticles(
            level,
            blockPos
        )
    }
}