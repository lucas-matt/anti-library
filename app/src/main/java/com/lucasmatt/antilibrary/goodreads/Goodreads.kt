package com.lucasmatt.antilibrary.goodreads

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.rx.rx_object
import com.lucasmatt.antilibrary.BuildConfig
import com.squareup.picasso.Picasso
import io.reactivex.Single
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister


class Goodreads(private val user:String) {

    companion object {
        init {
            FuelManager.instance.basePath = "https://www.goodreads.com"
            FuelManager.instance.baseParams = listOf(Pair("key", BuildConfig.GOODREADS_API_KEY))
        }

        val SERIALIZER: Serializer = Persister()
    }

    @Root(strict=false, name="GoodreadsResponse")
    data class UserResponse(@field:Element var user: User) {
        constructor(): this(User())
        class Deserializer: ResponseDeserializable<UserResponse> {
            override fun deserialize(bytes: ByteArray) = SERIALIZER.read(UserResponse::class.java, bytes.inputStream())
        }
    }

    @Root(strict=false)
    data class User(@field:Element var id: String, @field:Element var name: String) {
        constructor(): this("", "")
    }

    @Root(strict=false, name="GoodreadsResponse")
    data class ShelvesResponse(@field:ElementList var shelves: List<Shelf>) {
        constructor(): this(mutableListOf())
        class Deserializer: ResponseDeserializable<ShelvesResponse> {
            override fun deserialize(bytes: ByteArray) = SERIALIZER.read(ShelvesResponse::class.java, bytes.inputStream())
        }
    }

    @Root(strict=false)
    data class Shelf(@field:Element var id: String?,
                     @field:Element var name: String) {
        constructor(): this(null, "")
    }

    @Root(strict=false, name="GoodreadsResponse")
    data class ShelfContentsResponse(@field:ElementList var reviews: List<Review>) {
        constructor(): this(mutableListOf())
        class Deserializer: ResponseDeserializable<ShelfContentsResponse> {
            override fun deserialize(bytes: ByteArray) = SERIALIZER.read(ShelfContentsResponse::class.java, bytes.inputStream())
        }
    }

    @Root(strict=false)
    data class Review(@field:Element var id: String, @field:Element() var book: Book) {
        constructor(): this("", Book())
    }

    @Root(strict=false)
    data class Book(@field:Element var id: String,
                    @field:Element var title: String,
                    @field:Element(name="image_url", required=false) var imageUrl: String?,
                    @field:Element(name="small_image_url", required=false) var smallImageUrl: String?,
                    @field:Element(name="large_image_url", required=false) var largeImageUrl: String?) {

        constructor() : this("", "", null, null, null)

        fun asBitmap(ctx: Context): Bitmap? {
            return listOf(largeImageUrl, imageUrl, smallImageUrl)
                    .filter { img -> !img.isNullOrBlank() }
                    .map { img ->
                        val uri = Uri.parse(img)
                        Picasso.with(ctx)
                                .load(uri)
                                .get()
                    }
                    .first()
        }

    }

    fun userInfo(): Single<User> {
        return "/user/show/$user"
                .httpGet()
                .rx_object(UserResponse.Deserializer())
                .map { it?.component1()?.user }
    }

    fun shelves(): Single<List<Shelf>> {
        return "/shelf/list.xml"
                .httpGet(listOf("user_id" to user))
                .rx_object(ShelvesResponse.Deserializer())
                .map { it?.component1()?.shelves }
    }

    fun contentsOf(shelf: Goodreads.Shelf): Single<List<Goodreads.Book>> {
        return "/review/list"
                .httpGet(listOf("id" to user, "v" to 2, "shelf" to shelf.name, "per_page" to 200))
                .rx_object(ShelfContentsResponse.Deserializer())
                .map { it?.component1()?.reviews?.map { it.book } }
    }

}