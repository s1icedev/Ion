package net.horizonsend.ion.server.items.objects

import java.util.function.Supplier
import net.horizonsend.ion.server.BalancingConfiguration
import net.kyori.adventure.text.Component
import org.bukkit.Material

abstract class Magazine<T: BalancingConfiguration.EnergyWeapon.AmmoStorageBalancing>(
	identifier: String,

	material: Material,
	customModelData: Int,
	displayName: Component,

	private val balancingSupplier: Supplier<T>
) : AmmunitionHoldingItem(identifier, material, customModelData, displayName) {
	val balancing get() = balancingSupplier.get()
	override fun getMaximumAmmunition(): Int = balancing.capacity
}