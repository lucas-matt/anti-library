package com.lucasmatt.antilibrary.image

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.lucasmatt.antilibrary.goodreads.Goodreads
import com.lucasmatt.antilibrary.utils.takeRandom
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StitchTest {

    @Test
    fun testMe() {
        val goodreads = Goodreads("61058109-matthew-lucas")
        val books = goodreads.contentsOf(Goodreads.Shelf(null, "to-read"))
        val two = books.takeRandom(20)
        val bitmaps = two.forEach { book ->
            val ctx = InstrumentationRegistry.getTargetContext()
            val bmp = book.asBitmap(ctx)
            println("${bmp?.height} x ${bmp?.width}")
            ctx.setWallpaper(bmp)
            Thread.sleep(1000)
        }

    }

}