package net.horizonsend.ion.server.features.player

import io.papermc.paper.adventure.PaperAdventure
import net.horizonsend.ion.common.utils.text.miniMessage
import net.horizonsend.ion.server.IonServerComponent
import net.horizonsend.ion.server.features.achievements.Achievement
import net.horizonsend.ion.server.features.custom.items.CustomItemRegistry
import net.horizonsend.ion.server.features.gui.GuiItem
import net.horizonsend.ion.server.miscellaneous.utils.nms
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementNode
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementTree
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.TreeNodePosition
import net.minecraft.advancements.critereon.ImpossibleTrigger
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.ServerAdvancementManager
import net.minecraft.world.level.block.Blocks
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

object NMSAchievements : IonServerComponent() {
	override fun onEnable() {
		boostrapAchievements()
	}

	private fun boostrapAchievements() {
		val advancements = mutableMapOf<ResourceLocation, AdvancementHolder>()

		val consumer = Consumer<AdvancementHolder> { advancementHolder ->
			advancements[advancementHolder.id] = advancementHolder
		}

		val impossible = Criterion(CriteriaTriggers.IMPOSSIBLE, ImpossibleTrigger.TriggerInstance())

		val singleCriterionExample = Advancement.Builder.advancement()
			.display(
				Blocks.SCULK,
				PaperAdventure.asVanilla("<light_purple>HE TEST".miniMessage()),
				PaperAdventure.asVanilla("<aqua>HE TEST DESCRIPTION".miniMessage()),
				ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
				AdvancementType.CHALLENGE,
				false,
				false,
				false
			)
			.addCriterion("testingCriteria", impossible)
			.save(consumer, "test/root")

		val multiCriterionExample = Advancement.Builder.advancement()
			.display(
				Blocks.LADDER,
				PaperAdventure.asVanilla("<aqua>MULTIPLE".miniMessage()),
				PaperAdventure.asVanilla("<green>CRITERIA??".miniMessage()),
				null,
				AdvancementType.CHALLENGE,
				false,
				false,
				false
			)
			.addCriterion("criteria1", impossible)
			.addCriterion("criteria2", impossible)
			.addCriterion("criteria3", impossible)
			.parent(singleCriterionExample)
			.save(consumer, "test/multiple")

		val anyCriteriaExample = Advancement.Builder.advancement()
			.display(
				Blocks.LADDER,
				PaperAdventure.asVanilla("<aqua>ANY".miniMessage()),
				PaperAdventure.asVanilla("<green>CRITERIA??".miniMessage()),
				null,
				AdvancementType.CHALLENGE,
				false,
				false,
				false
			)
			.addCriterion("any1", impossible)
			.addCriterion("any2", impossible)
			.addCriterion("any3", impossible)
			.requirements(AdvancementRequirements.anyOf(mutableListOf("any1", "any2", "any3")))
			.parent(singleCriterionExample)
			.save(consumer, "test/any")

		val exploration_root = Advancement.Builder.advancement()
			.display(
				ItemStack(Material.CHAINMAIL_HELMET).nms,
				PaperAdventure.asVanilla("<aqua>Exploration".miniMessage()),
				PaperAdventure.asVanilla("<blue>The Start of Your Intergalactic Journey".miniMessage()),
				ResourceLocation.withDefaultNamespace("textures/block/sculk_catalyst_top.png"),
				AdvancementType.TASK,
				false,
				false,
				false
			)
			.addCriterion(Achievement.EXPLORATION_ROOT.criteria, impossible)
			.save(consumer, "exploration/root")

		val detect_ship = Advancement.Builder.advancement()
			.display(
				Blocks.JUKEBOX,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.DETECT_SHIP.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.DETECT_SHIP.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.DETECT_SHIP.criteria, impossible)
			.parent(exploration_root)
			.save(consumer, Achievement.DETECT_SHIP.key)

		val pilot_ship = Advancement.Builder.advancement()
			.display(
				ItemStack(Material.CLOCK).nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_SHIP.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_SHIP.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_SHIP.criteria, impossible)
			.parent(detect_ship)
			.save(consumer, Achievement.PILOT_SHIP.key)

		val enter_space = Advancement.Builder.advancement()
			.display(
				GuiItem.ASTERI_2.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.ENTER_SPACE.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.ENTER_SPACE.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.ENTER_SPACE.criteria, impossible)
			.parent(pilot_ship)
			.save(consumer, Achievement.ENTER_SPACE.key)

		val enter_hyperspace = Advancement.Builder.advancement()
			.display(
				CustomItemRegistry.CHETHERITE.constructItemStack().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.ENTER_HYPERSPACE.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.ENTER_HYPERSPACE.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.ENTER_HYPERSPACE.criteria, impossible)
			.parent(enter_space)
			.save(consumer, Achievement.ENTER_HYPERSPACE.key)

		val enter_null_space = Advancement.Builder.advancement()
			.display(
				GuiItem.HORIZON_2.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.ENTER_NULL_SPACE.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.ENTER_NULL_SPACE.description.miniMessage()),
				null,
				AdvancementType.GOAL,
				true,
				false,
				false
			)
			.addCriterion(Achievement.ENTER_NULL_SPACE.criteria, impossible)
			.parent(enter_hyperspace)
			.save(consumer, Achievement.ENTER_NULL_SPACE.key)

		val sink_ship = Advancement.Builder.advancement()
			.display(
				GuiItem.KILL_SHIP.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.SINK_SHIP.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.SINK_SHIP.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.SINK_SHIP.criteria, impossible)
			.parent(enter_space)
			.save(consumer, Achievement.SINK_SHIP.key)

		val get_sunk = Advancement.Builder.advancement()
			.display(
				GuiItem.KILL_SHIP.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.GET_SUNK.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.GET_SUNK.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.GET_SUNK.criteria, impossible)
			.parent(sink_ship)
			.save(consumer, Achievement.GET_SUNK.key)

		val buy_crate = Advancement.Builder.advancement()
			.display(
				Blocks.BROWN_SHULKER_BOX,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.BUY_CRATE.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.BUY_CRATE.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.BUY_CRATE.criteria, impossible)
			.parent(enter_space)
			.save(consumer, Achievement.BUY_CRATE.key)

		val sell_crate = Advancement.Builder.advancement()
			.display(
				Blocks.LIGHT_GRAY_SHULKER_BOX,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.SELL_CRATE.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.SELL_CRATE.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.SELL_CRATE.criteria, impossible)
			.parent(buy_crate)
			.save(consumer, Achievement.SELL_CRATE.key)

// ship classes

		val pilot_ships = Advancement.Builder.advancement()
			.display(
				GuiItem.GENERIC_STARSHIP.makeItem().nms,
				PaperAdventure.asVanilla("<aqua>All Piloted Ships".miniMessage()),
				PaperAdventure.asVanilla("Gotta Pilot 'em All!".miniMessage()),
				null,
				AdvancementType.GOAL,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_SHUTTLE.criteria, impossible)
			.addCriterion(Achievement.PILOT_TRANSPORT.criteria, impossible)
			.addCriterion(Achievement.PILOT_LIGHT_FREIGHTER.criteria, impossible)
			.addCriterion(Achievement.PILOT_MEDIUM_FREIGHTER.criteria, impossible)
			.addCriterion(Achievement.PILOT_HEAVY_FREIGHTER.criteria, impossible)
			.addCriterion(Achievement.PILOT_BARGE.criteria, impossible)
			.addCriterion(Achievement.PILOT_STARFIGHTER.criteria, impossible)
			.addCriterion(Achievement.PILOT_GUNSHIP.criteria, impossible)
			.addCriterion(Achievement.PILOT_CORVETTE.criteria, impossible)
			.addCriterion(Achievement.PILOT_FRIGATE.criteria, impossible)
			.addCriterion(Achievement.PILOT_DESTROYER.criteria, impossible)
			.addCriterion(Achievement.PILOT_CRUISER.criteria, impossible)
			.addCriterion(Achievement.PILOT_BATTLECRUISER.criteria, impossible)
			.addCriterion(Achievement.PILOT_SPEEDER.criteria, impossible)
			.parent(pilot_ship)
			.save(consumer, "exploration/pilot_ships")

		val pilot_speeder = Advancement.Builder.advancement()
			.display(
				GuiItem.GENERIC_STARSHIP.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_SPEEDER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_SPEEDER.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_SPEEDER.criteria, impossible)
			.parent(pilot_ships)
			.save(consumer, Achievement.PILOT_SPEEDER.key)


		val pilot_shuttle = Advancement.Builder.advancement()
			.display(
				GuiItem.SHUTTLE.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_SHUTTLE.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_SHUTTLE.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_SHUTTLE.criteria, impossible)
			.parent(pilot_ships)
			.save(consumer, Achievement.PILOT_SHUTTLE.key)

		val pilot_transport = Advancement.Builder.advancement()
			.display(
				GuiItem.TRANSPORT.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_TRANSPORT.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_TRANSPORT.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_TRANSPORT.criteria, impossible)
			.parent(pilot_shuttle)
			.save(consumer, Achievement.PILOT_TRANSPORT.key)

		val pilot_light_freighter = Advancement.Builder.advancement()
			.display(
				GuiItem.LIGHT_FREIGHTER.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_LIGHT_FREIGHTER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_LIGHT_FREIGHTER.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_LIGHT_FREIGHTER.criteria, impossible)
			.parent(pilot_transport)
			.save(consumer, Achievement.PILOT_LIGHT_FREIGHTER.key)

		val pilot_medium_freighter = Advancement.Builder.advancement()
			.display(
				GuiItem.MEDIUM_FREIGHTER.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_MEDIUM_FREIGHTER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_MEDIUM_FREIGHTER.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_MEDIUM_FREIGHTER.criteria, impossible)
			.parent(pilot_light_freighter)
			.save(consumer, Achievement.PILOT_MEDIUM_FREIGHTER.key)

		val pilot_heavy_freighter = Advancement.Builder.advancement()
			.display(
				GuiItem.HEAVY_FREIGHTER.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_HEAVY_FREIGHTER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_HEAVY_FREIGHTER.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_HEAVY_FREIGHTER.criteria, impossible)
			.parent(pilot_medium_freighter)
			.save(consumer, Achievement.PILOT_HEAVY_FREIGHTER.key)

		val pilot_barge = Advancement.Builder.advancement()
			.display(
				GuiItem.BARGE.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_BARGE.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_BARGE.description.miniMessage()),
				null,
				AdvancementType.CHALLENGE,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_BARGE.criteria, impossible)
			.parent(pilot_heavy_freighter)
			.save(consumer, Achievement.PILOT_BARGE.key)

		val pilot_starfighter = Advancement.Builder.advancement()
			.display(
				GuiItem.STARFIGHTER.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_STARFIGHTER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_STARFIGHTER.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_STARFIGHTER.criteria, impossible)
			.parent(pilot_ships)
			.save(consumer, Achievement.PILOT_STARFIGHTER.key)

		val pilot_gunship = Advancement.Builder.advancement()
			.display(
				GuiItem.GUNSHIP.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_GUNSHIP.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_GUNSHIP.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_GUNSHIP.criteria, impossible)
			.parent(pilot_starfighter)
			.save(consumer, Achievement.PILOT_GUNSHIP.key)

		val pilot_corvette = Advancement.Builder.advancement()
			.display(
				GuiItem.CORVETTE.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_CORVETTE.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_CORVETTE.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_CORVETTE.criteria, impossible)
			.parent(pilot_gunship)
			.save(consumer, Achievement.PILOT_CORVETTE.key)


		val pilot_frigate = Advancement.Builder.advancement()
			.display(
				GuiItem.FRIGATE.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_FRIGATE.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_FRIGATE.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_FRIGATE.criteria, impossible)
			.parent(pilot_corvette)
			.save(consumer, Achievement.PILOT_FRIGATE.key)

		val pilot_destroyer = Advancement.Builder.advancement()
			.display(
				GuiItem.DESTROYER.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_DESTROYER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_DESTROYER.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_DESTROYER.criteria, impossible)
			.parent(pilot_frigate)
			.save(consumer, Achievement.PILOT_DESTROYER.key)

		val pilot_cruiser = Advancement.Builder.advancement()
			.display(
				GuiItem.CRUISER.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_CRUISER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_CRUISER.description.miniMessage()),
				null,
				AdvancementType.GOAL,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_CRUISER.criteria, impossible)
			.parent(pilot_destroyer)
			.save(consumer, Achievement.PILOT_CRUISER.key)

		val pilot_battlecruiser = Advancement.Builder.advancement()
			.display(
				GuiItem.BATTLECRUISER.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.PILOT_BATTLECRUISER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.PILOT_BATTLECRUISER.description.miniMessage()),
				null,
				AdvancementType.CHALLENGE,
				true,
				false,
				false
			)
			.addCriterion(Achievement.PILOT_BATTLECRUISER.criteria, impossible)
			.parent(pilot_cruiser)
			.save(consumer, Achievement.PILOT_BATTLECRUISER.key)

		val add_pilot = Advancement.Builder.advancement()
			.display(
				Blocks.PLAYER_HEAD,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.ADD_PILOT.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.ADD_PILOT.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.ADD_PILOT.criteria, impossible)
			.parent(detect_ship)
			.save(consumer, Achievement.ADD_PILOT.key)

		val add_pilot_battlecruiser = Advancement.Builder.advancement()
			.display(
				Blocks.PLAYER_HEAD,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.ADD_PILOT_BATTLECRUISER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.ADD_PILOT_BATTLECRUISER.description.miniMessage()),
				null,
				AdvancementType.GOAL,
				true,
				false,
				true
			)
			.addCriterion(Achievement.ADD_PILOT_BATTLECRUISER.criteria, impossible)
			.parent(add_pilot)
			.save(consumer, Achievement.ADD_PILOT_BATTLECRUISER.key)

		val buy_bazaar = Advancement.Builder.advancement()
			.display(
				ItemStack(Material.ACACIA_SAPLING).nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.BUY_BAZAAR.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.BUY_BAZAAR.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.BUY_BAZAAR.criteria, impossible)
			.parent(pilot_ship)
			.save(consumer, Achievement.BUY_BAZAAR.key)

		val remote_buy_bazaar = Advancement.Builder.advancement()
			.display(
				CustomItemRegistry.PERSONAL_TRANSPORTER.constructItemStack().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.REMOTE_BUY_BAZAAR.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.REMOTE_BUY_BAZAAR.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.REMOTE_BUY_BAZAAR.criteria, impossible)
			.parent(buy_bazaar)
			.save(consumer, Achievement.REMOTE_BUY_BAZAAR.key)

		val sell_bazaar = Advancement.Builder.advancement()
			.display(
				ItemStack(Material.GOLD_NUGGET).nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.SELL_BAZAAR.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.SELL_BAZAAR.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.SELL_BAZAAR.criteria, impossible)
			.parent(remote_buy_bazaar)
			.save(consumer, Achievement.SELL_BAZAAR.key)

		val kill_player = Advancement.Builder.advancement()
			.display(
				GuiItem.PLAYER_KILL.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.KILL_PLAYER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.KILL_PLAYER.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				true
			)
			.addCriterion(Achievement.KILL_PLAYER.criteria, impossible)
			.parent(exploration_root)
			.save(consumer, Achievement.KILL_PLAYER.key)

		val kill_captain = Advancement.Builder.advancement()
			.display(
				ItemStack(Material.SPYGLASS).nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.KILL_CAPTAIN.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.KILL_CAPTAIN.description.miniMessage()),
				null,
				AdvancementType.GOAL,
				true,
				false,
				false
			)
			.addCriterion(Achievement.KILL_CAPTAIN.criteria, impossible)
			.parent(kill_player)
			.save(consumer, Achievement.KILL_CAPTAIN.key)

		val kill_settlement_leader = Advancement.Builder.advancement()
			.display(
				ItemStack(Material.DIAMOND_SWORD).nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.KILL_SETTLEMENT_LEADER.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.KILL_SETTLEMENT_LEADER.description.miniMessage()),
				null,
				AdvancementType.GOAL,
				true,
				false,
				false
			)
			.addCriterion(Achievement.KILL_SETTLEMENT_LEADER.criteria, impossible)
			.parent(kill_player)
			.save(consumer, Achievement.KILL_SETTLEMENT_LEADER.key)

		val obtain_player_head = Advancement.Builder.advancement()
			.display(
				Blocks.PLAYER_HEAD,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.OBTAIN_PLAYER_HEAD.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.OBTAIN_PLAYER_HEAD.description.miniMessage()),
				null,
				AdvancementType.GOAL,
				true,
				false,
				false
			)
			.addCriterion(Achievement.OBTAIN_PLAYER_HEAD.criteria, impossible)
			.parent(kill_player)
			.save(consumer, Achievement.OBTAIN_PLAYER_HEAD.key)

		val sink_ai_ship = Advancement.Builder.advancement()
			.display(
				Blocks.PISTON,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.SINK_AI_SHIP.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.SINK_AI_SHIP.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.SINK_AI_SHIP.criteria, impossible)
			.parent(enter_space)
			.save(consumer, Achievement.SINK_AI_SHIP.key)

		val sink_alien_ship = Advancement.Builder.advancement()
			.display(
				CustomItemRegistry.SUPERCONDUCTOR.constructItemStack().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.SINK_ALIEN_SHIP.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.SINK_ALIEN_SHIP.description.miniMessage()),
				null,
				AdvancementType.TASK,
				true,
				false,
				false
			)
			.addCriterion(Achievement.SINK_ALIEN_SHIP.criteria, impossible)
			.parent(sink_ai_ship)
			.save(consumer, Achievement.SINK_ALIEN_SHIP.key)

//challenges

		val million_bazaar_profit = Advancement.Builder.advancement()
			.display(
				ItemStack(Material.DIAMOND).nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.MILLION_BAZAAR_PROFIT.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.MILLION_BAZAAR_PROFIT.description.miniMessage()),
				null,
				AdvancementType.CHALLENGE,
				true,
				false,
				true
			)
			.addCriterion(Achievement.MILLION_BAZAAR_PROFIT.criteria, impossible)
			.parent(sell_bazaar)
			.save(consumer, Achievement.MILLION_BAZAAR_PROFIT.key)

		val sink_1k_ai_ships = Advancement.Builder.advancement()
			.display(
				CustomItemRegistry.SUPERCONDUCTOR_BLOCK.constructItemStack().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.SINK_1K_AI_SHIPS.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.SINK_1K_AI_SHIPS.description.miniMessage()),
				null,
				AdvancementType.CHALLENGE,
				true,
				false,
				true
			)
			.addCriterion(Achievement.SINK_1K_AI_SHIPS.criteria, impossible)
			.parent(sink_alien_ship)
			.save(consumer, Achievement.SINK_1K_AI_SHIPS.key)












//multi-criteria

		val obtain_all_cores = Advancement.Builder.advancement()
			.display(
				CustomItemRegistry.BATTLECRUISER_REACTOR_CORE.constructItemStack().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.OBTAIN_ALL_CORES.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.OBTAIN_ALL_CORES.description.miniMessage()),
				null,
				AdvancementType.CHALLENGE,
				true,
				false,
				false
			)
			.addCriterion(Achievement.OBTAIN_BATTLECRUISER_REACTOR_CORE.criteria, impossible)
			.addCriterion(Achievement.OBTAIN_CRUISER_REACTOR_CORE.criteria, impossible)
			.addCriterion(Achievement.OBTAIN_BARGE_REACTOR_CORE.criteria, impossible)
			.parent(pilot_ship)
			.save(consumer, Achievement.OBTAIN_ALL_CORES.key)

		val sink_each_ai_ship = Advancement.Builder.advancement()
			.display(
				Blocks.DISPENSER,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.SINK_EACH_AI_SHIP.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.SINK_EACH_AI_SHIP.description.miniMessage()),
				null,
				AdvancementType.CHALLENGE,
				true,
				false,
				false
			)
			.addCriterion(Achievement.SINK_吃饭人.criteria, impossible)
			.addCriterion(Achievement.SINK_WATCHERS.criteria, impossible)
			.addCriterion(Achievement.SINK_MINING_GUILD.criteria, impossible)
			.addCriterion(Achievement.SINK_PERSEUS_EXPLORERS.criteria, impossible)
			.addCriterion(Achievement.SINK_SYSTEM_DEFENSE_FORCES.criteria, impossible)
			.addCriterion(Achievement.SINK_TSAII_RAIDERS.criteria, impossible)
			.addCriterion(Achievement.SINK_PIRATES.criteria, impossible)
			.parent(sink_alien_ship)
			.save(consumer, Achievement.SINK_EACH_AI_SHIP.key)

		val visit_all_systems = Advancement.Builder.advancement()
			.display(
				GuiItem.ILIOS_2.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.VISIT_ALL_SYSTEMS.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.VISIT_ALL_SYSTEMS.description.miniMessage()),
				null,
				AdvancementType.GOAL,
				true,
				false,
				false
			)
			.addCriterion(Achievement.VISIT_ASTERI.criteria, impossible)
			.addCriterion(Achievement.VISIT_REGULUS.criteria, impossible)
			.addCriterion(Achievement.VISIT_SIRIUS.criteria, impossible)
			.addCriterion(Achievement.VISIT_ILIOS.criteria, impossible)
			.addCriterion(Achievement.VISIT_HORIZON.criteria, impossible)
			.addCriterion(Achievement.VISIT_TRENCH.criteria, impossible)
			.addCriterion(Achievement.VISIT_AU_0821.criteria, impossible)
			.parent(enter_hyperspace)
			.save(consumer, Achievement.VISIT_ALL_SYSTEMS.key)

		val visit_all_planets = Advancement.Builder.advancement()
			.display(
				GuiItem.EDEN_2.makeItem().nms,
				PaperAdventure.asVanilla(("<aqua>" + Achievement.VISIT_ALL_PLANETS.title).miniMessage()),
				PaperAdventure.asVanilla(Achievement.VISIT_ALL_PLANETS.description.miniMessage()),
				null,
				AdvancementType.CHALLENGE,
				true,
				false,
				false
			)
			.addCriterion(Achievement.VISIT_AERACH.criteria, impossible)
			.addCriterion(Achievement.VISIT_ARET.criteria, impossible)
			.addCriterion(Achievement.VISIT_CHANDRA.criteria, impossible)
			.addCriterion(Achievement.VISIT_CHIMGARA.criteria, impossible)
			.addCriterion(Achievement.VISIT_DAMKOTH.criteria, impossible)
			.addCriterion(Achievement.VISIT_EDEN.criteria, impossible)
			.addCriterion(Achievement.VISIT_GAHARA.criteria, impossible)
			.addCriterion(Achievement.VISIT_HERDOLI.criteria, impossible)
			.addCriterion(Achievement.VISIT_ILIUS.criteria, impossible)
			.addCriterion(Achievement.VISIT_ISIK.criteria, impossible)
			.addCriterion(Achievement.VISIT_KOVFEFE.criteria, impossible)
			.addCriterion(Achievement.VISIT_KRIO.criteria, impossible)
			.addCriterion(Achievement.VISIT_LIODA.criteria, impossible)
			.addCriterion(Achievement.VISIT_LUXITERNA.criteria, impossible)
			.addCriterion(Achievement.VISIT_QATRA.criteria, impossible)
			.addCriterion(Achievement.VISIT_RUBACIEA.criteria, impossible)
			.addCriterion(Achievement.VISIT_TURMS.criteria, impossible)
			.addCriterion(Achievement.VISIT_VASK.criteria, impossible)
			.parent(visit_all_systems)
			.save(consumer, Achievement.VISIT_ALL_PLANETS.key)

		//final line in function
		apply(advancements)
	}

	fun apply(advancements: Map<ResourceLocation, AdvancementHolder>) {
		val manager = MinecraftServer.getServer().advancements
		val plusOld = manager.advancements.plus(advancements)

		val advancementTree = AdvancementTree()
		advancementTree.addAll(plusOld.values)

		val iterator: Iterator<*> = advancementTree.roots().iterator()

		while (iterator.hasNext()) {
			val advancementNode = iterator.next() as AdvancementNode

			if (advancementNode.holder().value().display().isPresent) {
				TreeNodePosition.run(advancementNode)
			}
		}

		val tree = ServerAdvancementManager::class.java.getDeclaredField("tree")
		tree.isAccessible = true
		tree.set(manager, advancementTree)

		manager.advancements = plusOld
		Registries.ADVANCEMENT.registry()
	}
}
