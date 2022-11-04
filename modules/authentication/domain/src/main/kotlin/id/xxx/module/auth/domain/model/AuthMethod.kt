package id.xxx.module.auth.domain.model

sealed interface AuthMethod {
    enum class Mode { IN, UP }

    val mode: Mode

    data class Password(
        override val mode: Mode,
        val email: String,
        val password: String
    ) : AuthMethod

    data class Phone(
        override val mode: Mode,
        val verificationId: String,
        val code: String
    ) : AuthMethod

    data class Google(
        override val mode: Mode,
        val idToken: String,
    ) : AuthMethod
}