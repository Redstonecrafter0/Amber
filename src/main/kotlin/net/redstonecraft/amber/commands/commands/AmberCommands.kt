package net.redstonecraft.amber.commands.commands

import net.redstonecraft.amber.Amber
import net.redstonecraft.amber.commands.CommandManager
import net.redstonecraft.amber.commands.CommandsContext
import net.redstonecraft.amber.modules.BaseModule
import net.redstonecraft.amber.modules.Category
import net.redstonecraft.amber.modules.ToggleModule
import net.redstonecraft.amber.utils.Utils
import kotlin.math.floor
import kotlin.math.roundToInt

fun CommandsContext.setupAmberCommands() {
    parsedCommand<ToggleCommandArgs>("toggle", "Toggles the module.", ".toggle module <state>", false, "t") {
        onCommand {
            val state = when (it.state) {
                "on" -> true
                "off" -> false
                else -> null
            }
            (Category.categories.map { i -> i.modules }.flatten().firstOrNull { i -> i.id == it.module } as? ToggleModule)?.toggle(state)
        }
        onTabComplete {
            if (it.module == null) {
                Category.categories.map { i -> i.modules }.flatten().map { i -> i.id }
            } else if (it.state == null) {
                listOf("on", "off")
            } else {
                emptyList()
            }
        }
    }
    command("amber", "Get information of Amber.", ".amber", false) {
        onCommand {
            addChatMessage("§6" + ("=" * width))
            addChatMessage("")
            addChatMessage("§6§lAmber§r§f ${Amber.VERSION}")
            addChatMessage("§fby §b§l${Amber.AUTHORS.joinToString(", ")}")
            addChatMessage("")
            addChatMessage("§6" + ("=" * width))
        }
    }
    parsedCommand<HelpCommandArgs>("help", "Get information about a command.", ".help <command>", false, "?", "h") {
        onCommand {
            val cmd = if (it.command != null) {
                CommandManager.commands.filter { (k, _) -> it.command in k }.values.firstOrNull()
            } else null
            if (cmd != null) {
                addChatMessageP("§6" + ("=" * width))
                addChatMessageP("Command: ${cmd.name}")
                addChatMessageP("Description: ${cmd.description}")
                addChatMessageP("Usage: ${cmd.usage}")
                addChatMessageP("Aliases:")
                for (i in cmd.aliases) {
                    addChatMessageP(" - $i")
                }
                addChatMessageP("§6" + ("=" * width))
            } else {
                val perPage = 10
                val page = (it.command?.toIntOrNull() ?: 1) - 1
                val cmds = Utils.paginate(CommandManager.commands.toList(), page, perPage)
                if (cmds.isNotEmpty()) {
                    addChatMessageP("§6" + ("=" * width))
                    addChatMessageP("§6Commands Page §b${page + 1}§f/§b${floor(CommandManager.commands.size / perPage.toFloat()).roundToInt() + 1}")
                    addChatMessageP("-" * width)
                    for ((keys, command) in cmds) {
                        addChatMessageP("- §a${keys[0]}§f: ${command.description}")
                    }
                    addChatMessageP("§6" + ("=" * width))
                } else {
                    addChatMessageP("Invalid command or page.")
                }
            }
        }
        onTabComplete {
            if (it.command == null) {
                CommandManager.commands.keys.flatten()
            } else emptyList()
        }
    }
    parsedCommand<ModuleCommandArgs>("module", "Configures a module.", ".module module setting action <value>", false, "m") {
        onCommand {
            val setting = Category.getModule(it.module)?.settings?.firstOrNull { i -> i.id == it.setting }
            if (setting != null) {
                when (it.action) {
                    "run" -> {
                        if (setting.renderer == "button") {
                            val value = setting.value as? () -> Unit
                            if (value != null) value()
                        }
                    }
                    "set" -> setting.stringValue = it.value
                    "get" -> addChatMessageP(setting.stringValue)
                    "add" -> if (setting.renderer == "list") (setting.value as MutableList<String>) += it.value!!
                    "remove" -> if (setting.renderer == "list") (setting.value as MutableList<String>) -= it.value!!
                    "toggle" -> {
                        if (setting.value!!::class == Boolean::class) {
                            setting as BaseModule.Setting<Boolean>
                            setting.value = !setting.value
                        }
                    }
                }
            }
        }
        onTabComplete {
            when (null) {
                it.module -> Category.categories.map { i -> i.modules }.flatten().map { i -> i.id }
                it.setting -> Category.getModule(it.module)?.settings?.map { i -> i.id } ?: emptyList()
                it.action -> when (Category.getModule(it.module)?.settings?.firstOrNull { i -> i.id == it.setting }?.renderer ?: return@onTabComplete emptyList()) {
                    "button" -> listOf("run")
                    "switch" -> listOf("set", "get", "toggle")
                    "range", "colorPicker" -> listOf("set", "get")
                    "dropDownMenu" -> listOf("set", "get")
                    "list" -> listOf("add", "remove")
                    else -> emptyList()
                }
                else -> Category.getModule(it.module)?.settings?.firstOrNull { i -> i.id == it.setting }?.possibleValues ?: emptyList()
            }
        }
    }
}

private const val width = 40

private operator fun String.times(n: Int) = repeat(n)

data class ToggleCommandArgs(val module: String?, val state: String?)
data class HelpCommandArgs(val command: String?)
data class ModuleCommandArgs(val module: String?, val setting: String?, val action: String?, val value: String?)
