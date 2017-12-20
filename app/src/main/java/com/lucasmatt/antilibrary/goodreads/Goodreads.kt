package com.lucasmatt.antilibrary.goodreads

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.lucasmatt.antilibrary.BuildConfig

class Goodreads(private val user:String) {

    init {
        FuelManager.instance.basePath = "https://www.goodreads.com"
        FuelManager.instance.baseParams = listOf(Pair("key", BuildConfig.GOODREADS_API_KEY))
    }

    class UserResponse {
        val user: User? = null
    }

    class User {
        val id: String? = null
        val name: String? = null
    }

    fun userInfo(): String {
        val (_, resp, res) = "/user/show/$user".httpGet().responseString()
        return when(resp.statusCode) {
            200 -> {
                res.get()
            }
            else -> throw RuntimeException(res.get())
        }
    }

}