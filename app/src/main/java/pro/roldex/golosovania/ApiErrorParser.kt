package pro.roldex.golosovania

import com.google.gson.Gson
import retrofit2.Response

object ApiErrorParser {
    fun message(response: Response<*>): String {
        val body = response.errorBody()?.string()
        if (body.isNullOrBlank()) {
            return "Request failed: ${response.code()}"
        }

        return runCatching {
            Gson().fromJson(body, ErrorResponse::class.java)?.message
        }.getOrNull().orEmpty().ifBlank {
            "Request failed: ${response.code()}"
        }
    }
}
