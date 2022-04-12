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

        /**
         * Gets the module by its id
         * */
        fun getModule(id: String?) = if (id == null) null else categories.map { it.modules }.flatten().firstOrNull { it.id == id }
    }

    /**
     * The unique identifier of the category
     * */
    val id = this::class.simpleName!!

    /**
     * All modules in this category
     * */
    val modules = mutableListOf<BaseModule>()

    init {
        categories += this
    }
}
