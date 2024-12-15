package id.xxx.module.autentication

import androidx.lifecycle.ViewModel
import id.xxx.module.auth.domain.model.AuthenticationType
import id.xxx.module.auth.domain.usecase.AuthUseCase

class AuthenticationViewModel(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    fun authentication(authType: AuthenticationType) = authUseCase.sign(authType)
}