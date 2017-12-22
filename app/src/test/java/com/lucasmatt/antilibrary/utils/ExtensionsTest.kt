package com.lucasmatt.antilibrary.utils

import org.junit.Test
import org.junit.Assert.*

class ExtensionsTest {

    @Test
    fun listShouldRandomlyPop() {
        val list = listOf("x", "y", "z")
        val (l, ls) = list.random()
        assertTrue(list.contains(l))
        assertTrue(!ls.contains(l))
    }

    @Test(expected = IllegalArgumentException::class)
    fun takeRandomShouldFailIfTakeTooMany() {
        val list = listOf("x", "y", "z")
        list.takeRandom(17)
    }

    @Test
    fun takeRandomShouldChooseWithoutReplacement() {
        val list = listOf("x", "y", "z")
        val taken = list.takeRandom(2)
        assertEquals(2, taken.toSet().size)
    }

}