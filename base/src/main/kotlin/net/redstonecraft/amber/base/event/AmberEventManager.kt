package net.redstonecraft.amber.base.event

import java.util.*
import kotlin.reflect.KClass

class AmberEventManager {

    private val listeners = mutableMapOf<KClass<*>, MutableList<(IAmberEvent) -> Unit>>()

    fun <T: IAmberEvent> on(clazz: KClass<T>, block: (T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        listeners.getOrPut(clazz) { LinkedList() } += block as (IAmberEvent) -> Unit
    }

    inline fun <reified T: IAmberEvent> on(noinline block: (T) -> Unit) {
        on(T::class, block)
    }

    fun fire(event: IAmberEvent) {
        listeners[event::class]?.forEach {
            it(event)
        }
    }

    fun fire(event: IAmberCancellableEvent) {
        val list = listeners[event::class] ?: return
        for (it in list) {
            it(event)
            if (event.isCancelled) {
                return
            }
        }
    }

}
