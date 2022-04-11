package net.redstonecraft.amber.modules

import net.redstonecraft.amber.events.Event
import net.redstonecraft.amber.events.on

object TestCategory: Category("Test", "desc")

object TestModule: BaseModule("TestModule", "test", TestCategory) {

    private var a by range("a", 0, 0, 1, 10)

    var test by button("TestButton", shouldShow = ::a lt 5) {
        println("test")
    }

    init {
        on<Event> {
            println(it)
        }
    }

}
