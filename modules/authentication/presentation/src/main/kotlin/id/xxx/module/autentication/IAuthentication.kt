package id.xxx.module.autentication

import androidx.fragment.app.Fragment
import id.xxx.module.auth.domain.model.AuthenticationType

interface IAuthentication {

    companion object {

        fun Fragment.iSign() =
            if (parentFragment is IAuthentication)
                parentFragment as? IAuthentication
            else if (activity is IAuthentication)
                activity as? IAuthentication
            else
                null
    }

    suspend fun onAuthentication(type: AuthenticationType): IAuthenticationResult
}