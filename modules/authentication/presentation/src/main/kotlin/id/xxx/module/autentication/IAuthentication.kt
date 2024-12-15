package id.xxx.module.autentication

import androidx.fragment.app.Fragment
import id.xxx.module.auth.domain.model.AuthenticationType
import id.xxx.module.auth.domain.model.UserModel
import id.xxx.module.common.Resources
import kotlinx.coroutines.flow.Flow

interface IAuthentication {

    companion object {

        val Fragment.authentication: IAuthentication?
            get() =
                if (parentFragment is IAuthentication)
                    parentFragment as? IAuthentication
                else if (activity is IAuthentication)
                    activity as? IAuthentication
                else
                    null
    }

    fun onAuthentication(type: AuthenticationType): Flow<Resources<UserModel>>
}