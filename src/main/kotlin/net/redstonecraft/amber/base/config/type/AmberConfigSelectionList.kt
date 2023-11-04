package net.redstonecraft.amber.base.config.type

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.redstonecraft.amber.base.module.AmberModule

class AmberConfigSelectionList(
    value: MutableList<String>,
    displayName: String,
    description: String?,
    private val suggestionProvider: (List<String>) -> List<String>
): AmberConfigSetting<MutableList<String>>(value, displayName, description) {

    override fun serialize() = Json.encodeToString(value)

    override fun deserialize(data: String) {
        value = Json.decodeFromString(data)
    }

    fun getSuggestions(): List<String> {
        return suggestionProvider(value.toList())
    }

}

fun AmberModule.selectionList(default: MutableList<String>, displayName: String, description: String? = null, suggestionProvider: (List<String>) -> List<String>): AmberConfigSelectionList {
    return registerConfigSetting(AmberConfigSelectionList(default, displayName, description, suggestionProvider))
}
