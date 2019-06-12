package com.projectkr.shell

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import com.omarea.common.shell.KeepShellPublic
import com.omarea.common.ui.DialogHelper
import com.omarea.common.ui.ProgressBarDialog
import com.omarea.krscript.config.PageConfigReader
import com.omarea.krscript.config.PageListReader
import com.omarea.krscript.model.PageClickHandler
import com.omarea.krscript.model.PageInfo
import com.omarea.vtools.FloatMonitor
import com.projectkr.shell.ui.TabIconHelper
import com.projectkr.shell.utils.CpuFrequencyUtils
import com.projectkr.shell.utils.GpuUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_monitor.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    val progressBarDialog = ProgressBarDialog(this)
    private var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!KeepShellPublic.checkRoot()) {
            DialogHelper.animDialog(AlertDialog.Builder(this)
                    .setTitle(getString(R.string.need_root_permissions))
                    .setMessage(getString(R.string.need_root_permissions_desc))
                    .setCancelable(false)
                    .setPositiveButton(R.string.btn_confirm, { _, _ ->
                        System.exit(0)
                    }))
            return
        }

        //supportActionBar!!.elevation = 0f
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        setTitle(R.string.app_name)

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
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //getWindow().setNavigationBarColor(Color.WHITE);

        main_tabhost.setup()

        val tabIconHelper = TabIconHelper(main_tabhost, this)
        if (getString(R.string.framework_home_allow) == "true")
            tabIconHelper.newTabSpec(getString(R.string.tab_home), getDrawable(R.drawable.tab_home)!!, R.id.main_tabhost_cpu)
        main_tabhost.setOnTabChangedListener {
            tabIconHelper.updateHighlight()
        }

        progressBarDialog.showDialog(getString(R.string.please_wait))
        Thread(Runnable {
            val pages= PageListReader(this.applicationContext).readPageList(getString(R.string.framework_page_config));
            val favorites = PageConfigReader(this.applicationContext).readConfigXml(getString(R.string.framework_favorites_config))
            handler.post {
                progressBarDialog.hideDialog()
                list_pages.setListData(pages, object : PageClickHandler {
                    override fun openPage(title: String, config: String) { _openPage(title, config) }
                    override fun openPage(pageInfo: PageInfo) { _openPage(pageInfo.title, pageInfo.pageConfigPath) }
                })
                list_favorites.setListData(favorites)

                if (list_favorites.count > 0){
                    tabIconHelper.newTabSpec(getString(R.string.tab_favorites), getDrawable(R.drawable.tab_favorites)!!, R.id.main_tabhost_2)
                }
                if (list_pages.count > 0) {
                    tabIconHelper.newTabSpec(getString(R.string.tab_pages), getDrawable(R.drawable.tab_pages)!!, R.id.main_tabhost_3)
                }
            }
        }).start()

        val wm = getBaseContext().getSystemService(Context.WINDOW_SERVICE) as (WindowManager)
        val display = wm.getDefaultDisplay();
        val size = Point()
        display.getSize(size);
        val max = if (size.x > size.y) size.x else size.y
        if (max < 2160 && getDensity() > 440) {
            home_title_sum.visibility = View.GONE
            home_title_mem.visibility = View.GONE
            // home_title_cores.visibility = View.GONE
        }
    }

    fun _openPage(pageInfo: PageInfo) {
        _openPage(pageInfo.title, pageInfo.pageConfigPath)
    }

    fun _openPage(title:String, config:String) {
        try {
            val intent = Intent(this, ActionPage::class.java)
            intent.putExtra("title", title)
            intent.putExtra("config", config)
            startActivity(intent)
        } catch (ex: java.lang.Exception) {
            Log.e("_openPage", "" + ex.message)
        }
    }

    private fun getDensity(): Int {
        val dm = DisplayMetrics()
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.densityDpi;
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
                        Toast.makeText(applicationContext, getString(R.string.permission_float), Toast.LENGTH_LONG).show();
                    }
                } else {
                    FloatMonitor(this).showPopupWindow()
                    Toast.makeText(this, getString(R.string.float_monitor_tips), Toast.LENGTH_LONG).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private var coreCount = -1;
    private var activityManager: ActivityManager? = null

    private var minFreqs = HashMap<Int, String>()
    private var maxFreqs = HashMap<Int, String>()
    private var myHandler = Handler()
    private var updateTick = 0;
    private var timer: Timer? = null
    fun format1(value: Double): String {

        var bd = BigDecimal(value)
        bd = bd.setScale(1, RoundingMode.HALF_UP)
        return bd.toString()
    }

    private fun startTimer() {
        if (main_tabhost.currentTab == 0) {
            maxFreqs.clear()
            minFreqs.clear()

            stopTimer()
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    updateInfo()
                }
            }, 0, 1500)
            updateRamInfo()
        }
    }

    override fun onResume() {
        super.onResume()
        startTimer()
    }

    override fun onPause() {
        stopTimer()
        super.onPause()
    }

    private fun updateRamInfo() {
        try {
            val info = ActivityManager.MemoryInfo()
            if (activityManager == null) {
                activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            }
            activityManager!!.getMemoryInfo(info)
            val totalMem = (info.totalMem / 1024 / 1024f).toInt()
            val availMem = (info.availMem / 1024 / 1024f).toInt()
            home_raminfo_text.text = "${((totalMem - availMem) * 100 / totalMem)}% (${totalMem / 1024 + 1}GB)"
            home_raminfo.setData(totalMem.toFloat(), availMem.toFloat())
            val swapInfo = KeepShellPublic.doCmdSync("free -m | grep Swap")
            if (swapInfo.contains("Swap")) {
                try {
                    val swapInfos = swapInfo.substring(swapInfo.indexOf(" "), swapInfo.lastIndexOf(" ")).trim()
                    if (Regex("[\\d]{1,}[\\s]{1,}[\\d]{1,}").matches(swapInfos)) {
                        val total = swapInfos.substring(0, swapInfos.indexOf(" ")).trim().toInt()
                        val use = swapInfos.substring(swapInfos.indexOf(" ")).trim().toInt()
                        val free = total - use
                        home_swapstate_chat.setData(total.toFloat(), free.toFloat())
                        if (total > 99) {
                            home_zramsize.text = "${(use * 100.0 / total).toInt().toString()}% (${format1(total / 1024.0)}GB)"
                        } else {
                            home_zramsize.text = "${(use * 100.0 / total).toInt().toString()}% (${total}MB)"
                        }
                    }
                } catch (ex: java.lang.Exception) {
                }
                // home_swapstate.text = swapInfo.substring(swapInfo.indexOf(" "), swapInfo.lastIndexOf(" ")).trim()
            } else {
            }
        } catch (ex: Exception) {
        }
    }

    private fun updateInfo() {
        if (coreCount < 1) {
            coreCount = CpuFrequencyUtils.getCoreCount()
            myHandler.post {
                try {
                    cpu_core_count.text = coreCount.toString() + " " + getString(R.string.core)
                } catch (ex: Exception) {
                }
            }
        }
        val cores = ArrayList<CpuCoreInfo>()
        val loads = CpuFrequencyUtils.getCpuLoad()
        for (coreIndex in 0 until coreCount) {
            val core = CpuCoreInfo()

            core.currentFreq = CpuFrequencyUtils.getCurrentFrequency("cpu$coreIndex")
            if (!maxFreqs.containsKey(coreIndex) || (core.currentFreq != "" && maxFreqs.get(coreIndex).isNullOrEmpty())) {
                maxFreqs.put(coreIndex, CpuFrequencyUtils.getCurrentMaxFrequency("cpu" + coreIndex))
            }
            core.maxFreq = maxFreqs.get(coreIndex)

            if (!minFreqs.containsKey(coreIndex) || (core.currentFreq != "" && minFreqs.get(coreIndex).isNullOrEmpty())) {
                minFreqs.put(coreIndex, CpuFrequencyUtils.getCurrentMinFrequency("cpu" + coreIndex))
            }
            core.minFreq = minFreqs.get(coreIndex)

            if (loads.containsKey(coreIndex)) {
                core.loadRatio = loads.get(coreIndex)!!
            }
            cores.add(core)
        }
        val gpuFreq = GpuUtils.getGpuFreq() + getString(R.string.mhz)
        val gpuLoad = GpuUtils.getGpuLoad()
        myHandler.post {
            try {
                home_gpu_freq.text = gpuFreq
                home_gpu_load.text = getString(R.string.load_ratio) + gpuLoad + "%"
                if (gpuLoad > -1) {
                    home_gpu_chat.setData(100.toFloat(), (100 - gpuLoad).toFloat())
                }
                if (loads.containsKey(-1)) {
                    cpu_core_total_load.text = getString(R.string.load_ratio) + loads.get(-1)!!.toInt().toString() + "%"
                    home_cpu_chat.setData(100.toFloat(), (100 - loads.get(-1)!!.toInt()).toFloat())
                }
                if (cpu_core_list.adapter == null) {
                    cpu_core_list.adapter = AdapterCpuCores(this, cores)
                } else {
                    (cpu_core_list.adapter as AdapterCpuCores).setData(cores)
                }
            } catch (ex: Exception) {

            }
        }
        updateTick++
        if (updateTick > 5) {
            updateTick = 0
            minFreqs.clear()
            maxFreqs.clear()
        }
    }

    private fun stopTimer() {
        if (this.timer != null) {
            timer!!.cancel()
            timer = null
        }
    }
}
