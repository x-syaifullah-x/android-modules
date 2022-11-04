package id.xxx.module.auth

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.xxx.module.auth.domain.model.User
import id.xxx.module.viewbinding.ktx.viewBinding
import id.xxx.module.auth.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    companion object {

        const val KEY_DATA_EXTRA_USER = "key_data_extra_user"
    }

    private val vBinding by viewBinding<FragmentHomeBinding>()

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

        println(parentFragmentManager.fragments)

        val user =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getSerializable(KEY_DATA_EXTRA_USER, User::class.java)
            } else {
                @Suppress("DEPRECATION")
                arguments?.getSerializable(KEY_DATA_EXTRA_USER) as? User
            }
        vBinding.aa.text = "$user"
    }
}