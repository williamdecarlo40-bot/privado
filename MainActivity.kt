package br.com.doramasdramaflix.apk

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout

class MainActivity : Activity() {

    // Endereço do site. Se um dia mudar, é só trocar aqui.
    private val START_URL = "https://doramasdramaflix.com.br/"

    // Endereços que abrem dentro do app. O resto (WhatsApp, e-mail, outros sites) abre fora.
    private val HOSTS = listOf(
        "doramasdramaflix.com.br",
        "mercadopago.com", "mercadopago.com.br", "mercadolibre.com",
        "accounts.google.com"
    )

    private lateinit var root: FrameLayout
    private lateinit var web: WebView
    private var customView: View? = null
    private var customCallback: WebChromeClient.CustomViewCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bloqueia gravação e captura de tela dentro do app
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        root = FrameLayout(this)
        root.setBackgroundColor(Color.BLACK)
        web = WebView(this)
        web.setBackgroundColor(Color.BLACK)
        root.addView(
            web,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        setContentView(root)

        val s = web.settings
        s.javaScriptEnabled = true
        s.domStorageEnabled = true
        s.mediaPlaybackRequiresUserGesture = false
        s.cacheMode = WebSettings.LOAD_DEFAULT
        s.userAgentString = s.userAgentString + " DoramasApp/1.0"

        val cm = CookieManager.getInstance()
        cm.setAcceptCookie(true)
        cm.setAcceptThirdPartyCookies(web, true)

        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                val scheme = uri.scheme ?: ""
                val host = uri.host ?: ""
                if ((scheme == "http" || scheme == "https") && HOSTS.any { host == it || host.endsWith(".$it") }) {
                    return false // abre dentro do app
                }
                openOutside(uri)
                return true
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) {
                    view.loadData(
                        "<html><body style='background:#000;color:#fff;font-family:sans-serif;text-align:center;padding-top:30vh'>" +
                            "<h2>Sem conexão</h2><p>Verifique sua internet.</p>" +
                            "<p><a style='color:#ff4d8d' href='$START_URL'>Tentar de novo</a></p></body></html>",
                        "text/html; charset=utf-8", "UTF-8"
                    )
                }
            }
        }

        web.webChromeClient = object : WebChromeClient() {
            // Vídeo em tela cheia
            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                if (customView != null) {
                    callback.onCustomViewHidden()
                    return
                }
                customView = view
                customCallback = callback
                web.visibility = View.GONE
                root.addView(view, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                hideSystemBars(true)
            }

            override fun onHideCustomView() {
                customView?.let { root.removeView(it) }
                customView = null
                customCallback?.onCustomViewHidden()
                customCallback = null
                web.visibility = View.VISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                hideSystemBars(false)
            }
        }

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState)
        } else {
            web.loadUrl(START_URL)
        }
    }

    private fun openOutside(uri: Uri) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            // sem app para abrir: ignora
        }
    }

    @Suppress("DEPRECATION")
    private fun hideSystemBars(hide: Boolean) {
        window.decorView.systemUiVisibility = if (hide) {
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        when {
            customView != null -> web.webChromeClient?.onHideCustomView()
            web.canGoBack() -> web.goBack()
            else -> super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        web.saveState(outState)
    }

    override fun onPause() {
        super.onPause()
        web.onPause()
    }

    override fun onResume() {
        super.onResume()
        web.onResume()
    }

    override fun onDestroy() {
        web.destroy()
        super.onDestroy()
    }
}
