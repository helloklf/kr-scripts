package com.projectkr.shell

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.Toast
import com.omarea.common.shared.FilePathResolver
import com.omarea.common.ui.ProgressBarDialog
import com.omarea.krscript.TryOpenActivity
import com.omarea.krscript.config.PageConfigReader
import com.omarea.krscript.executor.ScriptEnvironmen
import com.omarea.krscript.model.*
import com.omarea.krscript.ui.ActionListFragment
import com.omarea.krscript.ui.FileChooserRender
import com.projectkr.shell.permissions.CheckRootStatus
import java.lang.Exception


class ActionPage : AppCompatActivity() {
    private val progressBarDialog = ProgressBarDialog(this)
    private var actionsLoaded = false
    private var handler = Handler()
    private var pageConfig: String = ""
    private var parentDir: String = ""
    private var autoRun: String = ""
    private var pageTitle = ""

    // 读取页面配置前
    private var beforeRead = ""
    // 读取页面配置后
    private var afterRead = ""

    private var loadSuccess = ""
    private var loadFail = ""

    // 页面配置脚本
    private var pageConfigSh = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        // 如果应用还没启动，就直接打开了actionPage(通常是PIO的快捷方式)，先跳转到启动页面
        if (!ScriptEnvironmen.isInited()) {
            val initIntent = Intent(this.applicationContext, SplashActivity::class.java)
            initIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            initIntent.putExtras(this.intent)
            initIntent.putExtra("JumpActionPage", true)
            startActivity(initIntent)
            // overridePendingTransition(0, 0)

            finish()
            return
        }

        ThemeModeState.switchTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_page)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        setTitle(R.string.app_name)

        // 显示返回按钮
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // 读取intent里的参数
        val intent = this.intent
        if (intent.extras != null) {
            val extras = intent.extras
            if (extras != null) {
                if (extras.containsKey("activity")) {
                    if(TryOpenActivity(this, extras.getString("activity")!!).tryOpen()) {
                        finish()
                        return
                    }
                }
                if (extras.containsKey("onlineHtmlPage")) {
                    try {
                        val page = Intent(this, ActionPageOnline::class.java)
                        page.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        page.putExtra("config", extras.getString("onlineHtmlPage"))
                        startActivity(page)
                    } catch (ex: Exception){}
                }

                if (extras.containsKey("title")) {
                    pageTitle = extras.getString("title")!!
                    title = pageTitle
                }
                if (extras.containsKey("config")) {
                    pageConfig = extras.getString("config")!!
                }
                if (extras.containsKey("config")) {
                    pageConfig = extras.getString("config")!!
                }
                if (extras.containsKey("parentDir")) {
                    parentDir = extras.getString("parentDir")!!
                }
                if (extras.containsKey("pageConfigSh")) {
                    pageConfigSh = extras.getString("pageConfigSh")!!
                }

                if (extras.containsKey("beforeRead")) {
                    beforeRead = extras.getString("beforeRead")!!
                }
                if (extras.containsKey("afterRead")) {
                    afterRead = extras.getString("afterRead")!!
                }
                if (extras.containsKey("loadSuccess")) {
                    loadSuccess = extras.getString("loadSuccess")!!
                }
                if (extras.containsKey("loadFail")) {
                    loadFail = extras.getString("loadFail")!!
                }
                if (extras.containsKey("autoRunItemId")) {
                    autoRun = extras.getString("autoRunItemId")!!
                }
            }
        }

        if (pageConfig.isEmpty() && pageConfigSh.isEmpty()) {
            setResult(2)
            finish()
        }
    }

    private var actionShortClickHandler = object : KrScriptActionHandler {
        override fun onActionCompleted(runnableNode: RunnableNode) {
            if (runnableNode.autoFinish ) {
                finishAndRemoveTask()
            } else if (runnableNode.reloadPage) {
                loadPageConfig()
            }
        }

        override fun addToFavorites(clickableNode: ClickableNode, addToFavoritesHandler: KrScriptActionHandler.AddToFavoritesHandler) {
            val page = if (clickableNode is PageNode) {
                clickableNode
            } else if (clickableNode is RunnableNode) {
                PageNode(parentDir).apply {
                    title = "" + this@ActionPage.title
                    beforeRead = this@ActionPage.beforeRead
                    pageConfigPath = this@ActionPage.pageConfig
                    pageConfigSh = this@ActionPage.pageConfigSh
                    afterRead = this@ActionPage.afterRead
                    loadSuccess = this@ActionPage.loadSuccess
                    loadFail = this@ActionPage.loadFail
                }
            } else {
                return
            }

            val intent = Intent()

            intent.component = ComponentName(this@ActionPage.applicationContext, ActionPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            if (clickableNode is RunnableNode) {
                intent.putExtra("autoRunItemId", clickableNode.key)
            }

            page.run {
                intent.putExtra("title", "" + title)
                if (activity.isNotEmpty()) {
                    intent.putExtra("activity", activity)
                }
                if (onlineHtmlPage.isNotEmpty()) {
                    intent.putExtra("onlineHtmlPage", onlineHtmlPage)
                }
                if (beforeRead.isNotEmpty()) {
                    intent.putExtra("beforeRead", beforeRead)
                }
                if (pageConfigPath.isNotEmpty()) {
                    intent.putExtra("config", pageConfigPath)
                }
                if (pageConfigSh.isNotEmpty()) {
                    intent.putExtra("pageConfigSh", pageConfigSh)
                }
                if (afterRead.isNotEmpty()) {
                    intent.putExtra("afterRead", afterRead)
                }
                if (loadSuccess.isNotEmpty()) {
                    intent.putExtra("loadSuccess", loadSuccess)
                }
                if (loadFail.isNotEmpty()) {
                    intent.putExtra("loadFail", loadFail)
                }
            }

            addToFavoritesHandler.onAddToFavorites(clickableNode, intent)
        }

        override fun onSubPageClick(pageNode: PageNode) {
            _openPage(pageNode)
        }

        override fun openFileChooser(fileSelectedInterface: FileChooserRender.FileSelectedInterface): Boolean {
            return chooseFilePath(fileSelectedInterface)
        }
    }

    private var fileSelectedInterface: FileChooserRender.FileSelectedInterface? = null
    private val ACTION_FILE_PATH_CHOOSER = 65400
    private val ACTION_FILE_PATH_CHOOSER_INNER = 65300

    private fun chooseFilePath(extension: String) {
        try {
            val intent = Intent(this, ActivityFileSelector::class.java)
            intent.putExtra("extension", extension)
            startActivityForResult(intent, ACTION_FILE_PATH_CHOOSER_INNER)
        } catch (ex: Exception) {
            Toast.makeText(this, "启动内置文件选择器失败！", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chooseFilePath(fileSelectedInterface: FileChooserRender.FileSelectedInterface): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), 2);
            Toast.makeText(this, getString(R.string.kr_write_external_storage), Toast.LENGTH_LONG).show()
            return false
        } else {
            return try {
                val suffix = fileSelectedInterface.suffix()
                if (suffix != null && suffix.isNotEmpty()) {
                    chooseFilePath(suffix)
                } else {
                    val intent = Intent(Intent.ACTION_GET_CONTENT);
                    val mimeType = fileSelectedInterface.mimeType()
                    if (mimeType != null) {
                        intent.type = mimeType
                    } else {
                        intent.type = "*/*"
                    }
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, ACTION_FILE_PATH_CHOOSER);
                }
                this.fileSelectedInterface = fileSelectedInterface
                true;
            } catch (ex: java.lang.Exception) {
                false
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
        } else if (requestCode == ACTION_FILE_PATH_CHOOSER_INNER) {
            val absPath = if (data == null || resultCode != Activity.RESULT_OK) null else data.getStringExtra("file")
            fileSelectedInterface?.onFileSelected(absPath)
            this.fileSelectedInterface = null
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getPath(uri: Uri): String? {
        return try {
            FilePathResolver().getPath(this, uri)
        } catch (ex: java.lang.Exception) {
            null
        }
    }

    private fun showDialog(msg: String) {
        handler.post {
            progressBarDialog.showDialog(msg)
        }
    }

    private fun hideDialog() {
        handler.post {
            progressBarDialog.hideDialog()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!actionsLoaded) {
            loadPageConfig()
        }
    }

    private fun loadPageConfig() {
        val activity = this

        Thread(Runnable {
            if (beforeRead.isNotEmpty()) {
                showDialog(getString(R.string.kr_page_before_load))
                ScriptEnvironmen.executeResultRoot(activity, beforeRead)
            }

            showDialog(getString(R.string.kr_page_loading))
            var items: ArrayList<NodeInfoBase>? = null

            if (pageConfigSh.isNotEmpty()) {
                items = PageConfigSh(this, pageConfigSh).execute()
            }

            if (items == null && pageConfig.isNotEmpty()) {
                items = PageConfigReader(this.applicationContext, pageConfig, parentDir).readConfigXml()
            }

            if (afterRead.isNotEmpty()) {
                showDialog(getString(R.string.kr_page_after_load))
                ScriptEnvironmen.executeResultRoot(activity, afterRead)
            }

            if (items != null && items.size != 0) {
                if (loadSuccess.isNotEmpty()) {
                    showDialog(getString(R.string.kr_page_load_success))
                    ScriptEnvironmen.executeResultRoot(activity, loadSuccess)
                }

                handler.post {
                    val autoRunTask = if (actionsLoaded) null else object : AutoRunTask {
                        override val key = autoRun
                        override fun onCompleted(result: Boolean?) {
                            if (result != true) {
                                Toast.makeText(this@ActionPage, getString(R.string.kr_auto_run_item_losted), Toast.LENGTH_SHORT).show()
                            }
                        }
                    };

                    val fragment = ActionListFragment.create(items, actionShortClickHandler, autoRunTask, ThemeModeState.getThemeMode())
                    supportFragmentManager.beginTransaction().replace(R.id.main_list, fragment).commitAllowingStateLoss()
                    hideDialog()
                    actionsLoaded = true
                }
            } else {
                if (loadFail.isNotEmpty()) {
                    showDialog(getString(R.string.kr_page_load_fail))
                    ScriptEnvironmen.executeResultRoot(activity, loadFail)
                    hideDialog()
                }

                handler.post {
                    Toast.makeText(this@ActionPage, getString(R.string.kr_page_load_fail), Toast.LENGTH_SHORT).show()
                }
                hideDialog()
                finish()
            }
        }).start()
    }

    fun _openPage(pageNode: PageNode) {
        OpenPageHelper(this).openPage(pageNode)
    }
}
