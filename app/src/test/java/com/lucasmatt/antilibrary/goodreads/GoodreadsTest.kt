package com.lucasmatt.antilibrary.goodreads


import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class GoodreadsTest {

//    @Test
//    fun tryme() {
//        val goodreads = Goodreads("61058109-matthew-lucas")
//        val info = goodreads.userInfo()
//        println(info)
//        val shelves = goodreads.shelves()
//        println(shelves)
//        for (shelf in shelves) {
//            val books = goodreads.contentsOf(shelf)
//            println(books)
//        }
//    }

    fun fakeResponse(xml: String) {
        val client = mockk<Client>()
        every { client.executeRequest(any()).statusCode } returns 200
        every { client.executeRequest(any()).responseMessage } returns "OK"
        every { client.executeRequest(any()).data } returns xml.toByteArray()
        every { client.executeRequest(any()).dataStream } returns xml.toByteArray().inputStream()
        FuelManager.instance.client = client
    }

    @Test
    fun shouldReturnUserInfo() {
        fakeResponse("""
            <GoodreadsResponse>
                <user>
                    <id>61058109</id>
                    <name>Mr Black</name>
                </user>
            </GoodreadsResponse>
        }""")
        val goodreads = Goodreads("myuser")
        val user = goodreads.userInfo()
    }

}