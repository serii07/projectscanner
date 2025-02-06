import com.example.myapplication.NetworkAPI
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class NetworkAPITest {

    @Test
    fun testPostToOpenAI() = runBlocking {
        // Arrange: Set up the NetworkAPI and payload
        val networkAPI = NetworkAPI()
        val payload = mapOf(
            "prompt" to "What is the product name for barcode  8901012185209",
            "max_tokens" to 50,
            "temperature" to 0.7,
            "model" to "gpt-4o-mini-2024-07-18"
        )

        try {
            // Act: Call the postToOpenAI method
            val response = networkAPI.postToOpenAI(payload)

            // Assert: Check if the response is valid
            assertNotNull("The response should not be null", response)
            println("OpenAI Response: $response")
        } catch (e: Exception) {
            // Fail the test if an exception occurs
            fail("API call failed: ${e.message}")
        }
    }
}
