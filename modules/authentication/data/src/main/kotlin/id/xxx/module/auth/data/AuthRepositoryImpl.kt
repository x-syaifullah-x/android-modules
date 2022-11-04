package id.xxx.module.auth.data

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthOptions
import id.xxx.module.auth.data.source.local.LocalDataSource
import id.xxx.module.auth.data.source.local.entity.UserEntity
import id.xxx.module.auth.data.source.remote.RemoteDataSource
import id.xxx.module.auth.domain.exception.AuthInvalidCredentialsException
import id.xxx.module.auth.domain.exception.AuthNetworkException
import id.xxx.module.auth.domain.model.User
import id.xxx.module.auth.domain.model.AuthMethod
import id.xxx.module.auth.domain.repository.AuthRepository
import id.xxx.module.common.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class AuthRepositoryImpl private constructor(
    private val local: LocalDataSource,
    private val remote: RemoteDataSource
) : AuthRepository {

    companion object {

        @Volatile
        private var _sInstance: AuthRepositoryImpl? = null

        fun getInstance(
            local: LocalDataSource, remote: RemoteDataSource
        ): AuthRepository = _sInstance ?: synchronized(this) {
            _sInstance ?: AuthRepositoryImpl(
                local = local, remote = remote
            ).also { _sInstance = it }
        }
    }

    override fun sign(method: AuthMethod): Flow<Resources<User>> = flow {
        emit(Resources.Loading())
        val result = remote.sign(method)
        val uid = result.uid
        val isNewUser = result.isNewUser
        val signProvider = result.signProvider
        val userEntity = UserEntity(
            uid = uid,
            isLoggedIn = true,
            isNewUser = isNewUser,
            signProvider = signProvider
        )
        val isSaveToLocal = local.save(userEntity)
        val signModel = User(id = uid, isNew = isNewUser, provider = signProvider)
        if (isSaveToLocal) {
            emit(Resources.Success(signModel))
            return@flow
        }
        logInfo(AuthRepositoryImpl::sign.name, "isSaveToLocal: false")
        emit(Resources.Success(signModel))
    }.flowOn(Dispatchers.IO)
        .catch {
            when (it) {
                is FirebaseNetworkException ->
                    emit(Resources.Failure(AuthNetworkException(it.message)))

                is FirebaseAuthInvalidCredentialsException ->
                    emit(Resources.Failure(AuthInvalidCredentialsException(it.message)))

                else -> emit(Resources.Failure(it))
            }
        }

    override fun getCurrentUser(): Flow<Resources<User?>> = callbackFlow {
        trySend(Resources.Loading())
        val job = launch {
            local.getUserLoggedIn().collect {
                val res =
                    if (it == null)
                        null
                    else
                        User(
                            id = it.uid,
                            isNew = it.isNewUser,
                            provider = it.signProvider
                        )
                trySend(Resources.Success(res))
            }
        }
        awaitClose { job.cancel() }
    }.flowOn(Dispatchers.IO)
        .catch { emit(Resources.Failure(it)) }

    private fun logInfo(funName: String, message: String) {
        val tag = "${AuthRepositoryImpl::class.java.simpleName}=>$funName"
        Log.i(tag, message)
    }
}