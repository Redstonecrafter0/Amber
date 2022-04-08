package net.redstonecraft.amber.modules

import org.lwjgl.glfw.GLFW
import java.awt.Color
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmName

@Suppress("LeakingThis")
abstract class BaseModule(
    val displayName: String,
    val description: String,
    val category: Category
) {

    val settings = mutableListOf<Setting<*>>()

    val id = this::class.jvmName

    inner class Setting<T>(
        val id: String,
        val displayName: String,
        var value: T,
        val extra: MutableMap<*, *>,
        val displayValueAdapter: (T, MutableMap<*, *>) -> String,
        val setter: (T, T, MutableMap<*, *>) -> T,
        val getter: (T, MutableMap<*, *>) -> Pair<T, T>,
        val renderer: String,
        val shouldShow: () -> Boolean
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

        init {
            settings += this
        }
    }

    inner class SettingProvider<T>(
        private val displayName: String,
        private val defaultValue: T,
        private val extra: MutableMap<*, *> = mutableMapOf<Any?, Any?>(),
        private val displayValueAdapter: (T, MutableMap<*, *>) -> String = { it, _ -> it.toString() },
        private val renderer: String = "default",
        private val shouldShow: () -> Boolean,
        init: SettingProvider<T>.() -> Unit = {}
    ) {

        private var setter: (T, T, MutableMap<*, *>) -> T = { _, it, _ -> it }
        private var getter: (T, MutableMap<*, *>) -> Pair<T, T> = { it, _ -> it to it }

        /**
         * A setter proxy the value goes through
         *
         * @param setter (old, new, extraData) -> valueToSet
         * */
        fun set(setter: (T, T, MutableMap<*, *>) -> T) {
            this.setter = setter
        }

        /**
         * A getter proxy the value goes through
         *
         * @param getter (value, extraData) -> returnedValue to valueToSet
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
            renderer,
            shouldShow
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
        shouldShow: () -> Boolean = { true },
        block: SettingProvider<T>.() -> Unit = {}
    ) = SettingProvider(displayName, defaultValue, extra, displayValueAdapter, renderer, shouldShow, block)

    fun button(displayName: String, shouldShow: () -> Boolean = { true }, block: () -> Unit) =
        SettingProvider(displayName, block, displayValueAdapter = { _, _, -> "" }, renderer = "button", shouldShow = shouldShow) {
            set { old, _, _ -> old(); old }
        }

    fun switch(displayName: String, defaultValue: Boolean, shouldShow: () -> Boolean = { true }, block: SettingProvider<Boolean>.() -> Unit = {}) =
        SettingProvider(displayName, defaultValue, renderer = "switch", shouldShow = shouldShow, init = block)

    fun <T: Number> range(displayName: String, defaultValue: T, min: T, step: T, max: T, shouldShow: () -> Boolean = { true }, block: SettingProvider<T>.() -> Unit = {}) =
        SettingProvider(displayName, defaultValue, mutableMapOf("min" to min, "step" to step, "max" to max), renderer = "range", shouldShow = shouldShow, init = block)

    fun colorPicker(displayName: String, defaultValue: Color, alpha: Boolean = true, shouldShow: () -> Boolean = { true }, block: SettingProvider<Color>.() -> Unit = {}) =
        SettingProvider(displayName, defaultValue, mutableMapOf("alpha" to alpha), { it, _ -> "#${it.rgb.toString(16).padStart(if (alpha) 8 else 6, '0')}" }, "colorPicker", shouldShow, block)

    fun dropDownMenu(displayName: String, defaultValue: String, available: MutableList<String>, shouldShow: () -> Boolean = { true }, block: SettingProvider<String>.() -> Unit = {}) =
        SettingProvider(displayName, defaultValue, mutableMapOf("available" to available), renderer = "dropDownMenu", shouldShow = shouldShow, init = block)

    fun <E: Enum<E>> dropDownMenu(displayName: String, defaultValue: E, displayValueAdapter: (E, MutableMap<*, *>) -> String = { it, _ -> it.name }, shouldShow: () -> Boolean = { true }, block: SettingProvider<E>.() -> Unit = {}) =
        SettingProvider(displayName, defaultValue, displayValueAdapter = displayValueAdapter, renderer = "dropDownMenu", shouldShow = shouldShow, init = block)

    fun list(displayName: String, defaultValue: List<String>, shouldShow: () -> Boolean = { true }, block: SettingProvider<List<String>>.() -> Unit = {}) =
        SettingProvider(displayName, defaultValue, displayValueAdapter = { it, _ -> it.joinToString("\n") }, renderer = "list", shouldShow = shouldShow, init = block)

    infix fun <T> KProperty<T>.eq(value: T): () -> Boolean = { getter.call() == value }
    infix fun <T> KProperty<T>.neq(value: T): () -> Boolean = { getter.call() != value }
    infix fun <T> T.isIn(value: KProperty<Collection<T>>): () -> Boolean = { this in value.getter.call() }
    infix fun <T> T.notIn(value: KProperty<Collection<T>>): () -> Boolean = { this !in value.getter.call() }
    infix fun <T: Comparable<T>> KProperty<T>.lt(value: T): () -> Boolean = { getter.call() < value }
    infix fun <T: Comparable<T>> KProperty<T>.gt(value: T): () -> Boolean = { getter.call() > value }
    infix fun <T: Comparable<T>> KProperty<T>.leq(value: T): () -> Boolean = { getter.call() <= value }
    infix fun <T: Comparable<T>> KProperty<T>.geq(value: T): () -> Boolean = { getter.call() >= value }

    init {
        category.modules += this
    }
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
    var enabled: Boolean = false,
    val preventEnableOnLoad: Boolean = false,
    override var key: Int = GLFW.GLFW_KEY_UNKNOWN
): BaseModule(displayName, description, category), BoundModule {

    fun toggle() {
        if (enabled)
            disable()
        else
            enable()
    }

    fun enable() {
        if (!enabled) {
            onEnable()
        }
    }

    fun disable() {
        if (enabled) {
            onDisable()
        }
    }

    open fun onEnable() {}
    open fun onDisable() {}
}
