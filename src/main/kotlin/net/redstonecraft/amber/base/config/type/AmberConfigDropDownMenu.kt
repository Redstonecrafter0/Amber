package net.redstonecraft.amber.base.config.type

import net.redstonecraft.amber.base.module.AmberModule

class AmberConfigDropDownMenu<T: Enum<T>>(
    value: T,
    displayName: String,
    description: String?,
    module: AmberModule,
    private val valueOf: (String) -> T
): AmberConfigSetting<T>(value, displayName, description, module) {

    override fun serialize() = value.toString()

    override fun deserialize(data: String) {
        value = valueOf(data)
    }

}

inline fun <reified T: Enum<T>> AmberModule.dropdownMenu(default: T, displayName: String, description: String? = null): AmberConfigDropDownMenu<T> {
    return registerConfigSetting(AmberConfigDropDownMenu(default, displayName, description, this) { enumValueOf(it) })
}
