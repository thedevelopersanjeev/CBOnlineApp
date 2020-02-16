package com.codingblocks.cbonlineapp.auth.onboarding

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.BuildConfig.CLIENT_ID
import com.codingblocks.cbonlineapp.BuildConfig.OAUTH_URL
import com.codingblocks.cbonlineapp.BuildConfig.REDIRECT_URI
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.extensions.getSpannableStringSecondBold
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import kotlinx.android.synthetic.main.fragment_login_home.*

class LoginHomeFragment : BaseCBFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_login_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setfirstSpan()
        setSecondSpan()

        mobileBtn.setOnClickListener {
            replaceFragmentSafely(
                SignInFragment(),
                tag = "SignIn",
                containerViewId = R.id.loginContainer
            )
        }

        gmailBtn.setOnClickListener {
            showWebView()
        }

        fbBtn.setOnClickListener {
            requireContext().openChrome(
                "${BuildConfig.OAUTH_URL}?redirect_uri=${BuildConfig.REDIRECT_URI}&response_type=code&client_id=${BuildConfig.CLIENT_ID}"
            )
        }
    }

    private fun showWebView() {
        CookieManager.getInstance().apply {
            setAcceptCookie(false)
            setAcceptThirdPartyCookies(webview, false)
        }
        val web: WebView = webview
        web.settings.javaScriptEnabled = true
        web.loadUrl("$OAUTH_URL?redirect_uri=$REDIRECT_URI&response_type=code&client_id=$CLIENT_ID")
        web.webViewClient = object : WebViewClient() {

            var authComplete = false

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                if (url.contains("code=") && !authComplete) {
                    val grantCode = Uri.parse(url).getQueryParameter("code")
                    authComplete = true
                } else if (url.contains("error=access_denied")) {
                    authComplete = true
                    web.visibility = View.GONE
                }
            }
        }
        web.visibility = View.VISIBLE
    }

    private fun setSecondSpan() {
        val policySpan = SpannableString(
            "By logging in you agree to Coding Blocks’s\n" +
                "Privacy Policy & Terms of Service"
        )
        val privacySpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                requireContext().openChrome("https://codingblocks.com/privacypolicy.html")
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.isUnderlineText = true
            }
        }

        val tosSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                requireContext().openChrome("https://codingblocks.com/tos.html")
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.isUnderlineText = true
            }
        }

        policySpan.setSpan(privacySpan, 41, 56, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        policySpan.setSpan(tosSpan, 59, policySpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        policyTv.apply {
            text = policySpan
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    private fun setfirstSpan() {
        val wordToSpan = getSpannableStringSecondBold("New here? ", "Create an account")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                replaceFragmentSafely(SignUpFragment(), containerViewId = R.id.loginContainer)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.isUnderlineText = false // set to false to remove underline
            }
        }
        wordToSpan.setSpan(clickableSpan, 9, wordToSpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        createAccTv.apply {
            text = wordToSpan
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }
}
