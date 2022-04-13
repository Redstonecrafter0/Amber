package net.redstonecraft.amber.commands.commands

import net.redstonecraft.amber.Amber
import net.redstonecraft.amber.commands.CommandManager
import net.redstonecraft.amber.commands.CommandsContext
import net.redstonecraft.amber.modules.BaseModule
import net.redstonecraft.amber.modules.BoundModule
import net.redstonecraft.amber.modules.Category
import net.redstonecraft.amber.modules.ToggleModule
import net.redstonecraft.amber.utils.GLFWKeys
import net.redstonecraft.amber.utils.Utils
import kotlin.math.floor
import kotlin.math.roundToInt

fun CommandsContext.setupAmberCommands() {
    parsedCommand<ToggleCommandArgs>("toggle", "Toggles the module.", ".toggle module <state>", false, "t") {
        onCommand { (module, stateS) ->
            val state = when (stateS) {
                "on" -> true
                "off" -> false
                else -> null
            }
            (Category.modules.firstOrNull { i -> i.id == module } as? ToggleModule)?.toggle(state)
        }
        onTabComplete { (module, state) ->
            if (module == null) {
                Category.modules.map { i -> i.id }
            } else if (state == null) {
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
        onCommand { (commandS) ->
            val cmd = if (commandS != null) {
                CommandManager.commands.filter { (k, _) -> commandS in k }.values.firstOrNull()
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
                val page = (commandS?.toIntOrNull() ?: 1) - 1
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
        onTabComplete { (command) ->
            if (command == null) {
                CommandManager.commands.keys.flatten()
            } else emptyList()
        }
    }
    parsedCommand<ModuleCommandArgs>("module", "Configures a module.", ".module module setting action <value>", false, "m") {
        onCommand { (module, settingId, action, value) ->
            val setting = Category.getModule(module)?.settings?.firstOrNull { i -> i.id == settingId }
            if (setting != null) {
                when (action) {
                    "run" -> {
                        if (setting.renderer == "button") {
                            val value = setting.value as? () -> Unit
                            if (value != null) value()
                        }
                    }
                    "set" -> setting.stringValue = value
                    "get" -> addChatMessageP(setting.stringValue)
                    "add" -> if (setting.renderer == "list") (setting.value as MutableList<String>) += value!!
                    "remove" -> if (setting.renderer == "list") (setting.value as MutableList<String>) -= value!!
                    "toggle" -> {
                        if (setting.value!!::class == Boolean::class) {
                            setting as BaseModule.Setting<Boolean>
                            setting.value = !setting.value
                            setting.save()
                        }
                    }
                }
            }
        }
        onTabComplete { (module, setting, action, _) ->
            when (null) {
                module -> Category.modules.map { i -> i.id }
                setting -> Category.getModule(module)?.settings?.map { i -> i.id } ?: emptyList()
                action -> when (Category.getModule(module)?.settings?.firstOrNull { i -> i.id == setting }?.renderer ?: return@onTabComplete emptyList()) {
                    "button" -> listOf("run")
                    "switch" -> listOf("set", "get", "toggle")
                    "range", "colorPicker" -> listOf("set", "get")
                    "dropDownMenu" -> listOf("set", "get")
                    "list" -> listOf("add", "remove")
                    else -> emptyList()
                }
                else -> Category.getModule(module)?.settings?.firstOrNull { i -> i.id == setting }?.possibleValues ?: emptyList()
            }
        }
    }
    parsedCommand<BindCommandArgs>("bind", "Binds a module to a key.", ".bind module key", false, "b") {
        onCommand { (module, char) ->
            if (char != null) {
                val key = GLFWKeys[char]
                if (key != null) {
                    (Category.getModule(module) as? BoundModule)?.key = key
                    addChatMessageP("Module §6$module§f now bound to §a${GLFWKeys.getName(key)}§f.")
                    return@onCommand
                }
            }
            addChatMessageP("§cModule $module not found, it is not a bound module or the key is invalid.")
        }
        onTabComplete { (module, _) ->
            if (module == null) {
                Category.modules.filter { it is BoundModule }.map { it.id }
            } else {
                emptyList()
            }
        }
    }
}

private const val width = 40

data class ToggleCommandArgs(val module: String?, val state: String?)
data class HelpCommandArgs(val command: String?)
data class ModuleCommandArgs(val module: String?, val setting: String?, val action: String?, val value: String?)
data class BindCommandArgs(val module: String?, val char: String?)
