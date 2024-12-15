package id.xxx.module.autentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.xxx.module.autentication.IAuthentication.Companion.iSign
import id.xxx.module.autentication.google.GoogleAccountResultContract
import id.xxx.module.autentication.password.LoginPasswordFragment
import id.xxx.module.autentication.phone.PhoneFragment
import id.xxx.module.auth.domain.model.AuthenticationType
import id.xxx.module.viewbinding.ktx.viewBinding
import id.xxx.modules.authentication.presentation.databinding.FragmentAuthenticationBinding
import kotlinx.coroutines.launch

class AuthenticationFragment : Fragment() {

    private val vBinding by viewBinding<FragmentAuthenticationBinding>()
    private val googleSignLauncher = registerForActivityResult(GoogleAccountResultContract()) {
        val idToken = it?.idToken ?: return@registerForActivityResult
        viewLifecycleOwner.lifecycleScope.launch {
            val res = iSign()?.onAuthentication(AuthenticationType.Google(idToken = idToken))
            if (res is IAuthenticationResult.Error)
                Toast.makeText(context, res.err.message, Toast.LENGTH_LONG).show()
        }
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = vBinding.root.apply {
        if (background == null)
            background = activity?.window?.decorView?.background
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, onBackPressedCallback)

        val idContainerForm = vBinding.containerForm.id
        parentFragmentManager.beginTransaction()
            .replace(idContainerForm, LoginPasswordFragment::class.java, null)
            .commit()

        vBinding.btnContinueWithPhone.setOnClickListener(this::onClickBtnContinueWithPhone)
        vBinding.btnContinueWithPassword.setOnClickListener(this::onClickBtnContinueWithPassword)
        vBinding.btnContinueWithGoogle.setOnClickListener(this::onClickBtnContinueWithGoogle)
    }

    private fun onClickBtnContinueWithGoogle(v: View) {
        val webClientId =
            "1098413132051-k1fm576dsbahfigib4e480sa4hflrah7.apps.googleusercontent.com"
        googleSignLauncher.launch(GoogleAccountResultContract.createIntent(webClientId))
    }

    private fun onClickBtnContinueWithPassword(v: View) {
        v.visibility = View.INVISIBLE
        vBinding.btnContinueWithPhone.visibility = View.VISIBLE
        parentFragmentManager.beginTransaction()
            .replace(vBinding.containerForm.id, LoginPasswordFragment::class.java, null)
            .commit()
    }

    private fun onClickBtnContinueWithPhone(v: View) {
        v.visibility = View.INVISIBLE
        vBinding.btnContinueWithPassword.visibility = View.VISIBLE
        parentFragmentManager.beginTransaction()
            .replace(vBinding.containerForm.id, PhoneFragment::class.java, null)
            .commit()
    }

    override fun onDestroyView() {
        val containerFormFragment =
            parentFragmentManager.findFragmentById(vBinding.containerForm.id)
        /* CONTAINER FORM FRAGMENT NO REMOVE IS LEAK */
        if (containerFormFragment != null)
            parentFragmentManager.beginTransaction().remove(containerFormFragment).commit()
        super.onDestroyView()
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