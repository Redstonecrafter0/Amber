This is only a rough estimation of the performance difference achieved by different things implemented on the same system.
In order to improve comparability the same location on the same world is used with the same resolution, video settings and there are no entities.

It turns out that the InGameHud (F3) takes a lot of frame time so a custom FPS counter is used instead.
The test system is a R5-3500X and an Arc A750.
The mod list of `mods` is:
- capes-1.5.3+1.20.2-fabric.jar
- entityculling-fabric-1.6.4-mc1.20.4.jar
- ferritecore-6.0.3-fabric.jar
- iris-mc1.20.4-1.6.17.jar
- lazydfu-0.1.3.jar
- lithium-fabric-mc1.20.4-0.12.1.jar
- sodium-fabric-0.5.8+mc1.20.4.jar

This branch exists to figure out if performance can be improved and by how much with what effort.
The primary way to achieve this is to use indirect rendering introduced in OpenGL 4.0 and other optimizations featured by [Vercidium on YouTube](https://www.youtube.com/@Vercidium).
Another approach would be replacing OpenGL by Vulkan. This would even allow for hardware accelerated ray tracing as an alternative to shaders not being able to use the hardware.
This might be a considerable compromise when modifying vertex formats which would break shaderpacks. Let's see which one it will be.

Compatibility with other mods or shaderpacks might be broken so this code is on its own branch in its own submodule.

| Name                                                                   | FPS  | Bottleneck |
|------------------------------------------------------------------------|------|------------|
| baseline                                                               | 190  | CPU        |
| ImmediatelyFast-Fabric-1.2.11+1.20.4.jar                               | 200  | CPU        |
| mods+ImmediatelyFast-Fabric-1.2.11+1.20.4.jar                          | 800  | CPU        |
| mods                                                                   | 830  | CPU        |
| mods+ImmediatelyFast-Fabric-1.2.11+1.20.4.jar-iris-mc1.20.4-1.6.17.jar | 850  | CPU        |
| VulkanMod_1.20.4-0.4.0.jar                                             | 1400 | N/A        |

> Note: As ImmediatelyFast primarily improves portions of the render engine which are not tested in this scenario like entities,
> the performance improvement is not comparable at all. Nonetheless, I included the test results and will probably retest in a scenario with many entities.
> This might also be the opportunity to use instanced entity rendering.
