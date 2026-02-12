package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException

@Serializable
data class WeatherResp(
    val current: Current? = null
)

@Serializable
data class Current(
    @SerialName("time") val time: String? = null,
    @SerialName("temperature_2m") val temp: Double? = null
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = OkHttpClient()
        val json = Json { ignoreUnknownKeys = true }

        setContent {
            var city by remember { mutableStateOf("") }
            var temp by remember { mutableStateOf<Double?>(null) }
            var time by remember { mutableStateOf<String?>(null) }
            var loading by remember { mutableStateOf(false) }
            var err by remember { mutableStateOf<String?>(null) }

            fun fetch() {
                loading = true
                err = null
                temp = null
                time = null

                // ТЫ МОЖЕШЬ ЗАМЕНИТЬ lat/lon на то, что получил из поиска города
                val url =
                    "https://api.open-meteo.com/v1/forecast?latitude=51.125&longitude=71.5&current=temperature_2m"

                val req = Request.Builder().url(url).build()
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        loading = false
                        err = "Network error"
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string().orEmpty()
                        try {
                            val data = json.decodeFromString(WeatherResp.serializer(), body)
                            temp = data.current?.temp
                            time = data.current?.time
                            loading = false
                        } catch (_: Exception) {
                            loading = false
                            err = "Parse error"
                        }
                    }
                })
            }

            MaterialTheme {
                Column(Modifier.padding(16.dp)) {
                    Text("Weather", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City (just for demo)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))
                    Button(onClick = { fetch() }, enabled = !loading) {
                        Text(if (loading) "Loading..." else "Get Weather")
                    }

                    Spacer(Modifier.height(16.dp))

                    err?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    if (temp != null) {
                        Text("Temperature: ${"%.1f".format(temp)} °C", style = MaterialTheme.typography.titleMedium)
                        Text("Time: ${time ?: "-"}", style = MaterialTheme.typography.bodySmall)
                    } else if (!loading && err == null) {
                        Text("No data yet", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
