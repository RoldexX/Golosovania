package pro.roldex.golosovania

data class LoginRequest(
    val login: String,
    val password: String
)

data class RegisterRequest(
    val login: String,
    val password: String,
    val email: String,
    val username: String,
    val name: String,
    val surname: String
)

data class AuthResponse(
    val token: String
)