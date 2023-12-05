package com.xd.mvvm.testrecorder.net

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.xd.mvvm.testrecorder.data.D
import com.xd.mvvm.testrecorder.data.Err
import com.xd.mvvm.testrecorder.data.Success
import com.xd.mvvm.testrecorder.logger.Logger
import com.xd.mvvm.testrecorder.sharedpref.BooleanSharedPreferenceLiveData
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

enum class HttpMethod {
    GET, POST, PUT, DELETE // You can add more methods as needed
}

class HttpService @Inject constructor(
    private val client: OkHttpClient,
    private val moshi: Moshi,
    private val host: String,
    private val path: String = "",
    private val simulateNetworkError: BooleanSharedPreferenceLiveData,
    private val simulateNetworkLatency: BooleanSharedPreferenceLiveData,
    private val scheme: String = "https"
) {
    private val logger = Logger("HttpService")

    suspend fun <T> fetchData(
        endpoint: String,
        clazz: Class<T>,
        parameters: Map<String, Any> = emptyMap(),
        method: HttpMethod = HttpMethod.GET
    ): D<T> {
        val timestamp = System.currentTimeMillis()
        val urlBuilder = okhttp3.HttpUrl.Builder()
            .scheme(scheme) // Assuming HTTPS, change as needed
            .host(host)
            .addPathSegments(path)
            .addPathSegments(endpoint)

        if (simulateNetworkError.value == true) {
            urlBuilder.host("x.com")
        }

        if (simulateNetworkLatency.value == true) {
            delay(3000)
        }

        // Add parameters to the URL
        parameters.forEach { (key, value) ->
            urlBuilder.addQueryParameter(key, value.toString())
        }

        val requestBuilder = Request.Builder()
            .url(urlBuilder.build())

        // Handle the HTTP method
        when (method) {
            HttpMethod.GET -> {} // Default is GET, so do nothing
            HttpMethod.POST -> requestBuilder.post(okhttp3.RequestBody.create(null, byteArrayOf()))
            HttpMethod.PUT -> requestBuilder.put(okhttp3.RequestBody.create(null, byteArrayOf()))
            HttpMethod.DELETE -> requestBuilder.delete()
            // Handle other methods as needed
        }
        return try {
            val response = client.newCall(requestBuilder.build()).execute()
            if (response.isSuccessful) {
                response.body?.string()?.let { responseBody ->
                    val data: T = moshi.adapter(clazz).fromJson(responseBody)!!
                    logger.i("http request success $host$path$endpoint ${System.currentTimeMillis() - timestamp}")
                    Success(data)
                } ?: run {
                    logger.i("http request Empty response body! $host$path$endpoint ${System.currentTimeMillis() - timestamp}")
                    Err("Empty response body!")
                }
            } else {
                logger.i("http request error $host$path$endpoint ${System.currentTimeMillis() - timestamp} HTTP error: ${response.code}")
                Err("HTTP error: ${response.code}")
            }
        } catch (e: IOException) {
            logger.i("http request error $host$path$endpoint ${System.currentTimeMillis() - timestamp} IOException: ${e.message}")
            Err("Network error: ${e.message}")
        } catch (e: JsonDataException) {
            logger.i("http request error $host$path$endpoint ${System.currentTimeMillis() - timestamp} JsonDataException: ${e.message}")
            Err("Serialization error: ${e.message}")
        }
    }

}
