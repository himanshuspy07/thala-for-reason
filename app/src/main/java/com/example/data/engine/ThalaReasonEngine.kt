package com.example.data.engine

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ThalaReasonResult(
    val isThala: Boolean,
    val originalInput: String,
    val expression: String,
    val explanation: String,
    val imageUrl: String? = null,
    val trackingId: String = "seven"
)

object ThalaReasonEngine {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Lists of legendary Thala-Dhoni memes and quotes
    val thalaQuotes = listOf(
        "\"Focus on the process, not the result. The result will take care of itself.\" — Captain Cool",
        "\"I have always believed that helper acts and calmness under pressure wins cups.\" — Dhoni",
        "\"I live in the present. I don't look back or too far ahead.\" — MSD",
        "\"If you have 15 runs to defend in the last over, and Thala is behind the stumps, you have 15 runs of hope.\"",
        "\"Jersey #7 isn't just a number; it is an emotion, a brand, and a reason for existence.\""
    )

    val thalaMemesList = listOf(
        Pair("Helicopter Shot", "MS Dhoni's signature Helicopter Shot has exactly 14 letters. 14 divided by 2 (Dhoni and the ball) = 7! THALA FOR A REASON! \uD83D\uDE81"),
        Pair("World Cup Six", "The iconic match-winning maximum in the 2011 World Cup was hit on April 2nd. 4 + 2 = 6, plus 1 (Dhoni himself) = 7! THALA FOR A REASON! \uD83C\uDFC6"),
        Pair("CSK 5 Trophies", "Dhoni won 5 IPL trophies + 2 Champion League Trophies = 7 Trophies total! THALA FOR A REASON! \uD83E\uDD81"),
        Pair("Bole Jo Koyal", "The song 'Bole Jo Koyal Baago Mein' has 4 words in the title. Dhoni has 3 syllables (Dho-ni-ji). 4 + 3 = 7! THALA FOR A REASON! \uD83D\uDC26"),
        Pair("Stumpings", "MSD once completed a stumping in 0.08 seconds. 8 minus 1 (Dhoni is #1) = 7! THALA FOR A REASON! \u26A1"),
        Pair("Dhoni Name", "The name 'M S DHONI' has 7 characters (ignoring spaces: M-S-D-H-O-N-I). THALA FOR A REASON! \u2764\uFE0F")
    )

    /**
     * Tries to find a localized funny cricket/Thala reason for the input.
     * If no direct match is found, it uses funny mathematical operators to make it equal 7!
     */
    fun getLocalReason(input: String): ThalaReasonResult {
        val trimmed = input.trim().replace("\\s+".toRegex(), "")
        if (trimmed.isEmpty()) {
            return ThalaReasonResult(
                isThala = false,
                originalInput = "",
                expression = "0",
                explanation = "Enter a number, equation or name to detect!"
            )
        }

        // 1. Check if name/word character count is 7
        val lettersOnly = trimmed.filter { it.isLetter() }
        if (lettersOnly.length == 7) {
            val letterFormula = lettersOnly.map { it.uppercaseChar() }.joinToString(" + ") { "1($it)" }
            return ThalaReasonResult(
                isThala = true,
                originalInput = input,
                expression = "$letterFormula = 7 Letters!",
                explanation = "The word '${input.uppercase()}' has exactly 7 letters!\nM-S-D-H-O-N-I has 7 letters!\nTHALA FOR A REASON! \uD83D\uDC26",
                trackingId = "bole_jo_koyal"
            )
        }

        // Check if "dhoni", "msd", "thala" are typed
        val lowercaseInput = input.lowercase()
        if (lowercaseInput.contains("dhoni") || lowercaseInput.contains("thala") || lowercaseInput.contains("msd")) {
            return ThalaReasonResult(
                isThala = true,
                originalInput = input,
                expression = "MS Dhoni = Legendary Captain",
                explanation = "'$input' contains the legend himself!\nJersey #7 is proud of you.\nTHALA FOR A REASON! \uD83C\uDFCF",
                trackingId = "seven_jersey"
            )
        }

        // 2. Try to parse as double
        val numericValue = trimmed.toDoubleOrNull()
        if (numericValue != null) {
            val intVal = numericValue.toInt()
            
            // Check if exactly 7
            if (intVal == 7) {
                return ThalaReasonResult(
                    isThala = true,
                    originalInput = input,
                    expression = "7 = 7",
                    explanation = "Direct Jersey #7 connection! You hit the sweet spot of the bat.\nTHALA FOR A REASON! \uD83E\uDD81",
                    trackingId = "seven_jersey"
                )
            }

            // Check if digits add up to 7 (e.g. 16, 25, 34, 43, 52, 61, 70, 115)
            val digitsSum = digitsSum(intVal)
            if (digitsSum == 7) {
                val digitSteps = intVal.toString().filter { it.isDigit() }.map { it.toString() }.joinToString(" + ")
                return ThalaReasonResult(
                    isThala = true,
                    originalInput = input,
                    expression = "$digitSteps = 7",
                    explanation = "The digits of $intVal add up to 7!\n$digitSteps = 7!\nTHALA FOR A REASON! \uD83C\uDFC6",
                    trackingId = "seven_jersey"
                )
            }

            // Check if contains digit 7
            if (intVal.toString().contains('7')) {
                return ThalaReasonResult(
                    isThala = true,
                    originalInput = input,
                    expression = "$intVal \u2192 Contains '7'",
                    explanation = "Your lucky number contains the magical digit 7!\nMSD's spirits are pleased.\nTHALA FOR A REASON! \u26A1",
                    trackingId = "helicopter_shot"
                )
            }

            // Forced funny equations if it doesn't add up!
            return getForcedMathReason(intVal)
        }

        // Fallback for random custom text lengths
        val textLen = lettersOnly.length
        if (textLen > 0) {
            val diff = 7 - textLen
            if (diff > 0) {
                return ThalaReasonResult(
                    isThala = true,
                    originalInput = input,
                    expression = "$textLen letters + $diff (Dhoni's aura) = 7",
                    explanation = "'$input' has $textLen letters. Add $diff for Dhoni's magical backup presence, and you get 7!\nTHALA FOR A REASON! \uD83D\uDC26",
                    trackingId = "bole_jo_koyal"
                )
            } else {
                val rem = textLen - 7
                return ThalaReasonResult(
                    isThala = true,
                    originalInput = input,
                    expression = "$textLen letters - $rem (Thala's opponents dismissed) = 7",
                    explanation = "'$input' has $textLen letters. Dismiss the $rem non-believers, and you get exactly 7!\nTHALA FOR A REASON! \uD83E\uDD81",
                    trackingId = "bole_jo_koyal"
                )
            }
        }

        return ThalaReasonResult(
            isThala = false,
            originalInput = input,
            expression = "7",
            explanation = "Can we connect this to Thala? Ask AI!"
        )
    }

    private fun digitsSum(value: Int): Int {
        var num = Math.abs(value)
        var sum = 0
        while (num > 0) {
            sum += num % 10
            num /= 10
        }
        return sum
    }

    private fun getForcedMathReason(num: Int): ThalaReasonResult {
        val absNum = Math.abs(num)
        
        if (absNum % 7 == 0) {
            val factor = absNum / 7
            return ThalaReasonResult(
                isThala = true,
                originalInput = num.toString(),
                expression = "$absNum / $factor = 7",
                explanation = "$absNum is a multiple of 7 ($factor * 7).\nTherefore, Dhoni approves!\nTHALA FOR A REASON! \uD83C\uDFCF",
                trackingId = "helicopter_shot"
            )
        }

        // Try single digit addition
        val dSum = digitsSum(absNum)
        val diff = 7 - dSum
        if (diff > 0) {
            val digitText = absNum.toString().map { it.toString() }.joinToString(" + ")
            return ThalaReasonResult(
                isThala = true,
                originalInput = num.toString(),
                expression = "($digitText) + $diff = 7",
                explanation = "Sum of digits of $absNum is $dSum. Add $diff (Dhoni's ICC Trophies and Champion Titles) to make it 7!\nTHALA FOR A REASON! \u2764\uFE0F",
                trackingId = "seven_jersey"
            )
        } else {
            val excess = dSum - 7
            val digitText = absNum.toString().map { it.toString() }.joinToString(" + ")
            return ThalaReasonResult(
                isThala = true,
                originalInput = num.toString(),
                expression = "($digitText) - $excess = 7",
                explanation = "Sum of digits of $absNum is $dSum. Subtract $excess (reasons why Dhoni is better than anyone else) to get 7!\nTHALA FOR A REASON! \uD83E\uDD81",
                trackingId = "helicopter_shot"
            )
        }
    }

    /**
     * Connects to Gemini API to find a hilarious reason why the input equals 7.
     */
    suspend fun getGeminiReason(input: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API Key is missing or default placeholder! Configure your real key in the Secrets panel on Google AI Studio to unlock true Dhoni wisdom."
        }

        val prompt = """
            You are a hilarious, die-hard MS Dhoni cricket meme engine. 
            The user entered the text: "$input".
            Write a funny, witty, and extremely creative explanation showing how this input mathematically or conceptually adds up or relates to jersey number 7 (MS Dhoni) and must end with the iconic phrase "THALA FOR A REASON!" in all caps.
            Use letters count, random historical cricket facts, or funny additions. Keep it short (maximum 3-4 sentences), highly funny, and optimized for viral cricket meme culture in India.
        """.trimIndent()

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        
        val payload = JSONObject().apply {
            put("contents", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body?.string() ?: "")
                val candidates = jsonResponse.getJSONArray("candidates")
                val content = candidates.getJSONObject(0).getJSONObject("content")
                val parts = content.getJSONArray("parts")
                parts.getJSONObject(0).getString("text").trim()
            } else {
                Log.e("ThalaReasonEngine", "Gemini API error status: ${response.code}")
                // Fallback local reason
                getLocalReason(input).explanation
            }
        } catch (e: Exception) {
            Log.e("ThalaReasonEngine", "Network fail during Gemini request", e)
            getLocalReason(input).explanation
        }
    }
}
