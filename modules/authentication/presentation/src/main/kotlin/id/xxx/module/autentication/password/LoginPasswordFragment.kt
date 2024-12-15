package id.xxx.module.autentication.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.xxx.module.autentication.IAuthentication
import id.xxx.module.autentication.IAuthenticationResult
import id.xxx.module.auth.domain.model.AuthenticationType
import id.xxx.module.viewbinding.ktx.viewBinding
import id.xxx.modules.authentication.presentation.databinding.FragmentSignInBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LoginPasswordFragment : Fragment() {

    private val vBinding by viewBinding<FragmentSignInBinding>()

    private var jobSignIn: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = vBinding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vBinding.textInputEditTextEmail.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val hint =
                    if (hasFocus)
                        "Email"
                    else
                        "Please enter your email"
                vBinding.textInputLayoutEmail.hint = hint
            }
        vBinding.textInputEditTextPassword.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val hint =
                    if (hasFocus)
                        "Password"
                    else
                        "Please enter your password"
                vBinding.textInputLayoutPassword.hint = hint
            }
        vBinding.buttonSignIn.setOnClickListener { signIn() }
        vBinding.tvSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(id, SignupPasswordFragment::class.java, null)
                .commit()
        }
    }

    private fun signIn() {
        vBinding.buttonSignIn.isEnabled = false
        vBinding.progressBar.visibility = View.VISIBLE
        val iAuthentication =
            if (parentFragment is IAuthentication)
                parentFragment as? IAuthentication
            else if (activity is IAuthentication)
                activity as? IAuthentication
            else
                null
        jobSignIn = lifecycleScope.launch {
            val email = "${vBinding.textInputEditTextEmail.text}"
            val password = "${vBinding.textInputEditTextPassword.text}"
            val t = AuthenticationType.Password(email = email, password = password)
            val res = iAuthentication?.onAuthentication(t)
            if (res is IAuthenticationResult.Error) {
                if (isActive) {
                    val c = vBinding.root.context
                    Toast.makeText(c, res.err.message, Toast.LENGTH_LONG).show()
                }
            }
            vBinding.buttonSignIn.isEnabled = true
            vBinding.progressBar.visibility = View.GONE
        }
    }
}