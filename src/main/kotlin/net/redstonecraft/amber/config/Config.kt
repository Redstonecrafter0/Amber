package net.redstonecraft.amber.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Config(
    var name: String,
    var description: String,
    var version: String,
    val authors: MutableList<String> = mutableListOf(),
    val categories: MutableMap<String, CategoryConfig> = mutableMapOf()
)

@Serializable
data class CategoryConfig(
    val id: String,
    val modules: MutableMap<String, ModuleConfig> = mutableMapOf()
)

@Serializable
data class ModuleConfig(
    val id: String,
    var enabled: Boolean?,
    var key: Int?,
    val settings: MutableMap<String, SettingConfig> = mutableMapOf()
)

@Serializable
data class SettingConfig(
    val id: String,
    var value: JsonElement
)
