package id.xxx.module.auth.domain.model

import java.io.Serializable

sealed interface AuthenticationType : Serializable {

    enum class Mode { Login, Signup }

    val mode: Mode

    data class Password(
        val email: String,
        val password: String,
        override val mode: Mode = Mode.Login,
    ) : AuthenticationType

    data class Phone(
        val verificationId: String,
        val code: String,
        override val mode: Mode = Mode.Login,
    ) : AuthenticationType

    data class Google(
        val idToken: String,
        override val mode: Mode = Mode.Login,
    ) : AuthenticationType
}