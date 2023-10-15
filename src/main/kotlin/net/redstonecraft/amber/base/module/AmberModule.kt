package net.redstonecraft.amber.base.module

import kotlinx.serialization.Transient
import net.redstonecraft.amber.base.module.category.AmberCategory
import org.quiltmc.loader.api.ModContainer

/**
 * This class is free to use for config-only modules.
 * */
abstract class AmberModule(
    val modId: String,
    val id: String,
    @Transient val name: String,
    @Transient val category: AmberCategory,
    @Transient val description: String
) {

    constructor(
        mod: ModContainer,
        id: String,
        name: String,
        category: AmberCategory,
        description: String
    ) : this(mod.metadata().id(), id, name, category, description)

}

abstract class AmberToggleModule(
    mod: ModContainer,
    id: String,
    name: String,
    category: AmberCategory,
    description: String
) : AmberModule(mod, id, name, category, description) {

    var isEnabled: Boolean = false
        private set

    open fun onToggle() {}
    open fun onEnable() {}
    open fun onDisable() {}

    fun toggle() {
        if (isEnabled) {
            disable()
        } else {
            enable()
        }
    }

    fun enable() {
        if (!isEnabled) {
            isEnabled = true
            onEnable()
        }
    }

    fun disable() {
        if (isEnabled) {
            isEnabled = false
            onDisable()
        }
    }

}

abstract class AmberTriggerModule(
    mod: ModContainer,
    id: String,
    name: String,
    category: AmberCategory,
    description: String
) : AmberModule(mod, id, name, category, description) {

    fun onTrigger() {}

}
