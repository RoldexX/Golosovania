package pro.roldex.golosovania

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Volatile
    private var authApiInstance: AuthApi? = null
    @Volatile
    private var votesApiInstance: VotesApi? = null
    @Volatile
    private var notificationsApiInstance: NotificationsApi? = null

    fun authApi(context: Context): AuthApi {
        return authApiInstance ?: synchronized(this) {
            authApiInstance ?: buildAuthApi(context.applicationContext).also {
                authApiInstance = it
            }
        }
    }

    fun votesApi(context: Context): VotesApi {
        return votesApiInstance ?: synchronized(this) {
            votesApiInstance ?: buildVotesApi(context.applicationContext).also {
                votesApiInstance = it
            }
        }
    }

    fun notificationsApi(context: Context): NotificationsApi {
        return notificationsApiInstance ?: synchronized(this) {
            notificationsApiInstance ?: buildNotificationsApi(context.applicationContext).also {
                notificationsApiInstance = it
            }
        }
    }

    private fun buildAuthApi(context: Context): AuthApi {
        return buildRetrofit(context).create(AuthApi::class.java)
    }

    private fun buildVotesApi(context: Context): VotesApi {
        return buildRetrofit(context).create(VotesApi::class.java)
    }

    private fun buildNotificationsApi(context: Context): NotificationsApi {
        return buildRetrofit(context).create(NotificationsApi::class.java)
    }

    private fun buildRetrofit(context: Context): Retrofit {
        val sessionManager = SessionManager(context)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { sessionManager.getToken() })
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
