package com.projectkr.shell

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.omarea.common.ui.ProgressBarDialog
import com.omarea.krscript.model.PageNode

class OpenPageHelper(private var activity: Activity) {
    private var progressBarDialog: ProgressBarDialog? = null
    private var handler = Handler(Looper.getMainLooper())

    private val dialog: ProgressBarDialog
        get() {
            if (progressBarDialog == null) {
                progressBarDialog = ProgressBarDialog(activity)
            }
            return progressBarDialog!!
        }

    private fun showDialog(msg: String) {
        handler.post {
            dialog.showDialog(msg)
        }
    }

    private fun hideDialog() {
        handler.post {
            dialog.hideDialog()
        }
    }

    fun openPage(pageNode: PageNode) {
        try {
            var intent: Intent? = null
            if (!pageNode.onlineHtmlPage.isEmpty()) {
                intent = Intent(activity, ActionPageOnline::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("config", pageNode.onlineHtmlPage)
            }

            if (!pageNode.pageConfigSh.isEmpty()) {
                if (intent == null) {
                    intent = Intent(activity, ActionPage::class.java)
                }
                intent.putExtra("pageConfigSh", pageNode.pageConfigSh)
            }

            if (!pageNode.pageConfigPath.isEmpty()) {
                if (intent == null) {
                    intent = Intent(activity, ActionPage::class.java)
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("config", pageNode.pageConfigPath)
            }

            intent?.run {
                putExtra("title", pageNode.title)

                if (pageNode.beforeRead.isNotBlank()) {
                    intent.putExtra("beforeRead", pageNode.beforeRead)
                }
                if (pageNode.afterRead.isNotBlank()) {
                    intent.putExtra("afterRead", pageNode.afterRead)
                }
                if (pageNode.loadSuccess.isNotBlank()) {
                    intent.putExtra("loadSuccess", pageNode.loadSuccess)
                }
                if (pageNode.loadFail.isNotBlank()) {
                    intent.putExtra("loadFail", pageNode.loadFail)
                }
                if (pageNode.parentPageConfigDir.isNotEmpty()) {
                    intent.putExtra("parentDir", pageNode.parentPageConfigDir)
                }

                activity.startActivity(intent)
            }
        } catch (ex: Exception) {
            Toast.makeText(activity, "" + ex.message, Toast.LENGTH_SHORT).show()
        }
    }
}
