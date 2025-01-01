package id.xxx.module.auth.domain.model

import java.io.Serializable

sealed interface AuthenticationType : Serializable {

    sealed interface Password : AuthenticationType {
        val email: String
        val password: String

        data class SignIn(
            override val email: String,
            override val password: String,
        ) : Password

        data class SignUp(
            override val email: String,
            override val password: String,
        ) : Password
    }

    sealed interface Phone : AuthenticationType {
        val verificationId: String
        val code: String

        data class SignIn(
            override val verificationId: String,
            override val code: String,
        ) : Phone

        data class SignUp(
            override val verificationId: String,
            override val code: String,
        ) : Phone
    }

    sealed interface Google : AuthenticationType {
        val idToken: String

        data class SignIn(
            override val idToken: String,
        ) : Google

        data class SignUp(
            override val idToken: String,
        ) : Google
    }
}