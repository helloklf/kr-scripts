package com.projectkr.shell

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.TabHost
import android.widget.Toast
import com.omarea.scripts.action.ActionListConfig
import com.omarea.shell.Files
import com.omarea.shell.KeepShellPublic
import com.omarea.ui.AdapterCpuCores
import com.omarea.ui.ProgressBarDialog
import com.projectkr.shell.action.ActionConfigReader
import com.projectkr.shell.switchs.SwitchConfigReader
import com.projectkr.shell.switchs.SwitchListConfig
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.collections.HashMap
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    internal var handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!KeepShellPublic.checkRoot()) {
            AlertDialog.Builder(this)
                    .setTitle("需要ROOT权限")
                    .setMessage("请在Magisk或SuperSU等ROOT权限管理器中，设置本应用允许使用ROOT权限！")
                    .setCancelable(false)
                    .setPositiveButton("确定", {
                        _, _ ->
                        System.exit(0)
                    })
                    .create()
                    .show()
            return
        }

        try {
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
        } catch (ex: Exception) {
        }
        main_tabhost.setup()
        main_tabhost.setOnTabChangedListener {
            if ((main_tabhost).currentTab == 2) {
                startTimer()
            } else {
                stopTimer()
            }
        }

        val mainActivity = this
        val progressBarDialog = ProgressBarDialog(mainActivity)
        progressBarDialog.showDialog("请稍等")
        Thread(Runnable {
            val actionInfos = ActionConfigReader.readActionConfigXml(mainActivity)
            val switchInfos = SwitchConfigReader.readActionConfigXml(mainActivity)
            handler.post {
                ActionListConfig(mainActivity).setListData(actionInfos)
                main_tabhost.addTab(main_tabhost.newTabSpec("tab0").setContent(R.id.main_tabhost_tab0).setIndicator("", getDrawable(R.drawable.shell)))
                if (switchInfos != null && switchInfos.size != 0) {
                    SwitchListConfig(mainActivity).setListData(switchInfos)
                }
                main_tabhost.addTab(main_tabhost.newTabSpec("tab1").setContent(R.id.main_tabhost_tab1).setIndicator("", getDrawable(R.drawable.switchs)))
                main_tabhost.addTab(main_tabhost.newTabSpec("tab2").setContent(R.id.main_tabhost_tab2).setIndicator("", getDrawable(R.drawable.cpu)))
                progressBarDialog.hideDialog()
            }
        }).start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_menu_info -> {
                val layoutInflater = LayoutInflater.from(this)
                AlertDialog.Builder(this)
                        //.setTitle("关于")
                        //.setMessage("以下脚本，均出自Kr-Yaodao Rom")
                        .setView(layoutInflater.inflate(R.layout.dialog_about, null))
                        .create()
                        .show()
            }
            R.id.option_menu_reboot -> {
                AlertDialog.Builder(this)
                        .setTitle(R.string.reboot_confirm)
                        .setPositiveButton(R.string.yes) { dialog, which ->
                            try {
                                Runtime.getRuntime().exec("reboot")
                            } catch (e: IOException) {
                                Toast.makeText(applicationContext, R.string.reboot_fail, Toast.LENGTH_SHORT).show()
                                e.printStackTrace()
                            }
                        }
                        .setNegativeButton(R.string.no) { dialog, which -> }
                        .create()
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
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

    private fun startTimer () {
        if (main_tabhost.currentTab == 2) {
            stopTimer()
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    updateInfo()
                }
            }, 0, 1000)
            updateRamInfo()
        }
    }

    override fun onResume() {
        super.onResume()
        startTimer()
    }

    @SuppressLint("SetTextI18n")
    private fun updateRamInfo() {
        try {
            val info = ActivityManager.MemoryInfo()
            if (activityManager == null) {
                activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            }
            activityManager!!.getMemoryInfo(info)
            val totalMem = (info.totalMem / 1024 / 1024f).toInt()
            val availMem = (info.availMem / 1024 / 1024f).toInt()
            home_raminfo_text.text = "${format1(availMem / 1024.0)} / ${totalMem / 1024 + 1} GB"
            home_ramstate.text = ((totalMem - availMem) * 100 / totalMem).toString() + "%"
            home_raminfo.setData(totalMem.toFloat(), availMem.toFloat())
            val sdFree = Files.getDirFreeSizeMB(Environment.getDataDirectory().absolutePath)
            if (sdFree > 8192) {
                datafree.text = "Data：" + sdFree / 1024 + " GB"
            } else {
                datafree.text = "Data：" + sdFree + " MB"
            }
            val sdSize = Files.getDirFreeSizeMB(Environment.getExternalStorageDirectory().absolutePath)
            if (sdSize > 8292) {
                sdfree.text = "SDCard：" + sdSize / 1024 + " GB"
            } else {
                sdfree.text = "SDCard：" + sdSize + " MB"
            }
            val swapInfo = KeepShellPublic.doCmdSync("free -m | grep Swap")
            if (swapInfo.contains("Swap")) {
                try {
                    val swapInfos = swapInfo.substring(swapInfo.indexOf(" "), swapInfo.lastIndexOf(" ")).trim()
                    if (Regex("[\\d]{1,}[\\s]{1,}[\\d]{1,}").matches(swapInfos)) {
                        val total = swapInfos.substring(0, swapInfos.indexOf(" ")).trim().toInt()
                        val use = swapInfos.substring(swapInfos.indexOf(" ")).trim().toInt()
                        val free = total - use
                        home_swapstate_chat.setData(total.toFloat(), free.toFloat())
                        home_swapstate.text = (use * 100.0 / total).toInt().toString() + "%"
                        if (total > 99) {
                            home_zramsize.text = "${format1(free / 1024.0)} / ${format1(total / 1024.0)} GB"
                        } else {
                            home_zramsize.text = "${free}/${total}MB"
                        }
                    }
                } catch (ex: java.lang.Exception) {
                    home_swapstate.text = ""
                }
                // home_swapstate.text = swapInfo.substring(swapInfo.indexOf(" "), swapInfo.lastIndexOf(" ")).trim()
            } else {
                home_swapstate.text = ""
            }
        } catch (ex: Exception) {
        }
    }
    override fun onPause() {
        stopTimer()
        super.onPause()
    }
    private fun stopTimer() {
        if (this.timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateInfo() {
        if (coreCount < 1) {
            coreCount = CpuFrequencyUtils.getCoreCount()
            myHandler.post {
                try {
                    cpu_core_count.text = "核心数：$coreCount"
                } catch (ex: Exception) {}
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
        myHandler.post {
            try {
                if (loads.containsKey(-1)) {
                    cpu_core_total_load.text = "负载：" + loads.get(-1)!!.toInt().toString() + "%"
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

}
