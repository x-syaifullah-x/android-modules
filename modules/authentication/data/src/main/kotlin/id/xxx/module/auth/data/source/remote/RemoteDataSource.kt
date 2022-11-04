package id.xxx.module.auth.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import id.xxx.module.auth.data.source.remote.model.SignResult
import id.xxx.module.auth.domain.exception.AccountAlreadyInUseException
import id.xxx.module.auth.domain.exception.UserNotRegisteredException
import id.xxx.module.auth.domain.model.AuthMethod
import kotlinx.coroutines.tasks.await

class RemoteDataSource private constructor() {

    companion object {

        @Volatile
        private var _sInstance: RemoteDataSource? = null

        fun getInstance() = _sInstance ?: synchronized(this) {
            _sInstance ?: RemoteDataSource().also { _sInstance = it }
        }
    }

    internal suspend fun sign(method: AuthMethod): SignResult {
        val auth = FirebaseAuth.getInstance()

        val isSignUp = method.mode == AuthMethod.Mode.UP

        val authResult =
            try {
                when (method) {
                    is AuthMethod.Password -> {
                        if (isSignUp)
                            auth.createUserWithEmailAndPassword(method.email, method.password)
                        else
                            auth.signInWithEmailAndPassword(method.email, method.password)
                    }

                    is AuthMethod.Phone -> {
                        val credential =
                            PhoneAuthProvider.getCredential(method.verificationId, method.code)
                        auth.signInWithCredential(credential)
                    }

                    is AuthMethod.Google -> {
                        val credential = GoogleAuthProvider.getCredential(method.idToken, null)
                        auth.signInWithCredential(credential)
                    }
                }.await()
            } catch (e: Throwable) {
                when (e) {
                    is FirebaseAuthInvalidCredentialsException ->
                        throw UserNotRegisteredException(message = e.message)

                    is FirebaseAuthUserCollisionException ->
                        throw AccountAlreadyInUseException(message = e.message)

                    else ->
                        throw e
                }
            }

        val user = authResult.user
            ?: throw NullPointerException("user")

        val additionalUserInfo = authResult.additionalUserInfo
        val isNewUser = additionalUserInfo?.isNewUser
            ?: throw NullPointerException("isNewUser")

        if (isSignUp && !isNewUser) {
            auth.signOut();
            throw AccountAlreadyInUseException()
        }

        if (!isSignUp && isNewUser) {
            user.delete().await()
            throw UserNotRegisteredException(message = "The supplied auth credential is incorrect, malformed or has expired.")
        }

        val token = user.getIdToken(false).await()
        val claimsFirebase = token.claims["firebase"] as Map<*, *>
        val signProvider = "${claimsFirebase["sign_in_provider"]}"
        return SignResult(uid = user.uid, isNewUser = isNewUser, signProvider = signProvider)
    }
}