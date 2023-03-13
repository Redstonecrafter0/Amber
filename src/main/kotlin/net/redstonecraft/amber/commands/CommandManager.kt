package net.redstonecraft.amber.commands

import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.willowtreeapps.fuzzywuzzy.diffutils.FuzzySearch
import java.util.concurrent.Executors
import kotlin.math.min

/**
 * The command manager.
 * */
object CommandManager {

    private val threadPool = Executors.newCachedThreadPool()

    val commands = mutableMapOf<List<String>, BaseCommand>()

    /**
     * Execute a command
     *
     * @param text The command to execute
     * */
    fun dispatch(text: String) {
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
        } else {
            val list = FuzzySearch.extractTop(text.substring(1), commands.keys.flatten(), 5).filter { it.score >= 40 }.sortedBy { it.score }.map { it.string!! }
            if (list.isEmpty()) {
                CommandTools.addChatMessageP("§cCommand not found.")
            } else {
                CommandTools.addChatMessageP("§cCommand not found. Did you mean §f${list.joinToString(", ")}?")
            }
        }
    }

    @JvmStatic
    fun tabComplete(text: String, cursor: Int): Suggestions? {
        if (text.startsWith(".") && !text.startsWith("..")) {
            val (cmd, args) = parse(text.substring(1))
            val command = commands.filterKeys { cmd in it }.values.firstOrNull()
            if (command is AutocompletedCommand) {
                val list = command.runComplete(args)
                if (list != null) {
                    return SuggestionsBuilder(
                        text,
                        text.lowercase(),
                        min(text.lastIndexOf(" ") + 1, cursor)
                    ).apply {
                        for (i in list) {
                            suggest(i)
                        }
                    }.build()
                }
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
