package id.xxx.module.auth.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import id.xxx.module.auth.data.source.remote.model.SignResult
import id.xxx.module.auth.domain.exception.AccountAlreadyInUseException
import id.xxx.module.auth.domain.exception.UserNotRegisteredException
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
//        auth.useEmulator("192.168.43.89", 9099)
        val authResult = try {
            when (type) {
                is AuthenticationType.SignInGoogle -> {
                    auth.signInWithCredential(GoogleAuthProvider.getCredential(type.idToken, null))
                }

                is AuthenticationType.SignUpGoogle -> {
                    auth.signInWithCredential(GoogleAuthProvider.getCredential(type.idToken, null))
                }

                is AuthenticationType.SignUpPhone -> {
                    val credential = PhoneAuthProvider.getCredential(type.verificationId, type.code)
                    auth.signInWithCredential(credential)
                }

                is AuthenticationType.SignInPhone -> {
                    val credential = PhoneAuthProvider.getCredential(type.verificationId, type.code)
                    auth.signInWithCredential(credential)
                }

                is AuthenticationType.SignInPassword -> {
                    val email = type.email
                    val password = type.password
                    auth.signInWithEmailAndPassword(email, password)
                    auth.signInWithEmailAndPassword(email, password)
                }

                is AuthenticationType.SignUpPassword -> {
                    val email = type.email
                    val password = type.password
                    auth.signInWithEmailAndPassword(email, password)
                    auth.createUserWithEmailAndPassword(email, password)
                }
            }.await()
        } catch (e: Throwable) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> throw UserNotRegisteredException()
                is FirebaseAuthUserCollisionException -> throw AccountAlreadyInUseException()
                else -> throw e
            }
        }
        val user = authResult.user
            ?: throw NullPointerException("user")

        val additionalUserInfo = authResult.additionalUserInfo
        val isNewUser = additionalUserInfo?.isNewUser
            ?: throw NullPointerException("isNewUser")

        if (type.flag == AuthenticationType.Flag.SIGN_IN && isNewUser) {
            user.delete().await()
            throw UserNotRegisteredException()
        }

        if (type.flag == AuthenticationType.Flag.SIGN_UP && !isNewUser) {
            auth.signOut(); throw AccountAlreadyInUseException()
        }

        val token = user.getIdToken(false).await()
        val claimsFirebase = token.claims["firebase"] as Map<*, *>
        val signProvider = "${claimsFirebase["sign_in_provider"]}"
        return SignResult(uid = user.uid, isNewUser = isNewUser, signProvider = signProvider)
    }
}