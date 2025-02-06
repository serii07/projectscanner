package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val TAG = "ResultsListView"

class ResultsListView : AppCompatActivity() {

    private val productDatabase = ProductDatabase(this)
    private val testResponseTextView: TextView by lazy { findViewById(R.id.activity_results_list_TESTBOX_textView) }
    private val testResponseTextView1: TextView by lazy { findViewById(R.id.activity_results_list_TESTBOX1_textView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_list_view)

        val barcodeNumber = intent.extras?.getString("barcode") ?: ""
        Log.d(TAG, "Received barcode: $barcodeNumber")

        val productImageView: ImageView = findViewById(R.id.productImageView)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (productName, imageUrl) = fetchProductName(barcodeNumber)
                Log.d(TAG, "Fetched product name: $productName, Image URL: $imageUrl")

                withContext(Dispatchers.Main) {
                    val cleanedProductName = cleanProductName(productName)
                    testResponseTextView1.text = "Product Name: $cleanedProductName"
                    testResponseTextView.text = "Barcode No: $barcodeNumber"

                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this@ResultsListView)
                            .load(imageUrl)
                            .into(productImageView)
                    } else {
                        Log.w(TAG, "Image URL is null or empty")
                    }

                    // Replaced Toast with Snackbar
                    val rootView = findViewById<View>(android.R.id.content)
                    Snackbar.make(rootView, "Product fetched successfully!", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching product", e)
                withContext(Dispatchers.Main) { handleApiError(e) }
            }
        }
    }

    private suspend fun fetchProductName(barcode: String): Pair<String, String?> {
        Log.d(TAG, "Sending request to Google Custom Search API for barcode: $barcode")

        val apiKey = "AIzaSyDNkEdAnqEn5gPXxA3O_xmZJsEcONlcwFY"
        val searchEngineId = "94a4ecdfb53ec46ca"
        val searchUrl = "https://www.googleapis.com/customsearch/v1?q=$barcode&key=$apiKey&cx=$searchEngineId"

        try {
            val response = NetworkAPI().fetchUrlResponseString(searchUrl)
            Log.d(TAG, "API response: $response")

            val jsonResponse = JSONObject(response)
            val items = jsonResponse.optJSONArray("items")

            if (items != null && items.length() > 0) {
                var title: String? = null
                var imageUrl: String? = null

                // Iterate through search results until a valid image is found
                for (i in 0 until items.length()) {
                    val currentItem = items.getJSONObject(i)

                    title = currentItem.optString("title", "Unknown Product")
                    imageUrl = currentItem.optJSONObject("pagemap")
                        ?.optJSONArray("cse_image")
                        ?.getJSONObject(0)
                        ?.optString("src", null)
                    // Check if the image URL is invalid
                    if (imageUrl != null && imageUrl != "https://www.bigbasket.com/transparentImg.png") {
                        break // Exit loop when a valid image is found
                    }
                }
                if (imageUrl == null) {
                    throw Exception("No valid product image found in search results.")
                }
                return Pair(title ?: "Unknown Product", imageUrl)
            } else {
                throw Exception("No product found in search results.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching product name", e)
            throw e
        }
    }

    private fun cleanProductName(rawProductName: String): String {
        // Trim leading/trailing spaces
        var cleanedName = rawProductName.trim()

        // Convert the name to lowercase and split it into words, then capitalize each word
        cleanedName = cleanedName.split(" ")
            .joinToString(" ") { word -> word.capitalize() }

        // Removing any unnecessary single words like "buy" or "online" if found
        cleanedName = cleanedName.split(" ").filter { it.lowercase() != "buy" && it.lowercase() != "online" && it.lowercase() != "price" && it.lowercase() != "at" && it.lowercase() != "best" && it.lowercase() != ":" && it.lowercase() != "|" }
            .joinToString(" ")

        // Remove any unwanted phrases
        cleanedName = cleanedName.replace("...", "").trim()
        cleanedName = cleanedName.replace("At Best ...", "").trim()
        cleanedName = cleanedName.replace("At Best Price ...", "").trim()
        cleanedName = cleanedName.replace("Aap Ka Bazar", "").trim()
        cleanedName = cleanedName.replace("Amazon.in", "").trim()

        // Normalize multiple spaces to a single space
        cleanedName = cleanedName.replace("\\s+".toRegex(), " ")

        return cleanedName
    }

    private fun handleApiError(e: Exception) {
        when (e) {
            is retrofit2.HttpException -> {
                when (e.code()) {
                    429 -> showError("Too many requests. Please try again later.")
                    404 -> showError("Product not found.")
                    401 -> showError("Unauthorized access. Check API key.")
                    else -> showError("Server error: ${e.code()}.")
                }
            }
            is java.net.UnknownHostException -> showError("No internet connection.")
            else -> showError("An unexpected error occurred: ${e.localizedMessage}.")
        }
    }

    private fun showError(message: String) {
        Log.e(TAG, message)
        // Replaced Toast with Snackbar
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
        testResponseTextView.text = "Error: $message"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_results_listview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_archived_items -> {
                val archivedIntent = Intent(this, ArchivedProductActivity::class.java)
                startActivity(archivedIntent)
            }
        }
        return true
    }

    @ExperimentalGetImage
    fun onClickScanAgain(view: View) {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
    }

    fun onClickSave(view: View) {
        val barcodeData = testResponseTextView.text.toString()
        val productName = testResponseTextView1.text.toString()
        val imageUrl = "hmm"

        val newProductData = ArchivedProduct(
            data = barcodeData,
            productName = productName,
            productImage = imageUrl
        )

        if (barcodeData.isBlank() || productName.isBlank()) {
            // Replaced Toast with Snackbar
            val rootView = findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, "No data to save", Snackbar.LENGTH_LONG).show()
        } else {
            productDatabase.insertProduct(newProductData)
            // Replaced Toast with Snackbar
            val rootView = findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, "Entry was archived successfully!", Snackbar.LENGTH_LONG).show()
        }
    }
}
