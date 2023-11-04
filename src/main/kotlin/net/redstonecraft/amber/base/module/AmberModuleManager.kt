package net.redstonecraft.amber.base.module

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class AmberModuleManager {

    private val moduleRegistry = mutableListOf<KClass<*>>()

    val loadedModules = mutableListOf<AmberModule>()

    fun <T: AmberModule> register(module: KClass<T>) {
        moduleRegistry += module
    }

    inline fun <reified T: AmberModule> register() {
        register(T::class)
    }

    fun loadUnloadedModules() {
        for (i in moduleRegistry) {
            if (!loadedModules.any { i.isInstance(it) }) {
                loadedModules += i.primaryConstructor!!.call() as AmberModule
            }
        }
    }

    fun unloadAllModules() {
        loadedModules.forEach { it.close() }
        loadedModules.clear()
    }

}
