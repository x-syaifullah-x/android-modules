package id.xxx.module.auth.domain.usecase

import id.xxx.module.auth.domain.model.User
import id.xxx.module.auth.domain.model.AuthMethod
import id.xxx.module.common.Resources
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {

    fun sign(method: AuthMethod): Flow<Resources<User>>

    fun getCurrentUser(): Flow<Resources<User?>>
}