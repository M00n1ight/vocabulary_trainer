package ru.moonlight.vocabulary_trainer

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponse
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.util.ArrayList

object Translator {
    private val key = getApiKey()

    fun translate(textForTranslate: String, lang: String): String? {
        val url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=$key&text=$textForTranslate&lang=$lang"

        val translate = runBlocking {
            try {
                Fuel.get(url).awaitObjectResponse(Translate.Deserializer)
            } catch (exception: Exception) {
                when (exception) {
                    is HttpException -> {
                        Log.d("Translation", "Network exception: ${exception.message}")
                        null
                    }
                    else -> {
                        Log.d("Translation", "An exception was thrown: ${exception.message}")
                        null
                    }
                }
            }
        }?.third?.text

        return translate?.get(0)
    }

    private data class Translate(
        val code: Int,
        val lang: String,
        val text: ArrayList<String>
    ) {
        object Deserializer: ResponseDeserializable<Translate> {
            override fun deserialize(content: String): Translate? = Gson().fromJson(content, Translate::class.java)
        }
    }
}
