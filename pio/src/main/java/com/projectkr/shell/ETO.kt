package com.projectkr.shell

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.omarea.common.shared.FilePathResolver
import com.omarea.common.ui.DialogHelper
import com.omarea.krscript.WebViewInjector
import com.omarea.krscript.ui.FileChooserRender
import kotlinx.android.synthetic.main.activity_action_page_online.*
import org.json.JSONObject
import java.nio.charset.Charset


class ETO : AppCompatActivity() {
    private val WEBVIEW_FILE_CHOOSER = 65535;
    private val WEBVIEW_FILE_PATH_CHOOSER = 65530;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_page_online)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        setTitle(R.string.eto_app_name)

        /*
        // 显示返回按钮
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener({ _ ->
            finish()
        })

        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        */

        val decorView = window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
        val actionBar = supportActionBar
        actionBar!!.hide()

        // initWebview("http://10.20.11.15:8002/#/")
        initWebview("file:///android_asset/ui/index.html")
    }

    @SuppressLint("JavascriptInterface")
    private fun initWebview(url: String?) {
        kr_online_webview.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                DialogHelper.animDialog(
                        AlertDialog.Builder(this@ETO)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton(R.string.btn_confirm, { _, _ -> })
                                .setOnDismissListener {
                                    result?.confirm()
                                }
                                .create()
                )
                return true // super.onJsAlert(view, url, message, result)
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                DialogHelper.animDialog(
                        AlertDialog.Builder(this@ETO)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton(R.string.btn_confirm, { _, _ ->
                                    result?.confirm()
                                })
                                .setNeutralButton(R.string.btn_cancel, { _, _ ->
                                    result?.cancel()
                                })
                                .create()
                )
                return true // super.onJsConfirm(view, url, message, result)
            }

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                try {
                    val intent = Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                    // intent.setType("file/*.img")
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, WEBVIEW_FILE_CHOOSER);
                    webviewFileCallback = filePathCallback
                    return true;
                } catch (ex: java.lang.Exception) {
                    Log.e("onShowFileChooser", "onShowFileChooser" + ex.message)
                    return false
                }
            }
        }

        kr_online_webview.loadUrl(url)

        WebViewInjector(kr_online_webview, object : FileChooserRender.FileChooserInterface {
            override fun openFileChooser(fileSelectedInterface: FileChooserRender.FileSelectedInterface): Boolean {
                return false
            }
        }).inject()
        kr_online_webview.addJavascriptInterface(ExtendedInterface(this, kr_online_webview), "ExtendedInterface")
    }

    private var webviewFileCallback: ValueCallback<Array<Uri>>? = null
    private var fileCallbackInterface: FileCallbackInterface? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WEBVIEW_FILE_CHOOSER) {
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (webviewFileCallback != null) {
                if (result != null) {
                    webviewFileCallback?.onReceiveValue(arrayOf(result))
                } else {
                    webviewFileCallback?.onReceiveValue(null)
                }
                webviewFileCallback = null
            }
        } else if (requestCode == WEBVIEW_FILE_PATH_CHOOSER) {
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (fileCallbackInterface != null) {
                if (result != null) {
                    val absPath = getPath(result)
                    fileCallbackInterface?.onFileChoose(absPath)
                } else {
                    fileCallbackInterface?.onFileChoose(null)
                }
            }
            this.fileCallbackInterface = null
        }
    }

    private fun chooseFilePath(fileCallbackInterface: FileCallbackInterface): Boolean {
        if (this.fileCallbackInterface == null) {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                // intent.setType("file/*.img")
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, WEBVIEW_FILE_PATH_CHOOSER);
                this.fileCallbackInterface = fileCallbackInterface
                return true;
            } catch (ex: java.lang.Exception) {
                return false
            }
        } else {
            return false
        }
    }

    private fun getPath(uri: Uri): String? {
        try {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), 2);
                Toast.makeText(this, getString(R.string.write_external_storage), Toast.LENGTH_LONG).show()
                return null;
            } else {
                return FilePathResolver().getPath(this, uri)
            }
        } catch (ex: java.lang.Exception) {
            return null
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (kr_online_webview.canGoBack()) {
            kr_online_webview.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
    }

    interface FileCallbackInterface {
        fun onFileChoose(path: String?)
    }

    private class ExtendedInterface(private var eto: ETO, private var webView: WebView) {
        @JavascriptInterface
        public fun currentRefreshRate(): Int {
            val display = eto.windowManager.getDefaultDisplay()
            val refreshRate = display.getRefreshRate()
            return Math.round(refreshRate)
        }

        @JavascriptInterface
        public fun deviceModel(): String? {
            return Build.PRODUCT
        }

        @JavascriptInterface
        public fun deviceName(): String? {
            return Build.MODEL
        }

        @JavascriptInterface
        public fun sdkInt(): Int {
            return Build.VERSION.SDK_INT
        }

        @JavascriptInterface
        public fun getOptions(): String? {
            try {
                val reader = eto.assets.open("eto/" + deviceModel()!! + "/sdk${sdkInt()}/config.json")
                val jsonStr = String(reader.readBytes(), Charset.defaultCharset())
                Log.d("getOptions", "" + jsonStr)
                return jsonStr
            } catch (ex: Exception) {
                return null
            }

        }

        @JavascriptInterface
        public fun chooseFile(callbackFunction: String) {
            eto.chooseFilePath(object : FileCallbackInterface {
                override fun onFileChoose(path: String?) {
                    Log.d("onFileChoose", "" + path)
                    if (path == null) {
                        webView.post {
                            webView.evaluateJavascript("$callbackFunction(null)", ValueCallback { })
                        }
                    } else {
                        val result = JSONObject()
                        result.put("absPath", path)
                        webView.evaluateJavascript("$callbackFunction(${result})", ValueCallback { })
                    }
                }
            })
        }
    }
}
