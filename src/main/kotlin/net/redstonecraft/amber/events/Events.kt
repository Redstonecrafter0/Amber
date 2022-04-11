package net.redstonecraft.amber.events

import net.redstonecraft.amber.config.Config

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
