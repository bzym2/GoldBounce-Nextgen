/*
 * This file is part of GoldBounce (https://github.com/bzym2/GoldBounce-Nextgen)
 *
 * Copyright (c) 2024 - 2025 bzym2
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */
package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.modes

import net.ccbluex.liquidbounce.config.types.Choice
import net.ccbluex.liquidbounce.config.types.ChoiceConfigurable
import net.ccbluex.liquidbounce.event.events.AttackEntityEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.modules.combat.ModuleAutoClicker
import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.ModuleCriticals
import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.ModuleCriticals.VisualsConfigurable.showCriticals
import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.ModuleCriticals.canDoCriticalHit
import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.ModuleCriticals.modes
import net.ccbluex.liquidbounce.features.module.modules.player.invcleaner.items.WeaponItemFacet
import net.ccbluex.liquidbounce.utils.client.SilentHotbar
import net.ccbluex.liquidbounce.utils.client.chat
import net.ccbluex.liquidbounce.utils.item.attackDamage
import net.ccbluex.liquidbounce.utils.item.getAttributeValue
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.item.SwordItem
import net.minecraft.util.Hand

/**
 * Criticals by switching to second-best weapon mode
 */
object CriticalsSwitch : Choice("SwitchWeapon") {

    override val parent: ChoiceConfigurable<Choice>
        get() = modes

    private fun getWeaponDamage(stack: ItemStack): Float? {
        if (stack.item !is SwordItem) return 0f
        return stack.getAttributeValue(EntityAttributes.ATTACK_DAMAGE)?.toFloat()
    }

    private fun findSecondBestWeapon(currentStack: ItemStack): Int? {
        val currentDamage = getWeaponDamage(currentStack)
        val hotbarSlots = (0..8).toList()

        val weapons = hotbarSlots.mapNotNull { slot ->
            val stack = player.inventory.getStack(slot)
            if (stack.item is SwordItem && stack != currentStack) {
                slot to getWeaponDamage(stack)
            } else {
                null
            }
        }.sortedByDescending { it.second }
        return when {
            weapons.size >= 2 -> weapons[1].first
            weapons.size == 1 -> weapons[0].first
            else -> null
        }
    }

    @Suppress("unused")
    private val attackHandler = handler<AttackEntityEvent> { event ->
        if (event.isCancelled || event.entity !is LivingEntity) {
            return@handler
        }

        val ignoreSprinting = ModuleCriticals.WhenSprinting.shouldAttemptCritWhileSprinting()

        if (!canDoCriticalHit(true, ignoreSprinting)) {
            return@handler
        }

        val currentStack = player.getStackInHand(Hand.MAIN_HAND)
        if (currentStack.item !is SwordItem) return@handler
        if(ModuleCriticals.VisualsConfigurable.debug){
            chat("Stack:${currentStack.item.name}")
        }
        val secondBestSlot = findSecondBestWeapon(currentStack) ?: return@handler
        if(ModuleCriticals.VisualsConfigurable.debug){
            chat("Switched to second-best weapon:${secondBestSlot}")
        }
        // Store current slot
        SilentHotbar.selectSlotSilently(this, secondBestSlot, 1)
        // Switch to second-best weapon

        // Attack with the switched weapon
        player.attack(event.entity)
        if(ModuleCriticals.VisualsConfigurable.debug){
            chat("Attacked with second-best weapon:${secondBestSlot}")
        }
        // Switch back to original weapon
        player.inventory.selectedSlot = currentSlot
        if (ModuleCriticals.VisualsConfigurable.debug){
            chat("Switched back to original weapon:${currentSlot}")
        }
        // Show critical particles
        showCriticals(event.entity)
    }
}
