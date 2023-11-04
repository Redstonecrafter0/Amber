package net.redstonecraft.amber.base.config.type

import net.redstonecraft.amber.base.module.AmberModule

class AmberConfigSwitch(
    value: Boolean,
    displayName: String,
    description: String?
): AmberConfigSetting<Boolean>(value, displayName, description) {

    override fun serialize() = value.toString()

    override fun deserialize(data: String) {
        value = data == "true"
    }

}

fun AmberModule.switch(default: Boolean, displayName: String, description: String? = null): AmberConfigSwitch {
    return registerConfigSetting(AmberConfigSwitch(default, displayName, description))
}
