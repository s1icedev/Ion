package net.horizonsend.ion.server.features.player

import io.papermc.paper.adventure.PaperAdventure
import net.horizonsend.ion.common.utils.text.miniMessage
import net.horizonsend.ion.server.IonServerComponent
import net.horizonsend.ion.server.features.achievements.Achievement
import net.horizonsend.ion.server.miscellaneous.utils.nms
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementNode
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

		val advancementName = Advancement.Builder.advancement()
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
			.addCriterion("testingCriteria", Criterion(CriteriaTriggers.IMPOSSIBLE, ImpossibleTrigger.TriggerInstance()))
			.save(consumer, "test/root")

		val multicriteriatest = Advancement.Builder.advancement()
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
			.addCriterion("whar1", Criterion(CriteriaTriggers.IMPOSSIBLE, ImpossibleTrigger.TriggerInstance()))
			.addCriterion("whar2", Criterion(CriteriaTriggers.IMPOSSIBLE, ImpossibleTrigger.TriggerInstance()))
			.addCriterion("whar3", Criterion(CriteriaTriggers.IMPOSSIBLE, ImpossibleTrigger.TriggerInstance()))
			.parent(advancementName)
			.save(consumer, "test/multiple")

		val exploration_root = Advancement.Builder.advancement()
			.display(
				ItemStack(Material.CLOCK).nms,
				PaperAdventure.asVanilla("<aqua>Exploration".miniMessage()),
				PaperAdventure.asVanilla("<blue>The Start of Your Intergalactic Journey".miniMessage()),
				ResourceLocation.withDefaultNamespace("textures/block/sculk_catalyst_top.png"),
				AdvancementType.TASK,
				false,
				false,
				false
			)
			.addCriterion(Achievement.EXPLORATION_ROOT.criteria, Criterion(CriteriaTriggers.IMPOSSIBLE, ImpossibleTrigger.TriggerInstance()))
			.save(consumer, Achievement.EXPLORATION_ROOT.key)

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
