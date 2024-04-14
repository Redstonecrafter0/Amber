package net.redstonecraft.amber.base.config.type

import net.redstonecraft.amber.AmberMod
import net.redstonecraft.amber.base.event.ConfigSettingChangeEvent
import net.redstonecraft.amber.base.module.AmberModule
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

abstract class AmberConfigSetting<T>(
    value: T,
    val displayName: String,
    val description: String?,
    val module: AmberModule
) {

    var value: T = value
        set(value) {
            val event = ConfigSettingChangeEvent(module, this)
            AmberMod.eventManager.fire(event)
            field = value
        }

    private var hiddenFunction: () -> Boolean = { false }

    lateinit var id: String
        private set

    val isHidden: Boolean
        get() = hiddenFunction()

    abstract fun serialize(): String
    abstract fun deserialize(data: String)

    fun hideIf(block: () -> Boolean): AmberConfigSetting<T> {
        hiddenFunction = block
        return this
    }

    fun hideIfTrue(block: KProperty0<Boolean>): AmberConfigSetting<T> {
        hiddenFunction = { block.get() }
        return this
    }

    fun hideIfFalse(block: KProperty0<Boolean>): AmberConfigSetting<T> {
        hiddenFunction = { !block.get() }
        return this
    }

    operator fun provideDelegate(moduleRef: AmberModule, property: KProperty<*>): KMutableProperty0<T> {
        id = property.name
        return ::value
    }

}
