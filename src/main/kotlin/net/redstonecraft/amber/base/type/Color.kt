package net.redstonecraft.amber.base.type

import kotlinx.serialization.Serializable

@Serializable
data class RGBColor(
    val r: Int,
    val g: Int,
    val b: Int,
    val a: Int
)

@Serializable
data class HSLColor(
    val h: Int,
    val s: Int,
    val l: Int,
    val a: Int
)
