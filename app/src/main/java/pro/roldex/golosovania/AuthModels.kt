package pro.roldex.golosovania

data class ErrorResponse(
    val success: Boolean,
    val message: String
)

data class LoginRequest(
    val login: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class RegisterRequest(
    val name: String,
    val surname: String,
    val login: String,
    val password: String,
    val email: String,
    val username: String
)

data class RegisterResponse(
    val token: String
)

data class UserProfileResponse(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
    val login: String,
    val username: String
)

data class UpdateUserProfileRequest(
    val name: String,
    val surname: String,
    val email: String,
    val username: String
)

data class UpdateUserProfileResponse(
    val success: Boolean
)

data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

data class UpdatePasswordResponse(
    val success: Boolean
)

data class LogoutResponse(
    val success: Boolean
)
