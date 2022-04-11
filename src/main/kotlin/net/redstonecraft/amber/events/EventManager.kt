package net.redstonecraft.amber.events

import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

/**
 * The [EventManager] is used to register and fire events.
 * */
object EventManager {

    private val handlers = mutableMapOf<KClass<*>, MutableList<Pair<Int, (Event) -> Unit>>>()

    /**
     * Registers an event handler.
     *
     * @param clazz The class of the event.
     * @param priority The priority of the event.
     * @param block The event handler.
     * */
    fun on(clazz: KClass<*>, priority: Int = 0, block: (Event) -> Unit) {
        if (clazz !in handlers) handlers += mutableListOf()
        handlers[clazz]!! += priority to block
    }

    /**
     * Registers an event handler.
     *
     * @param E The class of the event.
     * @param priority The priority of the event.
     * @param block The event handler.
     * */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified E: Event> on(priority: Int = 0, noinline block: (E) -> Unit) = on(E::class, priority, block as (Event) -> Unit)

    /**
     * Fires an event.
     *
     * @param event The event to fire.
     * */
    fun <E: Event> fire(event: E): E {
        if (event::class !in handlers) return event
        for (handler in handlers.filter { (key, _) -> key == event::class || key.isSuperclassOf(event::class) }.values.flatten().sortedBy { it.first }.map { it.second }) {
            try {
                handler(event)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            if (event is CancellableEvent && event.isCancelled) break
        }
        return event
    }

}

/**
 * Registers an event handler.
 *
 * @param E The class of the event.
 * @param priority The priority of the event.
 * @param block The event handler.
 * */
inline fun <reified E: Event> on(priority: Int = 0, noinline block: (E) -> Unit) = EventManager.on(priority, block)
