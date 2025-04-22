package net.ccbluex.liquidbounce.features.module.modules.combat.velocity.mode

import net.ccbluex.liquidbounce.config.types.ToggleableConfigurable
import net.ccbluex.liquidbounce.event.events.GameTickEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.events.WorldChangeEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.modules.combat.velocity.mode.VelocityMode
import net.ccbluex.liquidbounce.utils.client.chat
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket

/**
 * VelocityGrim mode
 *
 * Custom implementation of velocity reduction.
 */
internal object VelocityGrimFull : VelocityMode("GrimFull") {
    private val onlyGround by boolean("OnlyGround", false)
    private val debugMessage by boolean("DebugMessage", false)

    private val reduceFactor by float("Factor", 0.6f, 0.0f..1.0f)
    private val minHurtTime by int("MinHurtTime", 5, 0..10)
    private val maxHurtTime by int("MaxHurtTime", 10, 0..20)

    private var lastAttackTime = 0L

    init {
        // Tick-based handler to replace LivingUpdateEvent
        handler<GameTickEvent> {
            val player = mc.player ?: return@handler
            if (player.hurtTime in minHurtTime..maxHurtTime) {
                lastAttackTime = System.currentTimeMillis()
            }
        }
    }

    /**
     * Handles incoming velocity packets and modifies them based on settings.
     */
    private val packetHandler = handler<PacketEvent> { event ->
        val packet = event.packet

        if (packet is EntityVelocityUpdateS2CPacket && packet.entityId == mc.player?.id) {
            val player = mc.player ?: return@handler
            if (onlyGround && !player.isOnGround) return@handler

            if (player.hurtTime in minHurtTime..maxHurtTime) {
                if (debugMessage) {
                    chat("Reducing velocity: X=${packet.velocityX}, Y=${packet.velocityY}, Z=${packet.velocityZ}")
                }
                packet.velocityX = (packet.velocityX * reduceFactor).toInt()
                packet.velocityY = (packet.velocityY * reduceFactor).toInt()
                packet.velocityZ = (packet.velocityZ * reduceFactor).toInt()
            }
        } else if (packet is ExplosionS2CPacket) {
            event.cancelEvent() // Cancel explosion knockback
        }
    }

    /**
     * Resets state when the world changes.
     */
    private val worldHandler = handler<WorldChangeEvent> {
        lastAttackTime = 0L // Reset state on world change
    }
}
