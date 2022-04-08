package net.redstonecraft.amber.events

interface Event

interface CancellableEvent: Event {
    var isCancelled: Boolean
}
