package net.redstonecraft.amber.modules

import kotlin.reflect.jvm.jvmName

/**
 * @param displayName The display name of the category
 * */
@Suppress("LeakingThis")
abstract class Category(
    val displayName: String,
    val description: String
) {

    companion object {
        @JvmStatic
        val categories = mutableListOf<Category>()
    }

    /**
     * The unique identifier of the category
     * */
    val id = this::class.jvmName

    /**
     * All modules in this category
     * */
    val modules = mutableListOf<BaseModule>()

    init {
        categories += this
    }
}
