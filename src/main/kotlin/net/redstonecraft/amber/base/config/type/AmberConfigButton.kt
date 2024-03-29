package net.redstonecraft.amber.base.config.type

import net.redstonecraft.amber.base.module.AmberModule

class AmberConfigButton(
    value: () -> Unit,
    displayName: String,
    description: String?,
    module: AmberModule
): AmberConfigSetting<() -> Unit>(value, displayName, description, module) {

    override fun serialize() = ""
    override fun deserialize(data: String) {}

}

fun AmberModule.button(displayName: String, description: String? = null, block: () -> Unit): AmberConfigButton {
    return registerConfigSetting(AmberConfigButton(block, displayName, description, this))
}
