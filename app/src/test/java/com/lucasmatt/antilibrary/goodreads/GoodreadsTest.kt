package com.lucasmatt.antilibrary.goodreads


import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.Assert.*

class GoodreadsTest {

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
        assertEquals("61058109", user.id)
        assertEquals("Mr Black", user.name)
    }

    @Test
    fun shouldReturnAvailableShelves() {
        fakeResponse("""
           <GoodreadsResponse>
                <shelves start="1" end="3" total="3">
                    <user_shelf>
                        <id type="integer">199496770</id>
                        <name>read</name>
                    </user_shelf>
                    <user_shelf>
                        <id type="integer">183639273</id>
                        <name>reading</name>
                    </user_shelf>
                    <user_shelf>
                        <id type="integer">219283629</id>
                        <name>to-read</name>
                    </user_shelf>
                </shelves>
           </GoodreadsResponse>
        """)
        val goodreads = Goodreads("myuser")
        val shelves = goodreads.shelves()
        val ids = shelves.map { shelf -> shelf.id }
        val names = shelves.map { shelf -> shelf.name }
        assertEquals(listOf("199496770", "183639273", "219283629"), ids)
        assertEquals(listOf("read", "reading", "to-read"), names)
    }

    @Test
    fun shouldReturnAllBooksOnAShelf() {
        fakeResponse("""
           <GoodreadsResponse>
                <reviews start="1" end="1" total="147">
                    <review>
                        <id>2219182067</id>
                        <book>
                            <id type="integer">20828370</id>
                            <title>The Wild Truth: A Memoir</title>
                            <title_without_series>The Wild Truth: A Memoir</title_without_series>
                            <image_url>img.jpg</image_url>
                            <small_image_url>small.jpg</small_image_url>
                            <large_image_url>large.jpg</large_image_url>
                        </book>
                    </review>
                </reviews>
           </GoodreadsResponse>
        """)
        val goodreads = Goodreads("myuser")
        val books = goodreads.contentsOf(Goodreads.Shelf("1", "read"))
        val book = books.get(0)
        assertEquals(Goodreads.Book("20828370", "The Wild Truth: A Memoir", "img.jpg", "small.jpg", "large.jpg"), book)
    }

}