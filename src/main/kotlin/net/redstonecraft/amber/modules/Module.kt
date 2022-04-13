package net.redstonecraft.amber.modules

import kotlinx.serialization.json.*
import net.redstonecraft.amber.config.ConfigManager
import net.redstonecraft.amber.events.EventManager
import net.redstonecraft.amber.events.ModuleDisableEvent
import net.redstonecraft.amber.events.ModuleEnableEvent
import net.redstonecraft.amber.events.ModuleTriggerEvent
import org.lwjgl.glfw.GLFW
import java.awt.Color
import kotlin.reflect.KProperty

/**
 * The base class for all modules.
 *
 * Available settings:
 * - [button]
 * - [switch]
 * - [range]
 * - [colorPicker]
 * - [dropDownMenu]
 * - [list]
 * - [customSetting]
 *
 * Adding events:
 * ```kotlin
 * object YourModule: BaseModule(...) {
 *     init {
 *         on<Event> { // Respects super classes
 *             // Do something
 *         }
 *     }
 * }
 * ```
 *
 * Adding settings:
 * ```kotlin
 * object YourModule: BaseModule(...) {
 *     val id by button("displayName") {
 *         // Do something
 *     }
 * }
 * ```
 *
 * @property category The category of the module.
 * @property description The description of the module.
 * @property displayName The display name of the module.
 *
 * @see [ToggleModule] for a module that can be toggled.
 * @see [TriggerModule] for a module that can be triggered.
 */
@Suppress("LeakingThis")
abstract class BaseModule(
    val displayName: String,
    val description: String,
    val category: Category
) {

    /**
     * All settings of this module.
     */
    val settings = mutableListOf<Setting<*>>()

    /**
     * The unique identifier of this module.
     */
    val id = this::class.simpleName!!

    /**
     * The holder class of a setting.
     *
     * @property id The unique identifier of the setting.
     * @property displayName The display name of the setting.
     * @property value The value of the setting.
     * @property description The description of the setting.
     * @property extra Extra information of the setting that can be saved.
     * @property displayValueAdapter The adapter that converts the value to a display value.
     * @property setter The setter of the setting's value.
     * @property getter The getter of the setting's value.
     * @property renderer The renderer of the setting.
     * @property shouldShow Compute whether the setting should be shown. See [eq] [neq] [isIn] [notIn] [lt] [gt] [leq] [geq].
     */
    inner class Setting<T> internal constructor(
        val id: String,
        val displayName: String,
        var value: T,
        val extra: MutableMap<*, *>,
        val displayValueAdapter: (T, MutableMap<*, *>) -> String,
        val setter: (T, T, MutableMap<*, *>) -> T,
        val getter: (T, MutableMap<*, *>) -> T,
        val renderer: String,
        val shouldShow: () -> Boolean,
        val serializer: (T, MutableMap<*, *>) -> JsonElement,
        val deserializer: (JsonElement, T, MutableMap<*, *>) -> T?,
        val stringSerializer: (T, MutableMap<*, *>) -> String?,
        val stringDeserializer: (String, T, MutableMap<*, *>) -> T?,
        val possibleValuesGenerator: (MutableMap<*, *>) -> List<String>
    ) {

        fun save() {
            ConfigManager.saveWithId(ConfigManager.currentConfigId)
        }

        val displayValue: String
            get() = displayValueAdapter(value, extra)

        val possibleValues: List<String>
            get() = possibleValuesGenerator(extra)

        var stringValue: String?
            get() = stringSerializer(value, extra)
            set(value) {
                if (value != null) {
                    val a = stringDeserializer(value, this.value, extra)
                    if (a != null) {
                        this.value = a
                        save()
                    }
                }
            }

        operator fun setValue(ref: BaseModule, prop: KProperty<*>, value: T) {
            this.value = setter(this.value, value, extra)
            save()
        }

        operator fun getValue(ref: BaseModule, prop: KProperty<*>): T {
            return getter(value, extra)
        }

        var serialized: JsonElement
            get() = serializer(value, extra)
            set(value) {
                val des = deserializer(value, this.value, extra)
                if (des != null) {
                    this.value = des
                    save()
                }
            }

        init {
            settings += this
        }
    }

    /**
     * The provider of a setting.
     *
     * @param displayName The display name of the setting.
     * @param defaultValue The default value of the setting.
     * @param extra Extra information of the setting that can be saved.
     * @param displayValueAdapter The adapter that converts the value to a display value.
     * @param renderer The renderer of the setting.
     * @param shouldShow Compute whether the setting should be shown. See [eq] [neq] [isIn] [notIn] [lt] [gt] [leq] [geq].
     * @param init The initializer of the setting to configure a custom [set]ter or [get]ter.
     */
    inner class SettingProvider<T> internal constructor(
        private val displayName: String,
        private val defaultValue: T,
        private val extra: MutableMap<*, *>,
        private val displayValueAdapter: (T, MutableMap<*, *>) -> String,
        private val renderer: String = "default",
        private val shouldShow: () -> Boolean,
        private val serializer: (value: T, extra: MutableMap<*, *>) -> JsonElement,
        private val deserializer: (json: JsonElement, default: T, extra: MutableMap<*, *>) -> T?,
        private val stringSerializer: (T, MutableMap<*, *>) -> String?,
        private val stringDeserializer: (String, T, MutableMap<*, *>) -> T?,
        private val possibleValuesGenerator: (MutableMap<*, *>) -> List<String>,
        init: SettingProvider<T>.() -> Unit
    ) {

        private var setter: (T, T, MutableMap<*, *>) -> T = { _, it, _ -> it }
        private var getter: (T, MutableMap<*, *>) -> T = { it, _ -> it }

        /**
         * A setter proxy the value goes through
         *
         * @param setter (old, new, extraData) -> valueToSet
         * */
        fun set(setter: (old: T, new: T, extra: MutableMap<*, *>) -> T) {
            this.setter = setter
        }

        /**
         * A getter proxy the value goes through
         *
         * @param getter (value, extraData) -> returnedValue to valueToSet
         * */
        fun get(getter: (value: T, extra: MutableMap<*, *>) -> T) {
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
            shouldShow,
            serializer,
            deserializer,
            stringSerializer,
            stringDeserializer,
            possibleValuesGenerator
        )

        init {
            init()
        }
    }

    /**
     * A custom setting. Could be used when defining an extension function.
     *
     * @param displayName The display name of the setting.
     * @param defaultValue The default value of the setting.
     * @param extra Extra information of the setting that doesn't get saved.
     * @param displayValueAdapter The adapter that converts the value to a display value.
     * @param renderer The renderer of the setting.
     * @param shouldShow Compute whether the setting should be shown. See [eq] [neq] [isIn] [notIn] [lt] [gt] [leq] [geq].
     * @param init The initializer of the setting to configure a custom [set]ter or [get]ter.
     */
    fun <T> customSetting(
        displayName: String,
        defaultValue: T,
        extra: MutableMap<*, *> = mutableMapOf<Any?, Any?>(),
        displayValueAdapter: (value: T, extra: MutableMap<*, *>) -> String = { it, _ -> it.toString() },
        renderer: String = "default",
        shouldShow: () -> Boolean = { true },
        serializer: (value: T, extra: MutableMap<*, *>) -> JsonElement,
        deserializer: (json: JsonElement, default: T, extra: MutableMap<*, *>) -> T?,
        stringSerializer: (T, MutableMap<*, *>) -> String? = { _, _ -> null },
        stringDeserializer: (String, T, MutableMap<*, *>) -> T? = { _, _, _ -> null },
        possibleValuesGenerator: (extra: MutableMap<*, *>) -> List<String> = { emptyList() },
        init: SettingProvider<T>.() -> Unit = {}
    ) = SettingProvider(displayName, defaultValue, extra, displayValueAdapter, renderer, shouldShow, serializer, deserializer, stringSerializer, stringDeserializer, possibleValuesGenerator, init)

    /**
     * Adds a button setting.
     *
     * @param displayName The display name of the setting.
     * @param shouldShow Compute whether the setting should be shown. See [eq] [neq] [isIn] [notIn] [lt] [gt] [leq] [geq].
     * @param action The action to perform when the button is clicked.
     */
    fun button(displayName: String, shouldShow: () -> Boolean = { true }, action: () -> Unit) =
        customSetting(
            displayName,
            action,
            displayValueAdapter = { _, _, -> "" },
            renderer = "button",
            shouldShow = shouldShow,
            serializer = { _, _ -> JsonNull },
            deserializer = { _, it, _ -> it }) {
            set { old, _, _ -> old(); old }
        }

    /**
     * Adds a toggle setting.
     *
     * @param displayName The display name of the setting.
     * @param defaultValue The default value of the setting.
     * @param shouldShow Compute whether the setting should be shown. See [eq] [neq] [isIn] [notIn] [lt] [gt] [leq] [geq].
     * @param block The initializer of the setting to configure a custom [set]ter or [get]ter.
     * */
    fun switch(displayName: String, defaultValue: Boolean, shouldShow: () -> Boolean = { true }, block: SettingProvider<Boolean>.() -> Unit = {}) =
        customSetting(
            displayName,
            defaultValue,
            renderer = "switch",
            shouldShow = shouldShow,
            serializer = { it, _ -> JsonPrimitive(it) },
            deserializer = { it, _, _ -> it.jsonPrimitive.booleanOrNull },
            stringSerializer = { it, _ -> it.toString() },
            stringDeserializer = { it, _, _ -> it.toBooleanStrictOrNull() },
            possibleValuesGenerator = { listOf("true", "false") },
            init = block
        )

    /**
     * Adds a slider setting.
     *
     * @param displayName The display name of the setting.
     * @param defaultValue The default value of the setting.
     * @param min The minimum value of the slider.
     * @param step The step of the slider.
     * @param max The maximum value of the slider.
     * @param shouldShow Compute whether the setting should be shown. See [eq] [neq] [isIn] [notIn] [lt] [gt] [leq] [geq].
     * @param block The initializer of the setting to configure a custom [set]ter or [get]ter.
     * */
    fun <T: Number> range(displayName: String, defaultValue: T, min: T, step: T, max: T, shouldShow: () -> Boolean = { true }, block: SettingProvider<T>.() -> Unit = {}) =
        customSetting(
            displayName,
            defaultValue,
            mutableMapOf("min" to min, "step" to step, "max" to max),
            renderer = "range",
            shouldShow = shouldShow,
            serializer = { it, _ -> JsonPrimitive(it) },
            deserializer = { it, v, _ -> when (v::class) {
                Float::class -> it.jsonPrimitive.floatOrNull
                Double::class -> it.jsonPrimitive.doubleOrNull
                Int::class -> it.jsonPrimitive.intOrNull
                Long::class -> it.jsonPrimitive.longOrNull
                else -> error("Unknown Number type")
            } as T? },
            stringSerializer = { it, _ -> it.toString() },
            stringDeserializer = { it, v, _ -> when (v::class) {
                Float::class -> it.toFloatOrNull()
                Double::class -> it.toDoubleOrNull()
                Int::class -> it.toIntOrNull()
                Long::class -> it.toLongOrNull()
                else -> error("Unknown Number type")
            } as T? },
            init = block
        )

    /**
     * Adds a color picker setting.
     *
     * @param displayName The display name of the setting.
     * @param defaultValue The default value of the setting.
     * @param alpha Whether the color picker should have an alpha value.
     * @param shouldShow Compute whether the setting should be shown. See [eq] [neq] [isIn] [notIn] [lt] [gt] [leq] [geq].
     * @param block The initializer of the setting to configure a custom [set]ter or [get]ter.
     * */
    fun colorPicker(displayName: String, defaultValue: Color, alpha: Boolean = true, shouldShow: () -> Boolean = { true }, block: SettingProvider<Color>.() -> Unit = {}) =
        customSetting(
            displayName,
            defaultValue,
            mutableMapOf("alpha" to alpha),
            { it, _ -> "#${it.rgb.toString(16).padStart(if (alpha) 8 else 6, '0')}" },
            "colorPicker",
            shouldShow,
            { it, _ -> JsonPrimitive(it.rgb.toString(16)) },
            { it, _, _ -> Color(it.jsonPrimitive.content.toInt(16), alpha) },
            { it, _ -> it.rgb.toString(16) },
            { it, _, _ ->
                val num = it.toIntOrNull(16)
                if (num == null) {
                    null
                } else {
                    Color(num, alpha)
                }
            },
            init = block)

    /**
     * Adds a dropdown setting.
     *
     * @param displayName The display name of the setting.
     * @param defaultValue The default value of the setting.
     * @param available The available values of the setting.
     * @param shouldShow Compute whether the setting should be shown. See [eq] [neq] [isIn] [notIn] [lt] [gt] [leq] [geq].
     * @param block The initializer of the setting to configure a custom [set]ter or [get]ter.
     * */
    fun dropDownMenu(displayName: String, defaultValue: String, available: MutableList<String>, shouldShow: () -> Boolean = { true }, block: SettingProvider<String>.() -> Unit = {}) =
        customSetting(
            displayName,
            defaultValue,
            mutableMapOf("available" to available),
            renderer = "dropDownMenu",
            shouldShow = shouldShow,
            serializer = { it, extra ->
                JsonObject(
                    mapOf("value" to JsonPrimitive(it), "available" to JsonArray((extra["available"] as List<String>).map { i -> JsonPrimitive(i) }))
                ) },
            deserializer = { it, _, extra ->
                extra as MutableMap<Any?, Any?>
                extra["available"] = it.jsonObject["available"]!!.jsonArray.map { it.jsonPrimitive.content }.toMutableList()
                it.jsonObject["value"]!!.jsonPrimitive.content
            },
            stringSerializer = { it, _ -> it },
            stringDeserializer = { it, _, extra -> if (it in (extra["available"] as List<*>)) it else null },
            possibleValuesGenerator = { it["available"] as List<String> },
            init = block
        )

    /**
     * Adds a dropdown setting using enums.
     *
     * @param displayName The display name of the setting.
     * @param defaultValue The default value of the setting.
     * @param displayValueAdapter The function to convert the enum value to a display value.
     * @param shouldShow Compute whether the setting should be shown. See [eq] [neq] [isIn] [notIn] [lt] [gt] [leq] [geq].
     * @param block The initializer of the setting to configure a custom [set]ter or [get]ter.
     * */
    fun <E: Enum<E>> dropDownMenu(displayName: String, defaultValue: E, displayValueAdapter: (E, MutableMap<*, *>) -> String = { it, _ -> it.name }, shouldShow: () -> Boolean = { true }, block: SettingProvider<E>.() -> Unit = {}) =
        customSetting(
            displayName,
            defaultValue,
            displayValueAdapter = displayValueAdapter,
            renderer = "dropDownMenu",
            shouldShow = shouldShow,
            serializer = { it, _ -> JsonPrimitive(it.name) },
            deserializer = { it, old, _ -> old::class.java.enumConstants.first { i -> i.name == it.jsonPrimitive.content } },
            stringSerializer = { it, _ -> it.name },
            stringDeserializer = { it, old, _ -> old::class.java.enumConstants.firstOrNull { i -> i.name == it } },
            possibleValuesGenerator = { defaultValue::class.java.enumConstants.map { i -> i.name } },
            init = block
        )

    /**
     * Adds a list setting.
     *
     * @param displayName The display name of the setting.
     * @param defaultValue The default value of the setting.
     * @param shouldShow Compute whether the setting should be shown. See [eq] [neq] [isIn] [notIn] [lt] [gt] [leq] [geq].
     * @param block The initializer of the setting to configure a custom [set]ter or [get]ter.
     * */
    fun list(displayName: String, defaultValue: List<String> = emptyList(), shouldShow: () -> Boolean = { true }, block: SettingProvider<List<String>>.() -> Unit = {}) =
        customSetting(
            displayName,
            defaultValue,
            displayValueAdapter = { it, _ -> it.joinToString("\n") },
            renderer = "list",
            shouldShow = shouldShow,
            serializer = { it, _ -> JsonArray(it.map { i -> JsonPrimitive(i) }) },
            deserializer = { it, _, _ -> it.jsonArray.map { i -> i.jsonPrimitive.content } },
            stringSerializer = { it, _ -> it.joinToString("\n") },
            init = block
        )

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
        Category.modules += this
    }
}

/**
 * A module that can be bound to a key.
 * */
interface BoundModule {
    var key: Int
}

/**
 * A module that can be triggered by a key.
 *
 * @param displayName The display name of the module.
 * @param description The description of the module.
 * @param category The category of the module.
 * @param key The key that triggers the module.
 * */
abstract class TriggerModule(
    displayName: String,
    description: String,
    category: Category,
    override var key: Int = GLFW.GLFW_KEY_UNKNOWN
): BaseModule(displayName, description, category), BoundModule {

    fun run() {
        onRun()
        EventManager.fire(ModuleTriggerEvent(this))
    }

    abstract fun onRun()
}

/**
 * A module that can be toggled by a key.
 *
 * @param displayName The display name of the module.
 * @param description The description of the module.
 * @param category The category of the module.
 * @param isEnabled The initial state of the module.
 * @param preventEnableOnLoad If true, the module will not be enabled on load.
 * @param key The key that triggers the module.
 * */
abstract class ToggleModule(
    displayName: String,
    description: String,
    category: Category,
    var isEnabled: Boolean = false,
    val preventEnableOnLoad: Boolean = false,
    override var key: Int = GLFW.GLFW_KEY_UNKNOWN
): BaseModule(displayName, description, category), BoundModule {

    /**
     * Toggles the module.
     * */
    fun toggle(on: Boolean? = null) {
        when {
            on == true && !isEnabled -> enable()
            on == false && isEnabled -> disable()
            else -> if (isEnabled) disable() else enable()
        }
    }

    /**
     * Enables the module.
     * */
    fun enable() {
        if (!isEnabled) {
            isEnabled = true
            EventManager.fire(ModuleEnableEvent(this))
            onEnable()
        }
    }

    /**
     * Disables the module.
     * */
    fun disable() {
        if (isEnabled) {
            isEnabled = false
            EventManager.fire(ModuleDisableEvent(this))
            onDisable()
        }
    }

    open fun onEnable() {}
    open fun onDisable() {}
}
