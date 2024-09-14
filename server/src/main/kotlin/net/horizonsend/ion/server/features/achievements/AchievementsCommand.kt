package net.horizonsend.ion.server.features.achievements

import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import net.horizonsend.ion.common.extensions.success
import net.horizonsend.ion.common.extensions.userError
import net.horizonsend.ion.server.command.SLCommand
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("achievements")
@Suppress("Unused")
object AchievementsCommand : SLCommand() {
	override fun onEnable(manager: PaperCommandManager) {
		registerStaticCompletion(
			manager,
			"achievements",
			Achievement.entries.joinToString("|") { it.name }
		)
	}

	@Subcommand("grant")
	@CommandCompletion("@achievements @players")
	@CommandPermission("ion.achievements.grant")
	fun onAchievementGrant(sender: CommandSender, achievement: Achievement, target: String) {
		val player = Bukkit.getPlayer(target) ?: return sender.userError("Player $target must be online.")

		achievement.rewardAdvancement(player)

		sender.success("Gave achievement ${achievement.name} to $target.")
	}

	@Subcommand("revoke")
	@CommandCompletion("@achievements @players")
	@CommandPermission("ion.achievements.revoke")
	fun onAchievementRevoke(sender: CommandSender, achievement: Achievement, target: String) {
		val player = Bukkit.getPlayer(target) ?: return sender.userError("Player $target must be online.")

		achievement.revokeAdvancement(player)

		sender.success("Took achievement ${achievement.name} from $target.")
	}

}
