package id.xxx.module.autentication.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.xxx.module.autentication.IAuthentication.Companion.authentication
import id.xxx.module.auth.domain.model.AuthenticationType
import id.xxx.module.common.Resources
import id.xxx.module.viewbinding.ktx.viewBinding
import id.xxx.modules.authentication.presentation.databinding.FragmentSignInBinding
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LoginPasswordFragment : Fragment() {

    private val vBinding by viewBinding<FragmentSignInBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        vBinding.buttonLogin.setOnClickListener(this::onLogin)
        vBinding.tvSignUp.setOnClickListener(this::onSignup)
    }

    private fun onSignup(v: View) {
        parentFragmentManager.beginTransaction()
            .replace(id, SignupPasswordFragment::class.java, null)
            .commit()
    }

    private fun onLogin(v: View) {
        v.isEnabled = false
        vBinding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val email = "${vBinding.textInputEditTextEmail.text}"
            val password = "${vBinding.textInputEditTextPassword.text}"
            val t = AuthenticationType.Password(email = email, password = password)
            val res = authentication?.onAuthentication(t)?.lastOrNull()
            if (res is Resources.Failure) {
                if (isActive)
                    Toast.makeText(v.context, res.value.message, Toast.LENGTH_LONG).show()
            }
            v.isEnabled = true
            vBinding.progressBar.visibility = View.GONE
        }
    }
}