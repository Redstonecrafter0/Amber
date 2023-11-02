package net.redstonecraft.amber.base.module

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import net.redstonecraft.amber.base.config.AmberConfigData
import net.redstonecraft.amber.base.module.category.AmberCategory
import org.quiltmc.loader.api.ModContainer

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

@Serializable
data class AmberModuleData(
    val modId: String,
    val id: String,
    val config: Map<String, AmberConfigData>
)

object AmberModuleSerializer: KSerializer<AmberModule> {

    private val delegateSerializer: KSerializer<Map<String, Any>> = serializer()
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: AmberModule) {
        encoder.encodeSerializableValue(delegateSerializer, mutableMapOf())
    }

    override fun deserialize(decoder: Decoder): AmberModule {
        val value = decoder.decodeSerializableValue(delegateSerializer)
    }

}
