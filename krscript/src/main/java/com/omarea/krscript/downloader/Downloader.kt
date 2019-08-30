package com.omarea.krscript.downloader

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
import com.omarea.common.ui.DialogHelper
import com.omarea.krscript.R
import org.json.JSONObject

class Downloader(private var context: Context, private var activity: Activity? = null) {
    companion object {
        private val HISTORY_CONFIG = "kr_downloader"
    }

    fun downloadByBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        activity?.startActivity(intent);
    }

    fun downloadBySystem(url: String, contentDisposition: String?, mimeType: String?): Long? {
        try {
            // 指定下载地址
            val request = DownloadManager.Request(Uri.parse(url))
            // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
            request.allowScanningByMediaScanner()
            // 设置通知的显示类型，下载进行时和完成后显示通知
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            // 设置通知栏的标题，如果不设置，默认使用文件名
            //        request.setTitle("This is title");
            // 设置通知栏的描述
            //        request.setDescription("This is description");
            // 允许在计费流量下下载
            request.setAllowedOverMetered(true)
            // 允许该记录在下载管理界面可见
            request.setVisibleInDownloadsUi(true)
            // 允许漫游时下载
            request.setAllowedOverRoaming(true)
            // 允许下载的网路类型
            // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
            // 设置下载文件保存的路径和文件名
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            //        另外可选一下方法，自定义下载路径
            //        request.setDestinationUri()
            //        request.setDestinationInExternalFilesDir()
            val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            // 添加一个下载任务
            val downloadId = downloadManager.enqueue(request)
            addTaskHisotry(downloadId, url);
            Toast.makeText(context, context.getString(R.string.kr_download_create_success), Toast.LENGTH_SHORT).show()
            // 注册下载完成事件监听
            DownloadCompleteReceiver.autoRegister(context)
            return downloadId
        } catch (ex: Exception) {
            DialogHelper.helpInfo(context, context.getString(R.string.kr_download_create_fail), "" + ex.message)
            return null
        }
    }

    // 保存下载记录
    private fun addTaskHisotry(downloadId: Long, url: String) {
        val historyList = context.getSharedPreferences(HISTORY_CONFIG, Context.MODE_PRIVATE);

        val history = JSONObject();
        history.put("url", url);

        historyList.edit().putString(downloadId.toString(), history.toString(2)).apply();
    }

    // 保存下载成功后的路径
    fun saveTaskCompleted(downloadId: Long, absPath: String) {
        val historyList = context.getSharedPreferences(HISTORY_CONFIG, Context.MODE_PRIVATE);
        val historyStr = historyList.getString(downloadId.toString(), null)
        if (historyStr != null) {
            val hisotry = JSONObject(historyStr)
            hisotry.put("absPath", absPath)
            historyList.edit().putString(downloadId.toString(), hisotry.toString(2)).apply();
        }
    }
}