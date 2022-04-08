package net.redstonecraft.amber.modules

object TestCategory: Category("Test", "test")

object TestModule: BaseModule("TestModule", "test", TestCategory) {

    var test by button("TestButton") {}

}
