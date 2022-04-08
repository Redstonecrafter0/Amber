package net.redstonecraft.amber.events

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
