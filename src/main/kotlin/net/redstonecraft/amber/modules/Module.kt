package net.redstonecraft.amber.modules

import org.lwjgl.glfw.GLFW
import java.awt.Color
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmName

abstract class BaseModule(
    val displayName: String,
    val description: String,
    val category: Category
) {

    val name = this::class.jvmName

    inner class Setting<T>(
        val name: String,
        val displayName: String,
        var value: T,
        val extra: MutableMap<*, *>,
        val displayValueAdapter: (T, MutableMap<*, *>) -> String,
        val setter: (T, T, MutableMap<*, *>) -> T,
        val getter: (T, MutableMap<*, *>) -> Pair<T, T>,
        val renderer: String
    ) {

        val displayValue: String
            get() = displayValueAdapter(value, extra)

        operator fun setValue(ref: BaseModule, prop: KProperty<*>, value: T) {
            this.value = setter(this.value, value, extra)
        }

        operator fun getValue(ref: BaseModule, prop: KProperty<*>): T {
            val (ret, set) = getter(value, extra)
            value = set
            return ret
        }
    }

    inner class SettingProvider<T>(
        private val displayName: String,
        private val defaultValue: T,
        private val extra: MutableMap<*, *> = mutableMapOf<Any?, Any?>(),
        private val displayValueAdapter: (T, MutableMap<*, *>) -> String = { it, _ -> it.toString() },
        private val renderer: String = "default",
        init: SettingProvider<T>.() -> Unit = {}
    ) {

        private var setter: (T, T, MutableMap<*, *>) -> T = { _, it, _ -> it }
        private var getter: (T, MutableMap<*, *>) -> Pair<T, T> = { it, _ -> it to it }

        /**
         * A setter proxy the value goes through
         *
         * @param setter (old, new, extraData) -> setValue
         * */
        fun set(setter: (T, T, MutableMap<*, *>) -> T) {
            this.setter = setter
        }

        /**
         * A getter proxy the value goes through
         *
         * @param getter (value, extraData) -> returned to setValue
         * */
        fun get(getter: (T, MutableMap<*, *>) -> Pair<T, T>) {
            this.getter = getter
        }

        operator fun provideDelegate(ref: BaseModule, prop: KProperty<*>) = Setting(
            prop.name,
            displayName,
            defaultValue,
            extra,
            displayValueAdapter,
            setter,
            getter,
            renderer
        )

        init {
            init()
        }
    }

    fun <T> customSetting(
        displayName: String,
        defaultValue: T,
        displayValueAdapter: (T, MutableMap<*, *>) -> String = { it, _ -> it.toString() },
        renderer: String = "default",
        extra: MutableMap<*, *> = mutableMapOf<Any?, Any?>(),
        block: SettingProvider<T>.() -> Unit
    ) = SettingProvider(displayName, defaultValue, extra, displayValueAdapter, renderer, block)

    fun button(displayName: String, block: () -> Unit) =
        SettingProvider(displayName, block, displayValueAdapter = { _, _, -> "" }, renderer = "button")

    fun switch(displayName: String, defaultValue: Boolean, block: SettingProvider<Boolean>.() -> Unit) =
        SettingProvider(displayName, defaultValue, renderer = "switch", init = block)

    fun range(displayName: String, defaultValue: Float, min: Float = 0F, step: Float = .1F, max: Float = 10F, block: SettingProvider<Float>.() -> Unit) =
        SettingProvider(displayName, defaultValue, mutableMapOf("min" to min, "step" to step, "max" to max), renderer = "range", init = block)

    fun range(displayName: String, defaultValue: Double, min: Double = .0, step: Double = .1, max: Double = 10.0, block: SettingProvider<Double>.() -> Unit) =
        SettingProvider(displayName, defaultValue, mutableMapOf("min" to min, "step" to step, "max" to max), renderer = "range", init = block)

    fun range(displayName: String, defaultValue: Int, min: Int = 0, step: Int = 1, max: Int = 100, block: SettingProvider<Int>.() -> Unit) =
        SettingProvider(displayName, defaultValue, mutableMapOf("min" to min, "step" to step, "max" to max), renderer = "range", init = block)

    fun range(displayName: String, defaultValue: Long, min: Long = 0, step: Long = 1, max: Long = 100, block: SettingProvider<Long>.() -> Unit) =
        SettingProvider(displayName, defaultValue, mutableMapOf("min" to min, "step" to step, "max" to max), renderer = "range", init = block)

    fun colorPicker(displayName: String, defaultValue: Color, alpha: Boolean = true, block: SettingProvider<Color>.() -> Unit) =
        SettingProvider(displayName, defaultValue, mutableMapOf("alpha" to alpha), { it, _ -> "#${it.rgb.toString(16).padStart(if (alpha) 8 else 6, '0')}" }, "colorPicker", block)

    fun dropDownMenu(displayName: String, defaultValue: String, available: MutableList<String>, block: SettingProvider<String>.() -> Unit) =
        SettingProvider(displayName, defaultValue, mutableMapOf("available" to available), renderer = "dropDownMenu", init = block)

    fun <E: Enum<E>> dropDownMenu(displayName: String, defaultValue: E, displayValueAdapter: (E, MutableMap<*, *>) -> String = { it, _ -> it.name }, block: SettingProvider<E>.() -> Unit) =
        SettingProvider(displayName, defaultValue, displayValueAdapter = displayValueAdapter, renderer = "dropDownMenu", init = block)

    fun list(displayName: String, defaultValue: List<String>, block: SettingProvider<List<String>>.() -> Unit) =
        SettingProvider(displayName, defaultValue, displayValueAdapter = { it, _ -> it.joinToString("\n") }, renderer = "list", init = block)
}

interface BoundModule {
    var key: Int
}

abstract class TriggerModule(
    displayName: String,
    description: String,
    category: Category,
    override var key: Int = GLFW.GLFW_KEY_UNKNOWN
): BaseModule(displayName, description, category), BoundModule {

    abstract fun run()
}

abstract class ToggleModule(
    displayName: String,
    description: String,
    category: Category,
    override var key: Int = GLFW.GLFW_KEY_UNKNOWN
): BaseModule(displayName, description, category), BoundModule {

    fun onEnable() {}
    fun onDisable() {}
}
