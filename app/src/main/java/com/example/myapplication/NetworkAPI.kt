package com.example.myapplication

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

private const val TAG = "NetworkAPI"


class NetworkAPI {

    @Throws(IOException::class)
    suspend fun fetchUrlResponseString(urlString: String): String? {
        val remoteUrl = URL(urlString)
        // Returns a URLConnection instance that represents a connection to the remote object referred to by the URL.
        val remoteConnection = remoteUrl.openConnection() as HttpURLConnection
        try {
            // Set the request method
            remoteConnection.requestMethod = "GET"
            val responseCode = remoteConnection.responseCode
            Log.d(TAG, "GET request to $urlString returned response code: $responseCode")
            // Throw an IOException if the response code is not 200
            if (responseCode != HttpURLConnection.HTTP_OK) {
                val errorResponse = remoteConnection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.e(TAG, "Error accessing $urlString. Response: $errorResponse")
                throw IOException("Error accessing $urlString with response: $errorResponse")
            }

            val response = remoteConnection.inputStream.bufferedReader().use { it.readText() }
            return response
        } finally {
            remoteConnection.disconnect()
        }
    }
}
