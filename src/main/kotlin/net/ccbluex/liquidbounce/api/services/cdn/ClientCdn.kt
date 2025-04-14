/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2025 CCBlueX
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
 *
 *
 */
package net.ccbluex.liquidbounce.api.services.cdn

import net.ccbluex.liquidbounce.api.core.BaseApi
import net.ccbluex.liquidbounce.api.core.CLIENT_CDN
import net.ccbluex.liquidbounce.api.core.utf8Lines
import net.ccbluex.liquidbounce.api.models.cdn.IpcConfiguration
import okio.BufferedSource

object ClientCdn : BaseApi(CLIENT_CDN) {
    suspend fun requestDiscordConfiguration() = get<IpcConfiguration>("/discord.json")
    suspend fun requestStaffList(server: String): Set<String> = when(server){
        "Heypixel" -> setOf("绿豆乃SAMA","nightbary","体贴的炼金术雀","StarNO1","妖猫","小妖猫","妖猫的PC号","小H修bug","xiaotufei","元宵","CuteGirlQlQl","彩笔","布吉岛打工仔","元宵的测试号","抑郁的元宵","元宵睡不醒","抖音丶小匪","练书法的苦力怕","KiKiAman","元宵睡不醒","WS故","彩笔qwq","管理员-1","管理员-2","管理员-3","管理员-4","管理员-5","管理员-6","管理员-7","管理员-8","管理员-9","管理员-10","天使","艾米丽","可比不来嗯忑","鸡你太美","神伦子")
        "QuickMacro" -> setOf("BACs","YT_BACs","无年a","BoogerTheCat","花雨庭审判骑士","血樱丶星梦","Toxic_AslGy","Cloudy_C","仙阁灬特色","小符xfu360","欲生北茶丿年糕","刀客塔","CK_87","kIkkl12","Toxic_Yuuki","艾森啊")
        else -> {get<BufferedSource>("/staffs/$server").utf8Lines().asSequence().toHashSet()}
    }

}
