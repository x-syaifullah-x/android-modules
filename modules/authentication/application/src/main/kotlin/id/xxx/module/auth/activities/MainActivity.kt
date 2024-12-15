package id.xxx.module.auth.activities

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import id.xxx.module.auth.activities.fragment.HomeFragment
import id.xxx.module.auth.domain.model.AuthenticationType
import id.xxx.module.autentication.IAuthentication
import id.xxx.module.autentication.IAuthenticationResult
import id.xxx.module.autentication.password.LoginPasswordFragment
import id.xxx.module.common.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : FragmentActivity(), IAuthentication {

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishAfterTransition()
//            val fragments = supportFragmentManager.fragments
//            if (fragments.size > 1) {
//                supportFragmentManager.beginTransaction().remove(
//                    fragments.last()
//                ).commit()
//
//                if (supportFragmentManager.fragments.size == 0) {
//                    finishAfterTransition()
//                }
//            } else
//                finishAfterTransition()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        if (savedInstanceState == null) {
            lifecycleScope.launch {
                when (val currentUser = viewModel.currentUser.asFlow().drop(1).first()) {
                    is Resources.Loading -> throw NotImplementedError()
                    is Resources.Success -> {
                        val user = currentUser.value
                        val data =
                            if (user == null)
                                LoginPasswordFragment::class.java to null
                            else
                                HomeFragment::class.java to bundleOf(HomeFragment.KEY_DATA_EXTRA_USER to user)
                        supportFragmentManager.beginTransaction()
                            .replace(android.R.id.content, data.first, data.second)
                            .commit()
                    }

                    is Resources.Failure -> {}
                }
            }
        }
    }

    override suspend fun onAuthentication(type: AuthenticationType) =
        when (val res = viewModel.sign(type).lastOrNull()) {
            is Resources.Success -> {
                withContext(Dispatchers.Main) {
                    supportFragmentManager.beginTransaction().replace(
                        android.R.id.content,
                        HomeFragment::class.java,
                        bundleOf(HomeFragment.KEY_DATA_EXTRA_USER to res.value)
                    ).commit()
                }
                IAuthenticationResult.Success
            }

            is Resources.Failure -> IAuthenticationResult.Error(res.value)

            else -> throw NotImplementedError()
        }
}