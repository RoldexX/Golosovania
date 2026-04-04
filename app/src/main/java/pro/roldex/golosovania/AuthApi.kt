package pro.roldex.golosovania

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @GET("users/me")
    fun getCurrentUser(): Call<UserProfileResponse>

    @PATCH("users/me")
    fun updateCurrentUser(
        @Body request: UpdateUserProfileRequest
    ): Call<UpdateUserProfileResponse>

    @PATCH("users/me/password")
    fun updatePassword(
        @Body request: UpdatePasswordRequest
    ): Call<UpdatePasswordResponse>

    @POST("logout")
    fun logout(): Call<LogoutResponse>
}
