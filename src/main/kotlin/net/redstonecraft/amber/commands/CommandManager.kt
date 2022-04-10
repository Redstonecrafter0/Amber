package net.redstonecraft.amber.commands

import com.mojang.brigadier.suggestion.Suggestion
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import java.util.concurrent.Executors

/**
 * The command manager.
 * */
object CommandManager {

    private val threadPool = Executors.newCachedThreadPool()

    val commands = mutableMapOf<List<String>, BaseCommand>()

    /**
     * Execute a command
     *
     * @param command The command to execute
     * */
    fun dispatch(text: String) {
        println("cmd")
        val (cmd, args) = parse(text)
        val command = commands.filterKeys { cmd in it }.values.firstOrNull()
        if (command != null) {
            if (command.isAsync) {
                threadPool.submit {
                    command.run(args)
                }
            } else {
                command.run(args)
            }
        }
    }

    @JvmStatic
    fun tabComplete(text: String): Pair<List<String>, String>? {
        val screen = MinecraftClient.getInstance().currentScreen
        if (text.startsWith(".") && !text.startsWith("..") && screen is ChatScreen) {
            val (cmd, args) = parse(text)
            val command = commands.filterKeys { cmd in it }.values.firstOrNull()
            if (command is AutocompletedCommand) {
                return command.onTabComplete(args).first.toList() to cmd
            }
        }
        return null
    }

    private fun parse(text: String): Pair<String, String> {
        if (" " !in text) return text to ""
        val cmd = text.substring(0 until text.indexOf(' '))
        val args = try {
            text.substring(text.indexOf(' ') + 1)
        } catch (_: Throwable) { "" }
        return cmd to args
    }

}
