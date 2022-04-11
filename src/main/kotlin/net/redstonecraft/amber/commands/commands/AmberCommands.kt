package net.redstonecraft.amber.commands.commands

import net.redstonecraft.amber.Amber
import net.redstonecraft.amber.commands.CommandManager
import net.redstonecraft.amber.commands.commands
import net.redstonecraft.amber.modules.Category
import net.redstonecraft.amber.modules.ToggleModule

fun setupAmberCommands() {
    commands {
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
                addChatMessage("=" * 20)
                addChatMessage("Amber ${Amber.VERSION}")
                addChatMessage("by ${Amber.AUTHORS.joinToString(", ")}")
                addChatMessage("=" * 20)
            }
        }
        parsedCommand<HelpCommandArgs>("help", "Get information about a command.", ".help <command>", false, "?", "h") {
            onCommand {
                val cmd = if (it.command != null) {
                    CommandManager.commands.filter { (k, _) -> it.command in k }.values.firstOrNull()
                } else null
                if (cmd != null) {
                    addChatMessage(cmd.name)
                    addChatMessage(cmd.description)
                    addChatMessage(cmd.usage)
                    addChatMessage("Aliases:")
                    for (i in cmd.aliases) {
                        addChatMessage(i)
                    }
                } else {
                    // TODO: finish
                }
            }
        }
    }
}

private operator fun String.times(n: Int) = repeat(n)

data class ToggleCommandArgs(val module: String?, val state: String?)
data class HelpCommandArgs(val command: String?)
