package net.redstonecraft.amber.base.module

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import net.redstonecraft.amber.base.config.type.AmberConfigSetting
import net.redstonecraft.amber.base.event.ModuleEvent
import net.redstonecraft.amber.base.module.category.AmberCategory
import java.io.Closeable
import kotlin.reflect.full.primaryConstructor

/**
 * This class is free to use for config-only modules.
 * */
@Serializable(with = AmberModuleSerializer::class)
abstract class AmberModule(
    val modId: String,
    val id: String,
    val name: String,
    val category: AmberCategory,
    val description: String
): Closeable {

    internal var config: Map<String, String>
        get() = configSettings.associate { it.id to it.serialize() }
        set(value) = value.forEach { (id, data) ->
            configSettings.find { it.id == id }?.deserialize(data)
        }

    private val configSettings = mutableListOf<AmberConfigSetting<*>>()

    fun <T: AmberConfigSetting<*>> registerConfigSetting(setting: T): T {
        configSettings += setting
        return setting
    }

}

abstract class AmberToggleModule(
    modId: String,
    id: String,
    name: String,
    category: AmberCategory,
    description: String
) : AmberModule(modId, id, name, category, description) {

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
            val event = ModuleEvent(this, ModuleEvent.Action.ENABLED)
            if (event.isCancelled) {
                return
            }
            isEnabled = true
            onEnable()
        }
    }

    fun disable() {
        if (isEnabled) {
            val event = ModuleEvent(this, ModuleEvent.Action.DISABLED)
            if (event.isCancelled) {
                return
            }
            isEnabled = false
            onDisable()
        }
    }

}

abstract class AmberTriggerModule(
    modId: String,
    id: String,
    name: String,
    category: AmberCategory,
    description: String
) : AmberModule(modId, id, name, category, description) {

    fun trigger() {
        val event = ModuleEvent(this, ModuleEvent.Action.TRIGGERED)
        if (event.isCancelled) {
            return
        }
        onTrigger()
    }

    open fun onTrigger() {}

}

@Serializable
data class AmberModuleData(
    val `class`: String,
    val enabled: Boolean? = null,
    val config: Map<String, String>
) {

    constructor(module: AmberModule) : this(module.javaClass.canonicalName, if (module is AmberToggleModule) module.isEnabled else null, module.config)

}

object AmberModuleSerializer: KSerializer<AmberModule?> {

    private val delegateSerializer: KSerializer<AmberModuleData?> = serializer()
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: AmberModule?) {
        if (value != null) {
            encoder.encodeSerializableValue(delegateSerializer, AmberModuleData(value))
        } else {
            encoder.encodeSerializableValue(delegateSerializer, null)
        }
    }

    override fun deserialize(decoder: Decoder): AmberModule? {
        val moduleData = decoder.decodeSerializableValue(delegateSerializer) ?: return null
        return try {
            val module = Class.forName(moduleData.`class`).kotlin.primaryConstructor!!.call() as AmberModule
            module.config = moduleData.config
            if (module is AmberToggleModule) {
                module.enable()
            }
            module
        } catch (e: ClassNotFoundException) {
            null
        }
    }

}
