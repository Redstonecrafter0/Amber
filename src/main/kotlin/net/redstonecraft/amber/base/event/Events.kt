package net.redstonecraft.amber.base.event

interface IAmberEvent

interface IAmberCancellableEvent : IAmberEvent {

    var isCancelled: Boolean

}
