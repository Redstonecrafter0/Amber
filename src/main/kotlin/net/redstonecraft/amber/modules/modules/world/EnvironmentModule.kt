package net.redstonecraft.amber.modules.modules.world

import net.redstonecraft.amber.modules.BaseModule
import net.redstonecraft.amber.modules.modules.WorldCategory

object EnvironmentModule: BaseModule("Environment", "Settings of the environment.", WorldCategory) {

    var hideRain by switch("Hide rain", false)

}
