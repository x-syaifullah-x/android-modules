package id.xxx.module.autentication.helper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.xxx.module.auth.domain.model.AuthenticationType
import id.xxx.module.autentication.IAuthentication.Companion.iSign
import id.xxx.module.autentication.IAuthenticationResult
import id.xxx.module.autentication.google.GoogleAccountResultContract
import id.xxx.module.viewbinding.ktx.viewBinding
import id.xxx.modules.authentication.presentation.databinding.FragmentXxxBinding
import kotlinx.coroutines.launch

class ContinueWithFragment : Fragment() {

    private val vBinding by viewBinding<FragmentXxxBinding>()

    private val googleSignLauncher =
        registerForActivityResult(GoogleAccountResultContract()) {
            val idToken = it?.idToken ?: return@registerForActivityResult
            viewLifecycleOwner.lifecycleScope.launch {
                val res = iSign()?.onAuthentication(AuthenticationType.Google(idToken = idToken))
                if (res is IAuthenticationResult.Error)
                    Toast.makeText(context, res.err.message, Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = vBinding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vBinding.btnContinueWithGoogle.setOnClickListener {
            val i =
                GoogleAccountResultContract.createIntent(webClientId = "1098413132051-k1fm576dsbahfigib4e480sa4hflrah7.apps.googleusercontent.com")
            googleSignLauncher.launch(i)
        }
    }

    /* MEMORY LEAK WITH CredentialManager.create() */
//    private fun continueWithGoogle(c: FragmentActivity) {
//        c.lifecycleScope.launch {
//            val googleIdOption = GetGoogleIdOption.Builder()
//                .setFilterByAuthorizedAccounts(true)
//                .setServerClientId("1098413132051-k1fm576dsbahfigib4e480sa4hflrah7.apps.googleusercontent.com")
//                .setAutoSelectEnabled(false)
//                .build()
//            val request = GetCredentialRequest.Builder()
//                .addCredentialOption(googleIdOption)
//                .build()
//            val credentialManager = CredentialManager.create(c)
//            try {
//                val gcr = credentialManager.getCredential(c, request)
//                val credential = gcr.credential
//                val type = credential.type
//                if (type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//                    val googleIdTokenCredential = GoogleIdTokenCredential
//                        .createFrom(gcr.credential.data)
//                    val idToken = googleIdTokenCredential.idToken
//                    val r = iSign()?.onSign(TypeSign.Google(idToken = idToken))
//                    if (r is SignResult.Success) {
//                        requireActivity().onBackPressedDispatcher.onBackPressed()
//                    }
//                } else {
//                    throw NotImplementedError()
//                }
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            } finally {
//                cancel()
//            }
//        }
//    }
}