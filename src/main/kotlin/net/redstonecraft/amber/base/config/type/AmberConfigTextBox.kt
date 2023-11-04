package net.redstonecraft.amber.base.config.type

import net.redstonecraft.amber.base.module.AmberModule

class AmberConfigTextBox(
    value: String,
    displayName: String,
    description: String?,
    val maxLines: Int
): AmberConfigSetting<String>(value, displayName, description) {

    override fun serialize() = value

    override fun deserialize(data: String) {
        value = data
    }

}

fun AmberModule.textbox(default: String, displayName: String, maxLines: Int = 1, description: String? = null): AmberConfigTextBox {
    return registerConfigSetting(AmberConfigTextBox(default, displayName, description, maxLines))
}
