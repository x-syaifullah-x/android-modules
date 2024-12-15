package id.xxx.module.autentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.xxx.module.autentication.IAuthentication.Companion.authentication
import id.xxx.module.autentication.google.GoogleAccountResultContract
import id.xxx.module.autentication.password.LoginPasswordFragment
import id.xxx.module.autentication.phone.PhoneFragment
import id.xxx.module.autentication.phone.PhoneOTPSanderFragment
import id.xxx.module.autentication.phone.PhoneOTPVerifierFragment
import id.xxx.module.auth.domain.model.AuthenticationType
import id.xxx.module.common.Resources
import id.xxx.module.viewbinding.ktx.viewBinding
import id.xxx.modules.authentication.presentation.databinding.FragmentAuthenticationBinding
import kotlinx.coroutines.launch

/**
 * EXAMPLE
 * ```
 * class ExampleActivity : AppCompatActivity(), IAuthentication {
 *
 *     private val authenticationViewModel: AuthenticationViewModel by viewModels { Factory }
 *     private val onBackPressedCallback = object : OnBackPressedCallback(true) {
 *         override fun handleOnBackPressed() = finishAfterTransition()
 *     }
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         onBackPressedDispatcher.addCallback(onBackPressedCallback)
 *         if (savedInstanceState == null)
 *             supportFragmentManager.beginTransaction()
 *             .replace(android.R.id.content, AuthenticationFragment::class.java, null)
 *             .commit()
 *     }
 *
 *     override fun onAuthentication(type: AuthenticationType) = demoViewModel.authentication(type).map { res ->
 *         if (res is Resources.Success)
 *             Toast.makeText(this, "authentication success", Toast.LENGTH_LONG).show()
 *         return@map res
 *     }
 * }
 * ```
 */
class AuthenticationFragment : Fragment() {

    private val vBinding by viewBinding<FragmentAuthenticationBinding>()

    private val googleAccountLauncher = registerForActivityResult(GoogleAccountResultContract()) {
        val authType = AuthenticationType.Google(it?.idToken ?: return@registerForActivityResult)
        lifecycleScope.launch {
            authentication?.onAuthentication(authType)?.collect { res ->
                vBinding.btnContinueWithGoogle.isEnabled = res !is Resources.Loading
                val progress = vBinding.progressAuthGoogle
                when (res) {
                    is Resources.Loading -> progress.visibility = View.VISIBLE
                    is Resources.Success -> progress.visibility = View.GONE
                    is Resources.Failure -> {
                        progress.visibility = View.GONE
                        Toast.makeText(context, res.value.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val ft = parentFragmentManager.beginTransaction()
            when (val lastFragment = parentFragmentManager.fragments.lastOrNull()) {
                is PhoneOTPVerifierFragment -> ft.remove(lastFragment).commit()
                is PhoneOTPSanderFragment -> ft.remove(lastFragment).commit()
                else -> {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = vBinding.root.apply {
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, onBackPressedCallback)
        if (background == null) background = activity?.window?.decorView?.background
        val idContainerForm = vBinding.content.id
        if (savedInstanceState == null)
            replaceContent(LoginPasswordFragment::class.java)
        if (childFragmentManager.findFragmentById(idContainerForm) is PhoneFragment) {
            vBinding.btnContinueWithPhone.visibility = View.GONE
            vBinding.btnContinueWithPassword.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vBinding.btnContinueWithPhone.setOnClickListener(this::onClickContinueWithPhone)
        vBinding.btnContinueWithPassword.setOnClickListener(this::onClickContinueWithPassword)
        vBinding.btnContinueWithGoogle.setOnClickListener(this::onClickContinueWithGoogle)
    }

    private fun onClickContinueWithGoogle(v: View) =
        googleAccountLauncher.launch(null)

    private fun onClickContinueWithPassword(v: View) {
        v.visibility = View.INVISIBLE
        vBinding.btnContinueWithPhone.visibility = View.VISIBLE
        replaceContent(LoginPasswordFragment::class.java)
    }

    private fun onClickContinueWithPhone(v: View) {
        v.visibility = View.INVISIBLE
        vBinding.btnContinueWithPassword.visibility = View.VISIBLE
        replaceContent(PhoneFragment::class.java)
    }

    private fun <T : Fragment> replaceContent(
        fragmentClass: Class<T>, args: Bundle? = null, tag: String? = null
    ) = childFragmentManager.beginTransaction()
        .replace(vBinding.content.id, fragmentClass, args, tag)
        .commit()


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