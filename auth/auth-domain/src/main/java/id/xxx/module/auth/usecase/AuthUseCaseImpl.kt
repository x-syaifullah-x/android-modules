package id.xxx.module.auth.usecase

import id.xxx.module.auth.model.OobType
import id.xxx.module.auth.model.SignInType
import id.xxx.module.auth.model.SignUpType
import id.xxx.module.auth.model.UpdateType
import id.xxx.module.auth.repository.AuthRepository

class AuthUseCaseImpl private constructor(
    private val repo: AuthRepository
) : AuthUseCase {

    companion object {
        @Volatile
        private var INSTANCE: AuthUseCaseImpl? = null

        fun getInstance(repo: AuthRepository): AuthUseCase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthUseCaseImpl(repo)
                    .also { INSTANCE = it }
            }
    }

    override fun currentUser() =
        repo.currentUser()

    override fun signIn(type: SignInType) =
        repo.signIn(type)

    override fun signUp(type: SignUpType) =
        repo.signUp(type)

    override fun sendVerificationCode(phoneNumber: String, recaptchaResponse: String) =
        repo.sendVerificationCode(phoneNumber, recaptchaResponse)

    override fun signOut() =
        repo.signOut()

    override fun sendOobCode(type: OobType) =
        repo.sendOobCode(type)

    override fun resetPassword(oobCode: String, newPassword: String) =
        repo.resetPassword(oobCode, newPassword)

    override fun update(type: UpdateType) =
        repo.update(type)
}