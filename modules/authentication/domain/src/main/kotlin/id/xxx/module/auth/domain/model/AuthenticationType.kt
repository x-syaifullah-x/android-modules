package id.xxx.module.auth.domain.model

import java.io.Serializable

sealed interface AuthenticationType : Serializable {

    enum class Flag { SIGN_IN, SIGN_UP }

    val flag: Flag

    data class SignInPassword(
        val email: String,
        val password: String,
    ) : AuthenticationType {
        override val flag = Flag.SIGN_IN
    }

    data class SignUpPassword(
        val email: String,
        val password: String,
    ) : AuthenticationType {
        override val flag = Flag.SIGN_UP
    }

    data class SignInPhone(
        val verificationId: String,
        val code: String,
    ) : AuthenticationType {
        override val flag = Flag.SIGN_IN
    }

    data class SignUpPhone(
        val verificationId: String,
        val code: String,
    ) : AuthenticationType {
        override val flag = Flag.SIGN_UP
    }

    data class SignUpGoogle(
        val idToken: String,
    ) : AuthenticationType {
        override val flag = Flag.SIGN_UP
    }

    data class SignInGoogle(
        val idToken: String,
    ) : AuthenticationType {
        override val flag = Flag.SIGN_IN
    }
}