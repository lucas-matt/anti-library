package com.lucasmatt.antilibrary.utils

import java.util.*

fun <T> List<T>.random(): Pair<T, List<T>> {
    val idx = Random().nextInt(this.size)
    val l = this.get(idx)
    val ls = this.filterIndexed { i, t -> i != idx }
    return Pair(l, ls)
}

fun <T> List<T>.takeRandom(n: Int): List<T> {
    if (n > this.size || n < 1) {
        throw IllegalArgumentException("Can't take $n from list of size ${this.size}")
    }
    val (_, taken) = (1..n).fold(Pair<List<T>, List<T>>(this, listOf())) { (src, dst), _ ->
        val (x, xs) = src.random()
        Pair(xs, dst + listOf(x))
    }
    return taken
}