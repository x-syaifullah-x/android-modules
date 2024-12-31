package id.xxx.module.auth.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import id.xxx.module.auth.data.source.remote.model.SignResult
import id.xxx.module.auth.domain.model.AuthenticationType
import kotlinx.coroutines.tasks.await

class RemoteDataSource private constructor() {

    companion object {

        @Volatile
        private var _sInstance: RemoteDataSource? = null

        fun getInstance() = _sInstance ?: synchronized(this) {
            _sInstance ?: RemoteDataSource().also { _sInstance = it }
        }
    }

    internal suspend fun sign(type: AuthenticationType): SignResult {
        val auth = FirebaseAuth.getInstance()
        auth.useEmulator("192.168.43.89", 9099)
        val authResult = when (type) {
            is AuthenticationType.Google -> {
                auth.signInWithCredential(GoogleAuthProvider.getCredential(type.idToken, null))
            }

            is AuthenticationType.Phone -> {
                val credential = PhoneAuthProvider.getCredential(type.verificationId, type.code)
                auth.signInWithCredential(credential)
            }

            is AuthenticationType.Password -> {
                val email = type.email
                val password = type.password
                if (type.mode == AuthenticationType.Mode.Signup)
                    auth.createUserWithEmailAndPassword(email, password)
                else
                    auth.signInWithEmailAndPassword(email, password)
            }
        }.await()
        val user = authResult.user
            ?: throw NullPointerException("user")

        val additionalUserInfo = authResult.additionalUserInfo
        val isNewUser = additionalUserInfo?.isNewUser
            ?: throw NullPointerException("isNewUser")

        if (type.mode == AuthenticationType.Mode.Login && isNewUser) {
            user.delete().await()
            throw IllegalArgumentException("user not found")
        }

        if (type.mode == AuthenticationType.Mode.Signup && !isNewUser) {
            auth.signOut()
            throw IllegalArgumentException("user already exist")
        }

        val token = user.getIdToken(false).await()
        val claimsFirebase = token.claims["firebase"] as Map<*, *>
        val signProvider = "${claimsFirebase["sign_in_provider"]}"
        return SignResult(uid = user.uid, isNewUser = isNewUser, signProvider = signProvider)
    }
}