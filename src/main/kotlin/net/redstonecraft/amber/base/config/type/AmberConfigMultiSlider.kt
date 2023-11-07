package net.redstonecraft.amber.base.config.type

import net.redstonecraft.amber.base.module.AmberModule

class AmberConfigMultiSlider<T: Number>(
    value: List<T>,
    displayName: String,
    description: String?,
    val min: T,
    val max: T,
    val step: T,
    module: AmberModule,
    private val deserializer: (String) -> T
): AmberConfigSetting<MutableList<T>>(value.toMutableList(), displayName, description, module) {

    override fun serialize() = value.joinToString(",")

    override fun deserialize(data: String) {
        value = data.split(",").map { deserializer(it) }.toMutableList()
    }

}

fun AmberModule.multislider(default: List<Byte>, displayName: String, min: Byte = 0, max: Byte = 100, step: Byte = 1, description: String? = null): AmberConfigMultiSlider<Byte> {
    return registerConfigSetting(AmberConfigMultiSlider(default, displayName, description, min, max, step, this) { it.toByte() })
}

fun AmberModule.multislider(default: List<Short>, displayName: String, min: Short = 0, max: Short = 100, step: Short = 1, description: String? = null): AmberConfigMultiSlider<Short> {
    return registerConfigSetting(AmberConfigMultiSlider(default, displayName, description, min, max, step, this) { it.toShort() })
}

fun AmberModule.multislider(default: List<Int>, displayName: String, min: Int = 0, max: Int = 100, step: Int = 1, description: String? = null): AmberConfigMultiSlider<Int> {
    return registerConfigSetting(AmberConfigMultiSlider(default, displayName, description, min, max, step, this) { it.toInt() })
}

fun AmberModule.multislider(default: List<Long>, displayName: String, min: Long = 0, max: Long = 100, step: Long = 1, description: String? = null): AmberConfigMultiSlider<Long> {
    return registerConfigSetting(AmberConfigMultiSlider(default, displayName, description, min, max, step, this) { it.toLong() })
}

fun AmberModule.multislider(default: List<Float>, displayName: String, min: Float = 0f, max: Float = 100f, step: Float = 1f, description: String? = null): AmberConfigMultiSlider<Float> {
    return registerConfigSetting(AmberConfigMultiSlider(default, displayName, description, min, max, step, this) { it.toFloat() })
}

fun AmberModule.multislider(default: List<Double>, displayName: String, min: Double = .0, max: Double = 100.0, step: Double = 1.0, description: String? = null): AmberConfigMultiSlider<Double> {
    return registerConfigSetting(AmberConfigMultiSlider(default, displayName, description, min, max, step, this) { it.toDouble() })
}
