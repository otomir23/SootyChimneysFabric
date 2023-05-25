package me.otomir23.sootychimneys.block

import com.mojang.math.Vector3f
import me.otomir23.sootychimneys.config.CommonConfig
import me.otomir23.sootychimneys.core.ChimneySmokeProperties
import me.otomir23.sootychimneys.core.SootyChimney
import me.otomir23.sootychimneys.core.WindGetter
import me.otomir23.sootychimneys.loot.ModLootTables
import me.otomir23.sootychimneys.setup.ModBlockEntities
import me.otomir23.sootychimneys.setup.ModTags
import me.otomir23.sootychimneys.util.RandomOffset
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RedstoneTorchBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult


@Suppress("OVERRIDE_DEPRECATION")
open class ChimneyBlock(
    private val smokeProperties: ChimneySmokeProperties,
    properties: Properties
) : Block(properties), EntityBlock {
    companion object {
        val LIT: BooleanProperty = RedstoneTorchBlock.LIT
        val BLOCKED: BooleanProperty = BooleanProperty.create("blocked")
    }

    init {
        this.registerDefaultState(
            defaultBlockState()
                .setValue(LIT, true)
                .setValue(BLOCKED, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder
            .add(LIT)
            .add(BLOCKED)
    }

    /**
     * Expects a ChimneyBlock block state. May fail if not Chimney.
     */
    fun shouldEmitSmoke(blockState: BlockState, level: Level, pos: BlockPos): Boolean {
        return blockState.getValue(LIT) && !blockState.getValue(BLOCKED) && !level.getBlockState(pos.above())
            .`is`(ModTags.Blocks.CHIMNEYS)
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
        if (!tool.isEmpty && tool.`is`(ConventionalItemTags.AXES) && block is SootyChimney && block.isDirty) {
            block.makeSootParticles(level, pos)
            if (!level.isClientSide) {
                level as ServerLevel
                // Offset item spawning pos, depending on clicked face, to spawn items closer to the player.
                // Items shooting in opposite direction is not fun.
                val faceNormal = blockHitResult.direction.normal
                val itemSpawnPosition = Vector3f(
                    pos.x + 0.5f + faceNormal.x * 0.65f,
                    pos.y + 0.6f + faceNormal.y * 0.65f,
                    pos.z + 0.5f + faceNormal.z * 0.65f
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
            val particleOrigin = smokeProperties.particleOrigin
            val random = level.getRandom()
            for (i in 0 until random.nextInt(5)) {
                (level as ServerLevel).sendParticles(
                    ParticleTypes.SMOKE,
                    (
                            pos.x + particleOrigin.x()).toDouble(),
                    pos.y + particleOrigin.y() - 0.1,
                    (pos.z + particleOrigin.z()).toDouble(),
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

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        oldState: BlockState,
        isMoving: Boolean
    ) {
        if (level.isClientSide) return
        val hasRedstoneSignal = level.hasNeighborSignal(blockPos)
        if (blockState.getValue(LIT) == hasRedstoneSignal) level.setBlock(
            blockPos,
            blockState.setValue(LIT, !hasRedstoneSignal),
            UPDATE_ALL
        )
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(LIT, !blockPlaceContext.level.hasNeighborSignal(blockPlaceContext.clickedPos))
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
            blockState.setValue(LIT, !level.hasNeighborSignal(blockPos)),
            UPDATE_CLIENTS
        )
    }

    fun emitParticles(level: Level, pos: BlockPos) {
        val random = level.getRandom()
        if (random.nextFloat() > smokeProperties.intensity) return
        val particleOffset = smokeProperties.particleOrigin
        val x = pos.x + particleOffset.x()
        val y = pos.y + particleOffset.y()
        val z = pos.z + particleOffset.z()
        val wind = WindGetter.wind
        val windStrengthModifier = CommonConfig.windStrengthMultiplier
        val xSpeed: Double = wind.xCoordinate * wind.strength * windStrengthModifier
        val zSpeed: Double = wind.yCoordinate * wind.strength * windStrengthModifier
        val ySpeed: Double = 0.05 * smokeProperties.speed
        val particleSpread = smokeProperties.particleSpread
        val maxParticles = (4 * smokeProperties.intensity.coerceAtLeast(0.5f)).toInt()
        for (i in 0 until random.nextInt(maxParticles)) {
            val particleType = if (level.getBlockState(pos.below())
                    .`is`(ModTags.Blocks.CHIMNEYS)
            ) ParticleTypes.CAMPFIRE_SIGNAL_SMOKE else ParticleTypes.CAMPFIRE_COSY_SMOKE
            level.addAlwaysVisibleParticle(
                particleType, true,
                RandomOffset.offset(x, particleSpread.x(), random).toDouble(),
                RandomOffset.offset(y, particleSpread.y(), random).toDouble(),
                RandomOffset.offset(z, particleSpread.z(), random).toDouble(),
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
            && shouldEmitSmoke(blockState, level, pos) && random.nextDouble() < CommonConfig.dirtyChance
        ) {
            level.setBlock(pos, chimney.dirtyVariant.defaultBlockState(), UPDATE_CLIENTS)
        }
    }

    private fun spawnSootScrapingItems(pos: Vector3f, level: ServerLevel, items: List<ItemStack>) {
        for (itemStack in items) {
            val entity = ItemEntity(level, pos.x().toDouble(), pos.y().toDouble(), pos.z().toDouble(), itemStack)
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