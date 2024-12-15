package id.xxx.module.authentication

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import id.xxx.module.autentication.AuthenticationFragment
import id.xxx.module.autentication.AuthenticationViewModel
import id.xxx.module.autentication.IAuthentication
import id.xxx.module.auth.data.AuthRepositoryImpl
import id.xxx.module.auth.data.source.local.LocalDataSource
import id.xxx.module.auth.data.source.local.db.AppDatabase
import id.xxx.module.auth.data.source.remote.RemoteDataSource
import id.xxx.module.auth.domain.model.AuthenticationType
import id.xxx.module.auth.domain.usecase.AuthUseCaseImpl
import id.xxx.module.common.Resources
import kotlinx.coroutines.flow.map

class DemoActivity : AppCompatActivity(), IAuthentication {

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
//                val savedStateHandle = extras.createSavedStateHandle()
                val db = AppDatabase.getInstance(application)
                val authRepository = AuthRepositoryImpl.getInstance(
                    local = LocalDataSource.getInstance(dao = db.userDao),
                    remote = RemoteDataSource.getInstance(),
                )
                val authUseCase = AuthUseCaseImpl.getInstance(authRepository)
                return AuthenticationViewModel(authUseCase) as T
            }
        }
    }

    private val authenticationViewModel: AuthenticationViewModel by viewModels { Factory }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() = finishAfterTransition()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, AuthenticationFragment::class.java, null)
                .commit()
    }

    override fun onAuthentication(type: AuthenticationType) =
        authenticationViewModel.authentication(type).map { res ->
            if (res is Resources.Success)
                supportFragmentManager.beginTransaction().replace(
                    android.R.id.content,
                    HomeFragment::class.java,
                    bundleOf(HomeFragment.KEY_DATA_EXTRA_USER to res.value)
                ).commit()
            return@map res
        }
}