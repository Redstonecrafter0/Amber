package net.redstonecraft.amber.base.module.category

import net.redstonecraft.amber.AmberMod
import org.quiltmc.loader.api.ModContainer
import kotlin.reflect.KClass

abstract class AmberCategory(
    val mod: ModContainer,
    val id: String,
    val name: String
) {

    companion object {
        private val idRegister = mutableMapOf<String, AmberCategory>()
        private val classRegister = mutableMapOf<KClass<*>, AmberCategory>()

        fun <T : AmberCategory> registerCategory(category: T, clazz: KClass<out T>) {
            idRegister += category.fullId to category
            classRegister += clazz to category
        }

        inline fun <reified T: AmberCategory> registerCategory(category: T) {
            registerCategory(category, category::class)
        }

        fun getCategory(id: String): AmberCategory? {
            return idRegister[id]
        }

        fun getCategory(clazz: KClass<*>): AmberCategory? {
            return classRegister[clazz]
        }

        inline fun <reified T: AmberCategory> getCategory(): AmberCategory? {
            return getCategory(T::class)
        }
    }

    val fullId = "${mod.metadata().id()}:$id"

}

object WorldCategory : AmberCategory(AmberMod.mod, "world", "World")

object Visual : AmberCategory(AmberMod.mod, "visual", "Visual")
