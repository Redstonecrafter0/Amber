package net.redstonecraft.amber.commands

import edu.rice.cs.util.ArgumentTokenizer
import net.redstonecraft.amber.Amber
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * The base class for all commands.
 * Automatically registers the command with the [CommandManager].
 *
 * @param name The name of the command.
 * @param description The description of the command.
 * @param usage The usage of the command.
 * @param isAsync Whether the command is asynchronous.
 * @param aliases The aliases of the command.
 * */
@Suppress("LeakingThis")
abstract class BaseCommand(
    val name: String,
    val description: String,
    val usage: String,
    val isAsync: Boolean = false,
    vararg val aliases: String
) {

    internal fun run(args: String) {
        try {
            onCommand(args)
        } catch (e: IllegalStateException) {
            CommandTools.addChatMessage(usage)
            if (Amber.debug) CommandTools.addChatMessage(e.stackTraceToString().replace("\r", "").replace("\t", "    "))
        } catch (e: IllegalArgumentException) {
            CommandTools.addChatMessage(usage)
            if (Amber.debug) CommandTools.addChatMessage(e.stackTraceToString().replace("\r", "").replace("\t", "    "))
        }
    }

    abstract fun onCommand(raw: String)

    init {
        CommandManager.commands += (listOf(name) + aliases) to this
    }
}

/**
 * The base class for all commands that can autocomplete.
 * Automatically registers the command with the [CommandManager].
 *
 * @param name The name of the command.
 * @param description The description of the command.
 * @param usage The usage of the command.
 * @param isAsync Whether the command is asynchronous.
 * @param aliases The aliases of the command.
 * */
abstract class AutocompletedCommand(
    name: String,
    description: String,
    usage: String,
    isAsync: Boolean = false,
    vararg aliases: String
): BaseCommand(name, description, usage, isAsync, *aliases) {

    internal fun runComplete(args: String): Iterable<String>? {
        try {
            return onTabComplete(args)
        } catch (e: IllegalStateException) {
            CommandTools.addChatMessage(usage)
            if (Amber.debug) CommandTools.addChatMessage(e.stackTraceToString().replace("\r", "").replace("\t", "    "))
        } catch (e: IllegalArgumentException) {
            CommandTools.addChatMessage(usage)
            if (Amber.debug) CommandTools.addChatMessage(e.stackTraceToString().replace("\r", "").replace("\t", "    "))
        }
        return null
    }

    abstract fun onTabComplete(raw: String): Iterable<String>
}

private fun parseValue(clazz: KClass<*>, s: String?) = when (clazz) {
    String::class -> s
    Int::class -> s?.toIntOrNull()
    Long::class -> s?.toLongOrNull()
    Float::class -> s?.toFloatOrNull()
    Double::class -> s?.toDoubleOrNull()
    Boolean::class -> s?.toBooleanStrictOrNull()
    else -> null
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> parseArgs(raw: String, clazz: KClass<T>): T {
    val parsed = ArgumentTokenizer.tokenize(raw)
    val params = clazz.primaryConstructor?.parameters ?: error("Commands arguments has no primary constructor")
    if ((params.isEmpty() && parsed.isNotEmpty()) || ((parsed.size > params.size) && !params.last().isVararg)) error("Too many arguments")
    val values = params
        .mapIndexed { index, it -> it to if (it.isVararg && index == params.lastIndex) {
            val type = it.type.jvmErasure.java.componentType.kotlin
            if (index >= parsed.size) {
                arrayOf<String>()
            } else {
                parsed.subList(index, parsed.size).toTypedArray()
            }
        } else {
            parsed.getOrNull(index).let { s ->
                if (!(s == null && it.isOptional)) {
                    parseValue(it.type.jvmErasure, s)
                } else Unit
            }
        }
        }
        .filter { it.second != Unit }
        .toMap()
    if (values.any { (k, v) -> !k.type.isMarkedNullable && v == null }) error("Missing arguments.")
    return clazz.primaryConstructor!!.callBy(values)
}

/**
 * The base class for all commands that have advanced arguments.
 * Automatically registers the command with the [CommandManager].
 *
 * @param name The name of the command.
 * @param description The description of the command.
 * @param usage The usage of the command.
 * @param isAsync Whether the command is asynchronous.
 * @param aliases The aliases of the command.
 * */
abstract class ParsedCommand<T : Any>(
    name: String,
    description: String,
    usage: String,
    isAsync: Boolean = false,
    private val clazz: KClass<T>,
    vararg aliases: String
): BaseCommand(name, description, usage, isAsync, *aliases) {

    final override fun onCommand(raw: String) = onCommand(parseArgs(raw, clazz))

    abstract fun onCommand(args: T)
}

/**
 * The base class for all commands that have advanced arguments and have autocompletion.
 * Automatically registers the command with the [CommandManager].
 *
 * @param name The name of the command.
 * @param description The description of the command.
 * @param usage The usage of the command.
 * @param isAsync Whether the command is asynchronous.
 * @param aliases The aliases of the command.
 * */
abstract class AutocompletedParsedCommand<T : Any>(
    name: String,
    description: String,
    usage: String,
    isAsync: Boolean = false,
    private val clazz: KClass<T>,
    vararg aliases: String
): AutocompletedCommand(name, description, usage, isAsync, *aliases) {

    final override fun onCommand(raw: String) = onCommand(parseArgs(raw, clazz))

    abstract fun onCommand(args: T)

    final override fun onTabComplete(raw: String): Iterable<String> = onTabComplete(parseArgs(raw, clazz))

    abstract fun onTabComplete(args: T): Iterable<String>
}
