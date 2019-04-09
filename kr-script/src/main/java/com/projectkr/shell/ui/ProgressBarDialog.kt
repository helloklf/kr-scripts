package com.omarea.ui

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.projectkr.shell.R

/**
 * Loading弹窗
 * Created by Hello on 2018/02/27.
 */

open class ProgressBarDialog(private var context: Context) {
    private var alert: AlertDialog? = null

    public fun isDialogShow(): Boolean {
        return this.alert != null
    }

    public fun hideDialog() {
        if (alert != null) {
            alert!!.dismiss()
            alert!!.hide()
            alert = null
        }
    }

    public fun showDialog(text: String = "正在加载，请稍等..."): AlertDialog? {
        hideDialog()
        val layoutInflater = LayoutInflater.from(context)
        val dialog = layoutInflater.inflate(R.layout.dialog_progress, null)
        val textView = (dialog.findViewById(R.id.dialog_app_details_pkgname) as TextView)
        textView.text = text
        alert = AlertDialog.Builder(context).setView(dialog).setCancelable(false).create()
        alert!!.window!!.setWindowAnimations(R.style.windowAnim)
        alert!!.show()
        return alert
    }
}
