package net.redstonecraft.amber.events

import net.redstonecraft.amber.config.Config
import net.redstonecraft.amber.modules.BaseModule

/**
 * The [Event] root interface of the hierarchy.
 * */
interface Event

/**
 * The [Event]s that are cancellable implement this interface.
 * */
interface CancellableEvent: Event {
    var isCancelled: Boolean
}

class ConfigPreLoadEvent(var config: Config): CancellableEvent {
    override var isCancelled: Boolean = false
}

class ConfigPostLoadEvent(val config: Config): Event

class ConfigPreSaveEvent(var config: Config): CancellableEvent {
    override var isCancelled: Boolean = false
}

class ConfigPostSaveEvent(val config: Config): Event

class ExceptionEvent(val throwable: Throwable): Event

open class ModuleEvent(val module: BaseModule): Event
class ModuleTriggerEvent(module: BaseModule): ModuleEvent(module)
open class ModuleToggleEvent(module: BaseModule): ModuleEvent(module)
class ModuleEnableEvent(module: BaseModule): ModuleToggleEvent(module)
class ModuleDisableEvent(module: BaseModule): ModuleToggleEvent(module)

class KeyboardKeyEvent(val key: Int, val scancode: Int, val action: Int, val modifiers: Int): Event
class MousePressEvent(val button: Int, val action: Int, val mods: Int): Event

class TickEvent: Event

class WindowResizeEvent(val width: Int, val height: Int): Event
