package com.lucasmatt.antilibrary.goodreads

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.lucasmatt.antilibrary.BuildConfig

class Goodreads(private val user:String) {

    companion object {
        init {
            FuelManager.instance.basePath = "https://www.goodreads.com"
            FuelManager.instance.baseParams = listOf(Pair("key", BuildConfig.GOODREADS_API_KEY))
        }
        val MAPPER = {
            val mapper = XmlMapper()
            mapper.registerModule(KotlinModule())
            mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            mapper
        }()
    }

    data class UserResponse(val user: User) {
        class Deserializer: ResponseDeserializable<UserResponse> {
            override fun deserialize(bytes: ByteArray) = MAPPER.readValue(bytes, UserResponse::class.java)
        }
    }

    data class User(val id: String, val name: String)

    data class ShelvesResponse(val shelves: List<Shelf>) {
        class Deserializer: ResponseDeserializable<ShelvesResponse> {
            override fun deserialize(bytes: ByteArray) = MAPPER.readValue(bytes, ShelvesResponse::class.java)
        }
    }

    data class Shelf(val id: String?,
                     val name: String)

    data class ShelfContentsResponse(val reviews: List<Review>) {
        class Deserializer: ResponseDeserializable<ShelfContentsResponse> {
            override fun deserialize(bytes: ByteArray) = MAPPER.readValue(bytes, ShelfContentsResponse::class.java)
        }
    }

    data class Review(val id: String, val book: Book)

    data class Book(val id: String,
                    val title: String,
                    @JsonProperty("image_url") val imageUrl: String?,
                    @JsonProperty("small_image_url") val smallImageUrl: String?,
                    @JsonProperty("large_image_url") val largeImageUrl: String?)

    fun userInfo(): User {
        val (_, resp, res) = "/user/show/$user".httpGet().responseObject(UserResponse.Deserializer())
        return when(resp.statusCode) {
            200 -> {
                res.get().user
            }
            else -> throw RuntimeException(resp.responseMessage)
        }
    }

    fun shelves(): List<Shelf> {
        val (_, resp, res) = "/shelf/list.xml"
                .httpGet(listOf("user_id" to user))
                .responseObject(ShelvesResponse.Deserializer())
        return when(resp.statusCode) {
            200 -> {
                res.get().shelves
            }
            else -> throw RuntimeException(resp.responseMessage)
        }
    }

    fun contentsOf(shelf:Shelf): List<Book> {
        val (_, resp, res) = "/review/list"
                .httpGet(listOf("id" to user, "v" to 2, "shelf" to shelf.name))
                .responseObject(ShelfContentsResponse.Deserializer())
        return when(resp.statusCode) {
            200 -> {
                res.get().reviews.map { review -> review.book }
            }
            else -> throw RuntimeException(resp.responseMessage)
        }
    }

}