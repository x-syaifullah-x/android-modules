package id.xxx.module.autentication.phone

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import id.xxx.module.viewbinding.ktx.viewBinding
import id.xxx.modules.authentication.presentation.databinding.FragmentRacaptchaBinding
import org.json.JSONObject
import java.util.Locale

class PhoneOTPSanderFragment : Fragment() {

    companion object {

        const val DATA_EXTRA_PHONE_NUMBER = "KEY_PHONE_NUMBER"
    }

    private val vBinding by viewBinding<FragmentRacaptchaBinding>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val webView = vBinding.webView
            if (webView.canGoBack())
                webView.goBack()
            else
                parentFragmentManager.beginTransaction()
                    .remove(this@PhoneOTPSanderFragment).commit()
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

        val phoneNumber = arguments?.getString(DATA_EXTRA_PHONE_NUMBER)
            ?: throw Throwable("required argument phone number")

        val webView = vBinding.webView
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, Paint())
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
//                viewBinding.progressBar.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                println(error.description)
            }
        }

        val webSettings = webView.settings
        @SuppressLint("SetJavaScriptEnabled")
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE

        webView.addJavascriptInterface(object : Any() {
            @Suppress("unused")
            @JavascriptInterface
            fun onSend(response: String) {
                val j = JSONObject(response)
                try {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(this@PhoneOTPSanderFragment).commit()

                    val sessionInfo = j.getString("sessionInfo")
                    val args = bundleOf(
                        PhoneOTPVerifierFragment.DATA_EXTRA_PHONE_NUMBER to phoneNumber,
                        PhoneOTPVerifierFragment.DATA_EXTRA_SESSION_INFO to sessionInfo,
                    )
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(id, PhoneOTPVerifierFragment::class.java, args, null)
                        .commit()
                } catch (t: Throwable) {
                    val err = j.getJSONObject("error")
//                    val errCode = err.getString("code")
                    val errMessage = err.getString("message")
                    println(errMessage)
                }
            }
        }, "SendVerificationCode")

        val uri =
            Uri.Builder()
                .scheme("https")
                .authority("send-verification-code.web.app")
                .path("/index.html")
                .appendQueryParameter("phoneNumber", phoneNumber)
                .appendQueryParameter("languageCode", Locale.getDefault().language)
                .build()
        webView.loadUrl(uri.toString())
    }

    override fun onDestroyView() {
        val webView = vBinding.webView
        webView.removeAllViews()
        webView.destroy()
        super.onDestroyView()
    }
}