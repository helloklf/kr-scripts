package com.projectkr.shell

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.omarea.common.shared.FilePathResolver
import com.omarea.common.shell.KeepShellPublic
import com.omarea.common.ui.DialogHelper
import com.omarea.common.ui.ProgressBarDialog
import com.omarea.krscript.config.PageConfigReader
import com.omarea.krscript.model.ConfigItemBase
import com.omarea.krscript.model.KrScriptActionHandler
import com.omarea.krscript.model.PageInfo
import com.omarea.krscript.ui.ActionListFragment
import com.omarea.krscript.ui.FileChooserRender
import com.omarea.vtools.FloatMonitor
import com.projectkr.shell.ui.TabIconHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val progressBarDialog = ProgressBarDialog(this)
    private var handler = Handler()
    private var krScriptConfig = KrScriptConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeModeState.switchTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //supportActionBar!!.elevation = 0f
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        setTitle(R.string.app_name)

        krScriptConfig = KrScriptConfig()


        main_tabhost.setup()
        val tabIconHelper = TabIconHelper(main_tabhost, this)
        if (krScriptConfig.allowHomePage) {
            tabIconHelper.newTabSpec(getString(R.string.tab_home), getDrawable(R.drawable.tab_home)!!, R.id.main_tabhost_cpu)
        } else {
            main_tabhost_cpu.visibility = View.GONE
        }
        main_tabhost.setOnTabChangedListener {
            tabIconHelper.updateHighlight()
        }

        progressBarDialog.showDialog(getString(R.string.please_wait))
        Thread(Runnable {
            val page2Config = krScriptConfig.pageListConfig
            val favoritesConfig = krScriptConfig.favoriteConfig

            val pages = getItems(page2Config)
            val favorites = getItems(favoritesConfig)
            handler.post {
                progressBarDialog.hideDialog()

                if (favorites != null && favorites.size > 0) {
                    updateFavoritesTab(favorites, favoritesConfig)
                    tabIconHelper.newTabSpec(getString(R.string.tab_favorites), getDrawable(R.drawable.tab_favorites)!!, R.id.main_tabhost_2)
                } else {
                    main_tabhost_2.visibility = View.GONE
                }

                if (pages != null && pages.size > 0) {
                    updateMoreTab(pages, page2Config)
                    tabIconHelper.newTabSpec(getString(R.string.tab_pages), getDrawable(R.drawable.tab_pages)!!, R.id.main_tabhost_3)
                } else {
                    main_tabhost_3.visibility = View.GONE
                }
            }
        }).start()

        val home = FragmentHome()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.main_tabhost_cpu, home)
        transaction.commitAllowingStateLoss()
    }

    private fun getItems(pageInfo: PageInfo): ArrayList<ConfigItemBase>? {
        var items: ArrayList<ConfigItemBase>? = null

        if (pageInfo.pageConfigSh.isNotEmpty()) {
            items = PageConfigSh(this, pageInfo.pageConfigSh).execute()
        }
        if (items == null && pageInfo.pageConfigPath.isNotEmpty()) {
            items = PageConfigReader(this.applicationContext, pageInfo.pageConfigPath).readConfigXml()
        }

        return items
    }

    private fun updateFavoritesTab(items: ArrayList<ConfigItemBase>, pageInfo: PageInfo) {
        val favoritesFragment = ActionListFragment.create(items, getKrScriptActionHandler(pageInfo, true), null, ThemeModeState.getThemeMode())
        supportFragmentManager.beginTransaction().replace(R.id.list_favorites, favoritesFragment).commitAllowingStateLoss()
    }

    private fun updateMoreTab(items: ArrayList<ConfigItemBase>, pageInfo: PageInfo) {
        val allItemFragment = ActionListFragment.create(items, getKrScriptActionHandler(pageInfo, false), null, ThemeModeState.getThemeMode())
        supportFragmentManager.beginTransaction().replace(R.id.list_pages, allItemFragment).commitAllowingStateLoss()
    }

    private fun reloadFavoritesTab() {
        val favoritesConfig = krScriptConfig.favoriteConfig
        val favorites = getItems(favoritesConfig)
        favorites?.run {
            updateFavoritesTab(this, favoritesConfig)
        }
    }

    private fun reloadMoreTab() {
        val page2Config = krScriptConfig.pageListConfig

        val pages = getItems(page2Config)
        pages?.run {
            updateMoreTab(this, page2Config)
        }
    }

    private fun getKrScriptActionHandler(pageInfo: PageInfo, isFavoritesTab: Boolean): KrScriptActionHandler {
        return object : KrScriptActionHandler {
            override fun onActionCompleted(configItemBase: ConfigItemBase) {
                if (isFavoritesTab) {
                    reloadFavoritesTab()
                } else {
                    reloadMoreTab()
                }
            }

            override fun addToFavorites(configItemBase: ConfigItemBase, addToFavoritesHandler: KrScriptActionHandler.AddToFavoritesHandler) {
                val intent = Intent()

                intent.component = ComponentName(this@MainActivity.applicationContext, ActionPage::class.java)
                intent.putExtra("title", "" + title)
                intent.putExtra("beforeRead", "")
                intent.putExtra("config", pageInfo.pageConfigPath)
                intent.putExtra("pageConfigSh", pageInfo.pageConfigSh)
                intent.putExtra("afterRead", "")
                intent.putExtra("loadSuccess", "")
                intent.putExtra("loadFail", "")
                intent.putExtra("autoRunItemId", configItemBase.key)

                addToFavoritesHandler.onAddToFavorites(configItemBase, intent)
            }

            override fun onSubPageClick(pageInfo: PageInfo) {
                _openPage(pageInfo)
            }

            override fun openFileChooser(fileSelectedInterface: FileChooserRender.FileSelectedInterface): Boolean {
                return chooseFilePath(fileSelectedInterface)
            }
        }
    }

    private var fileSelectedInterface: FileChooserRender.FileSelectedInterface? = null
    private val ACTION_FILE_PATH_CHOOSER = 65400
    private fun chooseFilePath(fileSelectedInterface: FileChooserRender.FileSelectedInterface): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.kr_write_external_storage), Toast.LENGTH_LONG).show()
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2);
            return false
        } else {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*")
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, ACTION_FILE_PATH_CHOOSER);
                this.fileSelectedInterface = fileSelectedInterface
                return true;
            } catch (ex: Exception) {
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
        } catch (ex: Exception) {
            return null
        }
    }

    fun _openPage(pageInfo: PageInfo) {
        OpenPageHelper(this).openPage(pageInfo)
    }

    private fun getDensity(): Int {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.densityDpi
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_menu_info -> {
                val layoutInflater = LayoutInflater.from(this)
                DialogHelper.animDialog(
                        AlertDialog.Builder(this).setView(layoutInflater.inflate(R.layout.dialog_about, null))
                )
            }
            R.id.option_menu_reboot -> {
                DialogHelper.animDialog(AlertDialog.Builder(this)
                        .setTitle(R.string.reboot_confirm)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            KeepShellPublic.doCmdSync(getString(R.string.command_reboot))
                        }
                        .setNegativeButton(R.string.no) { _, _ -> })
            }
            R.id.action_graph -> {
                if (FloatMonitor.isShown == true) {
                    FloatMonitor(this).hidePopupWindow()
                    return false
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(this)) {
                        FloatMonitor(this).showPopupWindow()
                        Toast.makeText(this, getString(R.string.float_monitor_tips), Toast.LENGTH_LONG).show()
                    } else {
                        //若没有权限，提示获取
                        //val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        //startActivity(intent);
                        val intent = Intent()
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        intent.data = Uri.fromParts("package", this.packageName, null)

                        Toast.makeText(applicationContext, getString(R.string.permission_float), Toast.LENGTH_LONG).show()

                        try {
                            startActivity(intent)
                        } catch (ex: Exception) {
                        }
                    }
                } else {
                    FloatMonitor(this).showPopupWindow()
                    Toast.makeText(this, getString(R.string.float_monitor_tips), Toast.LENGTH_LONG).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
