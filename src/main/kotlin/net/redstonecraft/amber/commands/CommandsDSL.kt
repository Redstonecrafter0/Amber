package net.redstonecraft.amber.commands

import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object CommandsContext {

    /**
     * Create a new command with a raw String as arguments.
     *
     * @param name The name of the command.
     * @param description The description of the command.
     * @param usage The usage of the command.
     * @param isAsync Whether the command is asynchronous.
     * @param aliases The aliases of the command.
     * @param block Configure the command.
     * */
    fun command(name: String, description: String, usage: String, isAsync: Boolean, vararg aliases: String, block: CommandContext<String>.() -> Unit) {
        val ctx = CommandContext<String>()
        ctx.block()
        if (ctx.run == null) return
        if (ctx.complete == null) {
            object: BaseCommand(name, description, usage, isAsync, *aliases) {
                override fun onCommand(raw: String) = ctx.run!!(CommandTools, raw)
            }
        } else {
            object : AutocompletedCommand(name, description, usage, isAsync, *aliases) {
                override fun onCommand(raw: String) = ctx.run!!(CommandTools, raw)
                override fun onTabComplete(raw: String) = ctx.complete!!(CommandTools, raw)
            }
        }
    }

    /**
     * Create a new command with a [T] as arguments.
     *
     * @param T The type of the arguments.
     * @param name The name of the command.
     * @param description The description of the command.
     * @param usage The usage of the command.
     * @param isAsync Whether the command is asynchronous.
     * @param aliases The aliases of the command.
     * @param block Configure the command.
     * */
    fun <T : Any> parsedCommand(clazz: KClass<T>, name: String, description: String, usage: String = ".$name " + defaultUsage(clazz), isAsync: Boolean, vararg aliases: String, block: CommandContext<T>.() -> Unit) {
        val ctx = CommandContext<T>()
        ctx.block()
        if (ctx.run == null) return
        if (ctx.complete == null) {
            object: ParsedCommand<T>(name, description, usage, isAsync, clazz, *aliases) {
                override fun onCommand(args: T) = ctx.run!!(CommandTools, args)
            }
        } else {
            object: AutocompletedParsedCommand<T>(name, description, usage, isAsync, clazz, *aliases) {
                override fun onCommand(args: T) = ctx.run!!(CommandTools, args)
                override fun onTabComplete(args: T) = ctx.complete!!(CommandTools, args)
            }
        }
    }

    /**
     * Create a new command with a [T] as arguments.
     *
     * @param T The type of the arguments.
     * @param name The name of the command.
     * @param description The description of the command.
     * @param usage The usage of the command.
     * @param isAsync Whether the command is asynchronous.
     * @param aliases The aliases of the command.
     * @param block Configure the command.
     * */
    inline fun <reified T: Any> parsedCommand(name: String, description: String, usage: String, isAsync: Boolean, vararg aliases: String, noinline block: CommandContext<T>.() -> Unit) =
        parsedCommand(T::class, name, description, usage, isAsync, *aliases, block = block)

    private fun defaultUsage(clazz: KClass<*>) = clazz.primaryConstructor!!.parameters.joinToString(" ") { when {
        it.isOptional || it.type.isMarkedNullable -> "<${it.name}>"
        it.isVararg -> "[${it.name}]"
        else -> it.name!!
    } }

    class CommandContext<T> internal constructor() {

        internal var run: (CommandTools.(T) -> Unit)? = null
        internal var complete: (CommandTools.(T) -> Iterable<String>)? = null

        /**
         * Set the runnable to be executed when the command is executed.
         *
         * @param block The runnable to be executed.
         * */
        fun onCommand(block: CommandTools.(T) -> Unit) {
            run = block
        }

        /**
         * Set the runnable to be executed when the command is autocompleted.
         *
         * @param block The runnable to be executed.
         * */
        fun onTabComplete(block: CommandTools.(T) -> Iterable<String>) {
            complete = block
        }

    }


}

object CommandTools {

    const val prefix = "??6[??lAmber??r??6] ??f"

    /**
     * Print a message to the player.
     *
     * @param message The message to be printed.
     * */
    @JvmStatic
    fun addChatMessage(message: Any?) = MinecraftClient.getInstance().inGameHud.chatHud.addMessage(LiteralText(message.toString()))

    /**
     * Print a message to the player with the prefix.
     *
     * @param message The message to be printed.
     * */
    @JvmStatic
    fun addChatMessageP(message: Any?) = addChatMessage(prefix + message)

    operator fun String.times(n: Int) = repeat(n)

}

/**
 * Entry point for commands DSL
 *
 * @param block The block to add commands.
 * */
fun commands(block: CommandsContext.() -> Unit) = CommandsContext.block()
