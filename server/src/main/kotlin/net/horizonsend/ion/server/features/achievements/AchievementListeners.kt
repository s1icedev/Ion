package net.horizonsend.ion.server.features.achievements

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import net.horizonsend.ion.server.IonServerComponent
import net.horizonsend.ion.server.features.custom.items.CustomItemRegistry.customItem
import net.horizonsend.ion.server.miscellaneous.utils.Tasks
import net.horizonsend.ion.server.miscellaneous.utils.vaultEconomy
import net.horizonsend.ion.server.features.world.IonWorld.Companion.ion
import net.horizonsend.ion.server.features.world.WorldFlag
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerChangedWorldEvent

object AchievementListeners : IonServerComponent() {

	override fun onEnable() {
		checkBalances()
	}

	@EventHandler
	@Suppress("Unused")
	fun onPlayerAdvancementDoneEvent(event: PlayerAdvancementDoneEvent) {
		/* listener for the final reward of all multi-criterion advancements */

		val key = event.advancement.key.key //String
		when( key ) {
			Achievement.OBTAIN_ALL_CORES.key -> {
				Achievement.OBTAIN_ALL_CORES.rewardAdvancement(event.player)
			}
			Achievement.OBTAIN_ALL_POWER_ARMOR.key -> {
				Achievement.OBTAIN_ALL_POWER_ARMOR.rewardAdvancement(event.player)
			}
			Achievement.VISIT_ALL_PLANETS.key -> {
				Achievement.VISIT_ALL_PLANETS.rewardAdvancement(event.player)
			}
			Achievement.VISIT_ALL_SYSTEMS.key -> {
				Achievement.VISIT_ALL_SYSTEMS.rewardAdvancement(event.player)
			}
			Achievement.SINK_EACH_AI_SHIP.key -> {
				Achievement.SINK_EACH_AI_SHIP.rewardAdvancement(event.player)
			}

		}
	}

	@EventHandler
	@Suppress("Unused")
	fun onPlayerDeathEvent(event: PlayerDeathEvent) {
		val killer = event.entity.killer ?: return // Only player kills
		if (killer !== event.player) {
			Achievement.KILL_PLAYER.rewardAdvancement(killer)
		}
	}

	@EventHandler
	@Suppress("Unused")
	fun onPlayerChangedWorldEvent(event: PlayerChangedWorldEvent) {
		val player = event.player
		if(player.world.ion.hasFlag(WorldFlag.SPACE_WORLD)) {
			Achievement.ENTER_SPACE.rewardAdvancement(player)
		}
		if(player.world.name.contains("Trench", true) || player.world.name.contains("AU-0821",true)) {
			Achievement.ENTER_NULL_SPACE.rewardAdvancement(player)
		}

		val achievement = runCatching { Achievement.valueOf("VISIT_${(player.world.name).uppercase()}") }.getOrNull()
		achievement?.rewardAdvancement(player)
	}

	@EventHandler
	@Suppress("Unused")
	fun onPlayerAttemptPickupItemEvent(event: PlayerInventorySlotChangeEvent) {
		val player = event.player
		val item = event.newItemStack

		if (item.type == Material.AIR) return // dropped item

		val newCustomItem = item.customItem	// normal custom items

		val customItemName: String? = newCustomItem?.identifier

		if(customItemName != null) {
			rewardObtainedItemAdvancement(player, customItemName)
		} else {
			rewardObtainedItemAdvancement(player, item.type.name)
		}
	}

	private fun rewardObtainedItemAdvancement(player: Player, name: String) {
		val achievement = runCatching { Achievement.valueOf("OBTAIN_${name.uppercase()}") }.getOrNull()
		achievement?.rewardAdvancement(player)
	}

/*  currently using PlayerChangedWorldEvent, EnterPlanetEvent may be used later */

//	@EventHandler(priority = EventPriority.LOWEST)
//	fun onEnterPlanetEvent(event: EnterPlanetEvent) {
//		val player = (event.controller as? PlayerController)?.player ?: return
//		val playerData = SLPlayer[player.uniqueId]!!
//
//		when (event.newWorld.name.lowercase()) {
//
//		}
//	}

//	@EventHandler(priority = EventPriority.MONITOR)
//	@Suppress("Unused")
//	fun onDetectShip(event: StarshipDetectEvent) {
//
//	}

	private fun checkBalances() {
		Tasks.asyncRepeat(20L, 20L) {
			for (player in Bukkit.getOnlinePlayers()) {
				val num = vaultEconomy?.getBalance(player) ?: continue
				when {
					num >= 10000000.0 -> Achievement.BAL_10M.rewardAdvancement(player)
					num >= 1000000.0 -> Achievement.BAL_1M.rewardAdvancement(player)
					num >= 500000.0 -> Achievement.BAL_500K.rewardAdvancement(player)
					num >= 100000.0 -> Achievement.BAL_100K.rewardAdvancement(player)
					num >= 10000.0 -> Achievement.BAL_10K.rewardAdvancement(player)
					num >= 1000.0 -> Achievement.BAL_1K.rewardAdvancement(player)
				}
			}
		}
	}
}
