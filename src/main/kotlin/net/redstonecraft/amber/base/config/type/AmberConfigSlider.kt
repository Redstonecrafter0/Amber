package net.redstonecraft.amber.base.config.type

import net.redstonecraft.amber.base.module.AmberModule

class AmberConfigSlider<T: Number>(
    value: T,
    displayName: String,
    description: String?,
    val min: T,
    val max: T,
    val step: T,
    private val deserializer: (String) -> T
): AmberConfigSetting<T>(value, displayName, description) {

    override fun serialize() = value.toString()

    override fun deserialize(data: String) {
        value = deserializer(data)
    }

}

fun AmberModule.slider(default: Byte, displayName: String, min: Byte = 0, max: Byte = 100, step: Byte = 1, description: String? = null): AmberConfigSlider<Byte> {
    return registerConfigSetting(AmberConfigSlider(default, displayName, description, min, max, step) { it.toByte() })
}

fun AmberModule.slider(default: Short, displayName: String, min: Short = 0, max: Short = 100, step: Short = 1, description: String? = null): AmberConfigSlider<Short> {
    return registerConfigSetting(AmberConfigSlider(default, displayName, description, min, max, step) { it.toShort() })
}

fun AmberModule.slider(default: Int, displayName: String, min: Int = 0, max: Int = 100, step: Int = 1, description: String? = null): AmberConfigSlider<Int> {
    return registerConfigSetting(AmberConfigSlider(default, displayName, description, min, max, step) { it.toInt() })
}

fun AmberModule.slider(default: Long, displayName: String, min: Long = 0, max: Long = 100, step: Long = 1, description: String? = null): AmberConfigSlider<Long> {
    return registerConfigSetting(AmberConfigSlider(default, displayName, description, min, max, step) { it.toLong() })
}

fun AmberModule.slider(default: Float, displayName: String, min: Float = 0f, max: Float = 100f, step: Float = 1f, description: String? = null): AmberConfigSlider<Float> {
    return registerConfigSetting(AmberConfigSlider(default, displayName, description, min, max, step) { it.toFloat() })
}

fun AmberModule.slider(default: Double, displayName: String, min: Double = .0, max: Double = 100.0, step: Double = 1.0, description: String? = null): AmberConfigSlider<Double> {
    return registerConfigSetting(AmberConfigSlider(default, displayName, description, min, max, step) { it.toDouble() })
}
