package net.redstonecraft.amber.utils

import kotlin.math.min

object Utils {

    fun <T> paginate(list: List<T>, page: Int, perPage: Int): List<T> {
        return try {
            val start = page * perPage
            val end = start + perPage
            list.subList(start, min(end, list.size))
        } catch (_: IllegalArgumentException) {
            emptyList()
        } catch (_: IndexOutOfBoundsException) {
            emptyList()
        }
    }

}
