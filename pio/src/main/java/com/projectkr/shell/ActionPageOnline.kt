package com.projectkr.shell

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.omarea.common.ui.ProgressBarDialog
import com.omarea.krscript.WebViewInjector
import com.omarea.krscript.ui.FileChooserRender
import kotlinx.android.synthetic.main.activity_action_page_online.*


class ActionPageOnline : AppCompatActivity() {
    private val progressBarDialog = ProgressBarDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_page_online)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        setTitle(R.string.app_name)
        Log.e("_openPage", "Online---")

        // 显示返回按钮
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener({ _ ->
            finish()
        })

        /*
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

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }
        val actionBar = supportActionBar
        actionBar!!.hide()

        // 读取intent里的参数
        val intent = this.intent
        if (intent.extras != null) {
            val extras = intent.extras
            if (extras != null) {
                if (extras.containsKey("title")) {
                    title = extras.getString("title")!!
                }
                if (extras.containsKey("config")) {
                    initWebview(extras.getString("config"))
                }
            }
        }
    }

    private fun initWebview(url: String?) {
        kr_online_webview.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                DialogHelper.animDialog(
                        AlertDialog.Builder(this@ActionPageOnline)
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
                        AlertDialog.Builder(this@ActionPageOnline)
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
        }

        kr_online_webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBarDialog.hideDialog()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBarDialog.showDialog(getString(R.string.please_wait))
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                try{
                    val requestUrl = request?.url
                    if(requestUrl != null && requestUrl.scheme?.startsWith("http") != true){
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl.toString()));
                        startActivity(intent);
                        return true;
                    } else {
                        return super.shouldOverrideUrlLoading(view, request);
                    }
                } catch (e:Exception){
                    return super.shouldOverrideUrlLoading(view, request);
                }
            }
        }

        kr_online_webview.loadUrl(url)

        WebViewInjector(kr_online_webview,
                object : FileChooserRender.FileChooserInterface {
                    override fun openFileChooser(fileSelectedInterface: FileChooserRender.FileSelectedInterface) : Boolean {
                        return chooseFilePath(fileSelectedInterface)
                    }
                }).inject()
    }

    private var fileSelectedInterface: FileChooserRender.FileSelectedInterface? = null
    private val ACTION_FILE_PATH_CHOOSER = 65400
    private fun chooseFilePath(fileSelectedInterface: FileChooserRender.FileSelectedInterface): Boolean {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2);
            Toast.makeText(this, getString(R.string.kr_write_external_storage), Toast.LENGTH_LONG).show()
            return false
        } else {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*")
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, ACTION_FILE_PATH_CHOOSER);
                this.fileSelectedInterface = fileSelectedInterface
                return true;
            } catch (ex: java.lang.Exception) {
                return false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACTION_FILE_PATH_CHOOSER) {
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (fileSelectedInterface != null) {
                if (result != null) {
                    val absPath = getPath(result)
                    fileSelectedInterface?.onFileSelected(absPath)
                } else {
                    fileSelectedInterface?.onFileSelected(null)
                }
            }
            this.fileSelectedInterface = null
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getPath(uri: Uri): String? {
        try {
            return FilePathResolver().getPath(this, uri)
        } catch (ex: java.lang.Exception) {
            return null
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && kr_online_webview.canGoBack()) {
            kr_online_webview.goBack()
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
