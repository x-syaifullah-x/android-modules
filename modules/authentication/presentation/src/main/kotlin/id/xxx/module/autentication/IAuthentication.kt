package id.xxx.module.autentication

import id.xxx.module.auth.domain.model.User
import id.xxx.module.auth.domain.model.AuthMethod
import id.xxx.module.common.Resources
import kotlinx.coroutines.flow.Flow

interface IAuthentication {

    fun onAuthenticate(request: AuthMethod): Flow<Resources<User>>
}