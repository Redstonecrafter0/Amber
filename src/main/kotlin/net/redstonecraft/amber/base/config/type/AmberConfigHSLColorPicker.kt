package net.redstonecraft.amber.base.config.type

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.redstonecraft.amber.base.module.AmberModule
import net.redstonecraft.amber.base.type.HSLColor

class AmberConfigHSLColorPicker(
    value: HSLColor,
    displayName: String,
    description: String?,
    val alpha: Boolean
): AmberConfigSetting<HSLColor>(value, displayName, description) {

    override fun serialize() = Json.encodeToString(value)

    override fun deserialize(data: String) {
        value = Json.decodeFromString(data)
    }

}

fun AmberModule.color(default: HSLColor, displayName: String, alpha: Boolean = false, description: String? = null): AmberConfigHSLColorPicker {
    return registerConfigSetting(AmberConfigHSLColorPicker(default, displayName, description, alpha))
}
