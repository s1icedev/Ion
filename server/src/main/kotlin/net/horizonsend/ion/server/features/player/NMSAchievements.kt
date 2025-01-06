package net.horizonsend.ion.server.features.player

import io.papermc.paper.adventure.PaperAdventure
import net.horizonsend.ion.common.utils.text.miniMessage
import net.horizonsend.ion.server.IonServerComponent
import net.horizonsend.ion.server.features.achievements.Achievement
import net.horizonsend.ion.server.features.custom.items.CustomItemRegistry
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
				ItemStack(Material.CLOCK).nms,
				PaperAdventure.asVanilla("<aqua>Exploration".miniMessage()),
				PaperAdventure.asVanilla("<blue>The Start of Your Intergalactic Journey".miniMessage()),
				ResourceLocation.withDefaultNamespace("textures/block/sculk_catalyst_top.png"),
				AdvancementType.TASK,
				false,
				false,
				false
			)
			.addCriterion(Achievement.EXPLORATION_ROOT.criteria, impossible)
			.save(consumer, Achievement.EXPLORATION_ROOT.key)

		val visit_all_systems = Advancement.Builder.advancement()
			.display(
				CustomItemRegistry.ILIOS.constructItemStack().nms,
				PaperAdventure.asVanilla(Achievement.VISIT_ALL_SYSTEMS.title.miniMessage()),
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
			.parent(exploration_root)
			.save(consumer, Achievement.VISIT_ALL_SYSTEMS.key)

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
