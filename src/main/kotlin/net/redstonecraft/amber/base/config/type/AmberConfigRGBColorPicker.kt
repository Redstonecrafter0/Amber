package net.redstonecraft.amber.base.config.type

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.redstonecraft.amber.base.module.AmberModule
import net.redstonecraft.amber.base.type.RGBColor

class AmberConfigRGBColorPicker(
    value: RGBColor,
    displayName: String,
    description: String?,
    val alpha: Boolean
): AmberConfigSetting<RGBColor>(value, displayName, description) {

    override fun serialize() = Json.encodeToString(value)

    override fun deserialize(data: String) {
        value = Json.decodeFromString(data)
    }

}

fun AmberModule.color(default: RGBColor, displayName: String, alpha: Boolean = false, description: String? = null): AmberConfigRGBColorPicker {
    return registerConfigSetting(AmberConfigRGBColorPicker(default, displayName, description, alpha))
}
