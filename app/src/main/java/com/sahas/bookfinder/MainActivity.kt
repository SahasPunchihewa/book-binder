package com.sahas.bookfinder

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var tv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv = findViewById(R.id.tv)
        // collecting all the JSON string
        var stb = StringBuilder()
        val url_string = "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=15"
        val url = URL(url_string)
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        runBlocking {
            launch {
                // run the code of the coroutine in a new thread
                withContext(Dispatchers.IO) {
                    var bf = BufferedReader(InputStreamReader(con.inputStream))
                    var line: String? = bf.readLine()
                    while (line != null) {
                        stb.append(line + "\n")
                        line = bf.readLine()
                    }
                    parseJSON(stb)
                }
            }
        }
    }

    suspend fun parseJSON(stb: java.lang.StringBuilder) {
        // this contains the full JSON returned by the Web Service
        val json = JSONObject(stb.toString())
        // Information about all the books extracted by this function
        var allBooks = java.lang.StringBuilder()
        var jsonArray: JSONArray = json.getJSONArray("items")
        // extract all the books from the JSON array
        for (i in 0..jsonArray.length() - 1) {
            val book: JSONObject = jsonArray[i] as JSONObject
            // this is a json object
            // extract the title
            val volInfo = book["volumeInfo"] as JSONObject
            val title = volInfo["title"] as String
            allBooks.append("${i + 1}) \"$title\" ")
            // extract all the authors
            val authors = volInfo["authors"] as JSONArray
            allBooks.append("authors: ")
            for (i in 0..authors.length() - 1)
                allBooks.append(authors[i] as String + ", ")
            allBooks.append("\n\n")
        }
        tv.setText(allBooks)
    }
}