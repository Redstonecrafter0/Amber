package net.redstonecraft.amber.base.config.type

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.redstonecraft.amber.base.module.AmberModule
import kotlin.reflect.KMutableProperty0

class AmberConfigMutableDropDownMenu(
    value: String,
    displayName: String,
    description: String?,
    val optionsRef: KMutableProperty0<MutableList<String>>,
    module: AmberModule
): AmberConfigSetting<String>(value, displayName, description, module) {

    @Serializable
    data class Data(
        val value: String,
        val options: List<String>
    )

    override fun serialize() = Json.encodeToString(Data(value, optionsRef.get()))

    override fun deserialize(data: String) {
        val dataValues = Json.decodeFromString<Data>(data)
        value = dataValues.value
        optionsRef.set(dataValues.options.toMutableList())
    }

}

fun AmberModule.dropdownMenu(default: String, displayName: String, options: KMutableProperty0<MutableList<String>>, description: String? = null): AmberConfigMutableDropDownMenu {
    return registerConfigSetting(AmberConfigMutableDropDownMenu(default, displayName, description, options, this))
}
