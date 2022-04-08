package net.redstonecraft.amber.modules

import kotlin.reflect.jvm.jvmName

@Suppress("LeakingThis")
abstract class Category(val displayName: String) {

    companion object {
        val categories = mutableListOf<Category>()
    }

    val id = this::class.jvmName

    val modules = mutableListOf<BaseModule>()

    init {
        categories += this
    }
}
