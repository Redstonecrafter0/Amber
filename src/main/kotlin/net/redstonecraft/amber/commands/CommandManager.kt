package net.redstonecraft.amber.commands

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
    fun dispatch(command: String) {
        println("cmd")
        val (cmd, args) = parse(command)
        println(cmd)
        println(args)
        val command = commands.filterKeys { cmd in it }.values.firstOrNull()
        println(command?.usage)
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

    fun tabComplete(text: String) {
        val (cmd, args) = parse(text) ?: return
        val command = commands.filterKeys { cmd in it }.values.firstOrNull()
        if (command is AutocompletedCommand) {
            if (command.isAsync) {
                threadPool.submit { command.onTabComplete(args) }
            } else {
                command.onTabComplete(args)
            }
        }
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
