package id.xxx.module.autentication.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import id.xxx.module.autentication.password.LoginPasswordFragment
import id.xxx.module.viewbinding.ktx.viewBinding
import id.xxx.modules.authentication.presentation.databinding.FragmentPhoneBinding

class PhoneFragment : Fragment() {

    companion object {

        const val DATA_EXTRA_PHONE_NUMBER = "data_extra_phone_number"
        const val DATA_EXTRA_MESSAGE_ERROR = "data_extra_message_error"
    }

    private val vBinding by viewBinding<FragmentPhoneBinding>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val sfm = requireActivity().supportFragmentManager
            sfm.beginTransaction().replace(id, LoginPasswordFragment::class.java, null).commit()
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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )

        arguments?.getString(DATA_EXTRA_MESSAGE_ERROR)?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            arguments?.remove(DATA_EXTRA_MESSAGE_ERROR)
        }

        val phoneNumber = arguments?.getString(DATA_EXTRA_PHONE_NUMBER)
            ?: "+628123456789"
        vBinding.textInputEditTextPhone.setText(phoneNumber)
        vBinding.textInputEditTextPhone.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val hint =
                    if (hasFocus)
                        "Phone Number"
                    else
                        "Please enter phone number"
                vBinding.textInputLayoutPhone.hint = hint
            }

        vBinding.buttonNext.setOnClickListener {
            onClickButtonNet(view = it, phoneNumber = "${vBinding.textInputEditTextPhone.text}")
        }
    }

    private fun onClickButtonNet(view: View, phoneNumber: String) {
        requireActivity().supportFragmentManager.beginTransaction().replace(
            this@PhoneFragment.id,
            PhoneOTPSanderFragment::class.java,
            bundleOf(PhoneOTPSanderFragment.DATA_EXTRA_PHONE_NUMBER to phoneNumber),
            null
        ).commit()

//        viewModel.verifyPhoneNumber(activity = requireActivity(), phoneNumber = phoneNumber)
//        requireActivity().supportFragmentManager.beginTransaction().replace(
//            this@PhoneFragment.id,
//            PhoneVerificationFragment::class.java,
//            bundleOf(PhoneVerificationFragment.DATA_EXTRA_PHONE_NUMBER to phoneNumber),
//            null
//        ).commit()

//        vBinding.progressBar.visibility = View.VISIBLE
//        view.isEnabled = false
//        viewModel.verifyPhoneNumber(
//            viewLifecycleOwner,
//            activity = requireActivity(),
//            phoneNumber = phoneNumber,
//            onCodeSent = {
//                if (!view.isEnabled) {
//                    vBinding.progressBar.visibility = View.GONE
//                    requireActivity().supportFragmentManager.beginTransaction()
//                        .add(
//                            this@PhoneFragment.id,
//                            PhoneVerificationFragment::class.java,
//                            bundleOf(PhoneVerificationFragment.DATA_EXTRA_PHONE_NUMBER to phoneNumber),
//                            null
//                        ).commit()
//                    view.isEnabled = true
//                }
//            },
//            onError = {
//                view.isEnabled = true
//                vBinding.progressBar.visibility = View.GONE
//                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
//            }
//        )
    }
}