package net.redstonecraft.amber.modules

object TestCategory: Category("Test")

object TestModule: BaseModule("TestModule", "test", TestCategory) {

    private var a by range("a", 0, 0, 1, 10)

    var test by button("TestButton", shouldShow = ::a lt 5) {}

}
