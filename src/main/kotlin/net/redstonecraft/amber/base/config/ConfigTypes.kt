package net.redstonecraft.amber.base.config

import kotlinx.serialization.Serializable

abstract class AmberConfigSetting<T>(
    var value: T
)

typealias AmberConfigData = MutableMap<String, AmberConfigSetting<*>>
