package com.omarea.krscript.ui

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.omarea.common.ui.DialogHelper
import com.omarea.krscript.R
import com.omarea.krscript.model.ActionParamInfo

class AppChooserRender(private var actionParamInfo: ActionParamInfo, private var context: Context) {

    fun render(): View {
        val layout = LayoutInflater.from(context).inflate(R.layout.kr_param_app, null)
        val textView = layout.findViewById<TextView>(R.id.kr_param_app_text)
        val pathView = layout.findViewById<TextView>(R.id.kr_param_app_package)
        val btn = layout.findViewById<ImageButton>(R.id.kr_param_app_btn)

        btn.setOnClickListener {
            val packages = loadPackages()
            DialogHelper.animDialog(
                    AlertDialog.Builder(context)
                            .setTitle("请选择应用")
                            .setSingleChoiceItems(
                                    packages.map { it.desc }.toTypedArray(),
                                    packages.map { it.value }.indexOf(pathView.text)
                            )
                            { dialog, index ->
                                textView.text = packages[index].desc
                                pathView.text = packages[index].value

                                dialog.dismiss()
                            })
        }

        if (actionParamInfo.valueFromShell != null) {
            textView.text = actionParamInfo.valueFromShell
            pathView.text = actionParamInfo.valueFromShell
        } else if (!actionParamInfo.value.isNullOrEmpty()) {
            textView.text = actionParamInfo.value
            pathView.text = actionParamInfo.value
        }

        pathView.tag = actionParamInfo.name

        return layout
    }

    private fun loadPackages(): List<ActionParamInfo.ActionParamOption> {
        val pm = context.packageManager
        val filter = actionParamInfo.optionsFromShell?.map {
            (it.get("item") as ActionParamInfo.ActionParamOption).value
        }
        val packages = pm.getInstalledPackages(0).filter {
            filter == null || filter.contains(it.packageName)
        }

        val options = packages.map {
            ActionParamInfo.ActionParamOption().apply {
                desc = "" + it.applicationInfo.loadLabel(pm)
                value = it.packageName
            }
        }

        return options
    }
}
