package net.redstonecraft.amber.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.redstonecraft.amber.Amber
import net.redstonecraft.amber.events.*
import net.redstonecraft.amber.modules.BoundModule
import net.redstonecraft.amber.modules.Category
import net.redstonecraft.amber.modules.ToggleModule
import java.io.File
import java.io.FileNotFoundException

object ConfigManager {

    val idRegex = "[0-9a-zA-Z-+._]+".toRegex()
    const val fileEnding = ".amber.json"

    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        ignoreUnknownKeys = true
    }

    val dir = Amber.dir.resolve("config").apply { if (!exists() || !isDirectory) mkdirs() }

    val availableIds: List<String>
        get() = dir.list { _, it -> it.endsWith(fileEnding) }!!.filter { validateFile(dir.resolve(it)) }.map { it.removeSuffix(fileEnding) }

    var currentConfigId = dir.resolve(".amber.current_config").let { if (it.exists() && it.isFile && validateId(it.readText())) it.readText() else "default" }
        set(value) {
            field = value
            try {
                dir.resolve(".amber.current_config").writeText(value)
            } catch (_: Throwable) {
            }
        }

    var currentConfig = Config("Default", "The default configuration", Amber.VERSION, mutableListOf("Redstonecrafter0"), mutableMapOf())

    fun export(): Config {
        val config = Config(
            currentConfig.name, currentConfig.description, currentConfig.version, currentConfig.authors, Category.categories.associate { category ->
                category.id to CategoryConfig(
                    category.id, category.modules.associate { module ->
                        module.id to ModuleConfig(
                            module.id, (module as? ToggleModule)?.enabled, (module as? BoundModule)?.key, module.settings.associate { setting -> setting.id to SettingConfig(
                                setting.id, setting.serialized
                            ) }.toMutableMap()
                        )
                    }.toMutableMap()
                )
            }.toMutableMap()
        )
        return config
    }

    fun import(orgConfig: Config) {
        val event = EventManager.fire(ConfigPreLoadEvent(orgConfig))
        if (!event.isCancelled) {
            val config = event.config
            Category.categories.forEach { category ->
                category.modules.forEach { module ->
                    if (module is ToggleModule && !module.preventEnableOnLoad) module.toggle(config.categories[category.id]!!.modules[module.id]!!.enabled!!)
                    if (module is BoundModule) module.key = config.categories[category.id]!!.modules[module.id]!!.key!!
                    module.settings.forEach { setting ->
                        if (category.id in config.categories &&
                            module.id in config.categories[module.id]!!.modules &&
                            setting.id in config.categories[category.id]!!.modules[module.id]!!.settings) {
                            setting.serialized = config.categories[category.id]!!.modules[module.id]!!.settings[setting.id]!!.value
                        }
                    }
                }
            }
            EventManager.fire(ConfigPostLoadEvent(config))
        }
    }

    private fun saveToFile(file: File) {
        val config = export()
        val event = EventManager.fire(ConfigPreSaveEvent(config))
        if (!event.isCancelled) {
            json.encodeToStream(event.config, file.outputStream())
            EventManager.fire(ConfigPostSaveEvent(event.config))
        }
    }

    private fun loadFromFile(file: File) {
        import(json.decodeFromStream(file.inputStream()))
    }

    fun loadById(id: String) {
        try {
            loadFromFile(dir.resolve(id + fileEnding))
        } catch (e: FileNotFoundException) {
            if (id != "default") throw e
        }
        currentConfigId = id
    }

    fun saveWithId(id: String) {
        require(validateId(id)) { "Invalid id. Does not match ${idRegex.pattern}." }
        saveToFile(File(dir, id + fileEnding))
    }

    fun validateId(id: String): Boolean = id.matches(idRegex)

    fun validateFile(file: File): Boolean {
        if (file.name.endsWith(fileEnding) && validateId(file.name.removeSuffix(fileEnding)) && file.exists() && file.isFile) {
            try {
                json.decodeFromStream<Config>(file.inputStream())
                return true
            } catch (_: Throwable) {
            }
        }
        return false
    }

}
