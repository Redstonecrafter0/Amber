package net.redstonecraft.amber.base.config

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.redstonecraft.amber.AmberMod
import net.redstonecraft.amber.base.module.AmberModule

class AmberConfigManager {

    val configDir = AmberMod.dir.resolve("config").also { it.mkdirs() }
    private val currentConfigFile = configDir.resolve("current.txt")

    var currentConfig = currentConfigFile.also {
        if (!it.exists() || !it.isFile) {
            it.createNewFile()
            it.writeText("default")
        }
    }.readText()
        set(value) {
            val file = configDir.resolve("$value.amber.conf")
            if (file.exists() && file.isFile) {
                loadConfig(value)
                currentConfigFile.writeText(value)
                field = value
            } else {
                file.createNewFile()
                field = value
                saveConfig()
            }
        }

    val availableConfigs: List<String>
        get() = configDir.listFiles { _, name -> name.endsWith(".amber.conf") }!!.map { it.name.removeSuffix(".amber.conf") }

    private fun loadConfig(name: String) {
        AmberMod.moduleManager.unloadAllModules()
        AmberMod.moduleManager.loadedModules += Json.decodeFromString<List<AmberModule>>(configDir.resolve("$name.amber.conf").readText())
        AmberMod.moduleManager.loadUnloadedModules()
    }

    fun saveConfig() {
        configDir.resolve("$currentConfig.amber.conf").writeText(Json.encodeToString(AmberMod.moduleManager.loadedModules))
    }

    fun resetConfig() {
        AmberMod.moduleManager.unloadAllModules()
        AmberMod.moduleManager.loadUnloadedModules()
    }

}
