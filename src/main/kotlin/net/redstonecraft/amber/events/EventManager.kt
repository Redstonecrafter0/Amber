package net.redstonecraft.amber.events

import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

object EventManager {

    private val handlers = mutableMapOf<KClass<*>, MutableList<Pair<Int, (Event) -> Unit>>>()

    fun on(clazz: KClass<*>, priority: Int = 0, block: (Event) -> Unit) {
        if (clazz !in handlers) handlers += mutableListOf()
        handlers[clazz]!! += priority to block
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified E: Event> on(priority: Int = 0, noinline block: (E) -> Unit) = on(E::class, priority, block as (Event) -> Unit)

    fun <E: Event> fire(event: E): E {
        if (event::class !in handlers) return event
        for (handler in handlers.filter { (key, _) -> key == event::class || key.isSuperclassOf(event::class) }.values.flatten().sortedBy { it.first }.map { it.second }) {
            handler(event)
            if (event is CancellableEvent && event.isCancelled) break
        }
        return event
    }

}

inline fun <reified E: Event> on(priority: Int = 0, noinline block: (E) -> Unit) = EventManager.on(priority, block)
