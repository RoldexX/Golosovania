package pro.roldex.golosovania

import com.google.gson.Gson
import retrofit2.Response

object ApiErrorParser {
    fun message(response: Response<*>, fallback: (Int) -> String = { code -> "Ошибка запроса: $code" }): String {
        val body = response.errorBody()?.string()
        if (body.isNullOrBlank()) {
            return fallback(response.code())
        }

        return runCatching {
            Gson().fromJson(body, ErrorResponse::class.java)?.message
        }.getOrNull().orEmpty().ifBlank {
            fallback(response.code())
        }
    }
}
