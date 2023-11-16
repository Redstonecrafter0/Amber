package net.redstonecraft.amber.base.event

import net.redstonecraft.amber.base.config.type.AmberConfigSetting
import net.redstonecraft.amber.base.module.AmberModule

interface IAmberEvent

interface IAmberCancellableEvent : IAmberEvent {
    var isCancelled: Boolean
}

class KeyPressEvent(val key: Int, val scancode: Int, val action: Int, val modifiers: Int): IAmberCancellableEvent {
    override var isCancelled = false
}

class TickEvent: IAmberEvent

class FrameEvent: IAmberEvent

class ModuleEvent(val module: AmberModule, val action: Action): IAmberCancellableEvent {
    override var isCancelled = false
    enum class Action {
        ENABLED, DISABLED, TRIGGERED
    }
}

class ConfigSettingChangeEvent(val module: AmberModule, val setting: AmberConfigSetting<*>): IAmberEvent
