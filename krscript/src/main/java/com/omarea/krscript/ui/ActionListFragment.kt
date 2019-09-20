package com.omarea.krscript.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.omarea.common.ui.DialogHelper
import com.omarea.common.ui.ProgressBarDialog
import com.omarea.krscript.R
import com.omarea.krscript.config.ActionParamInfo
import com.omarea.krscript.executor.ScriptEnvironmen
import com.omarea.krscript.executor.SimpleShellExecutor
import com.omarea.krscript.model.*

class ActionListFragment : PreferenceFragment(), Preference.OnPreferenceClickListener {
    private lateinit var mContext: Context
    private lateinit var actionInfos: ArrayList<ConfigItemBase>
    private fun findItemByKey(key: String, actionInfos: ArrayList<ConfigItemBase>): ConfigItemBase? {
        for (item in actionInfos) {
            if (item.id == key) {
                return item
            } else if (item is GroupInfo && item.children.size > 0) {
                val result = findItemByKey(key, item.children)
                if (result != null) {
                    return  result
                }
            }
        }
        return null
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        val handler = Handler(Looper.getMainLooper())
        if (preference != null) {
            val key = preference.key
            try {
                val item = findItemByKey(key, actionInfos)
                if (item == null) {
                    Log.e("onPreferenceClick", "找不到指定ID的项 key: " + key)
                    return true
                }
                if (item is ActionInfo) {
                    onActionClick(item, Runnable {
                        handler.post {
                            if (item.descPollingShell.isNotEmpty()) {
                                item.desc = ScriptEnvironmen.executeResultRoot(mContext, item.descPollingShell)
                            }
                            preference.summary = item.desc
                        }
                    })
                } else if (item is PickerInfo) {
                    onPickerClick(item, Runnable {
                        handler.post {
                            if (item.descPollingShell.isNotEmpty()) {
                                item.desc = ScriptEnvironmen.executeResultRoot(mContext, item.descPollingShell)
                            }
                            preference.summary = item.desc
                        }
                    })
                } else if (item is SwitchInfo) {
                    onSwitchClick(item, Runnable {
                        handler.post {
                            if (item.descPollingShell.isNotEmpty()) {
                                item.desc = ScriptEnvironmen.executeResultRoot(mContext, item.descPollingShell)
                            }
                            if (item.getState != null && !item.getState.isEmpty()) {
                                val shellResult = ScriptEnvironmen.executeResultRoot(mContext, item.getState)
                                item.selected = shellResult == "1" || shellResult.toLowerCase() == "true"
                            }
                            preference.summary = item.desc
                            (preference as SwitchPreference).isChecked = item.selected
                        }
                    })
                }
            } catch (ex: Exception) {
            }
        }
        return false
    }

    private lateinit var progressBarDialog: ProgressBarDialog
    private var fileChooser: FileChooserRender.FileChooserInterface? = null
    private var actionShortClickHandler: ActionShortClickHandler? = null

    fun setListData(
            context: Context,
            actionInfos: ArrayList<ConfigItemBase>?,
            fileChooser: FileChooserRender.FileChooserInterface,
            actionShortClickHandler: ActionShortClickHandler? = null) {
        this.mContext = context
        this.progressBarDialog = ProgressBarDialog(mContext)
        if (actionInfos != null) {
            this.actionInfos = actionInfos
            this.fileChooser = fileChooser
            this.actionShortClickHandler = actionShortClickHandler
        }
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootView = getView()
        val list = rootView!!.findViewById<View>(android.R.id.list) as ListView
        list.divider = null

        val preferenceScreen = this.preferenceManager.createPreferenceScreen(mContext)
        this.preferenceScreen = preferenceScreen
        mapConfigList(preferenceScreen, actionInfos)
    }

    private fun mapConfigList(preferenceScreen: PreferenceGroup, actionInfos: ArrayList<ConfigItemBase>) {
        var preferenceCategory: PreferenceCategory? = null
        for (index in 0 until actionInfos.size) {
            val it = actionInfos[index]
            var preference: Preference? = null
            if (it is SwitchInfo) {
                preference = createSwitchPreference(it)
            } else if (it is ActionInfo) {
                preference = createActionPreference(it)
            } else if (it is PickerInfo) {
                preference = createListPreference(it)
            } else if (it is GroupInfo) {
                preferenceCategory = createPreferenceGroup(it)
                preferenceScreen.addPreference(preferenceCategory)
                if (it.children.size > 0) {
                    mapConfigList(preferenceCategory, it.children)
                }
            }

            if (preference != null) {
                if (preferenceCategory == null) {
                    preferenceScreen.addPreference(preference)
                } else {
                    preferenceCategory.addPreference(preference)
                }
            }
        }
    }

    private fun createListPreference(pickerInfo: PickerInfo): Preference {
        /*
        // 不够自由
        val item = ListPreference(mContext)
        item.key = index.toString()
        item.title = "" + pickerInfo.title
        item.summary = "" + pickerInfo.desc
        item.onPreferenceClickListener = this
        item.dialogTitle = item.title
        item.value = "" + pickerInfo.value
        item.layoutResource = R.layout.kr_picker_list_item

        if (pickerInfo.options != null) {
            item.entries = pickerInfo.options!!.map {  it.desc }.toTypedArray()
            item.entryValues = pickerInfo.options!!.map {  it.value }.toTypedArray()
        } else {
            item.entries = arrayOf()
            item.entryValues = arrayOf()
        }

        return item
        */

        val item = this.preferenceManager.createPreferenceScreen(mContext)
        // val item = EditTextPreference(mContext)
        item.key = pickerInfo.id
        item.title = "" + pickerInfo.title
        item.summary = "" + pickerInfo.desc
        item.onPreferenceClickListener = this
        item.layoutResource = R.layout.kr_action_list_item2
        // item.widgetLayoutResource = R.layout.kr_action_list_item2

        return item
    }

    private fun createSwitchPreference(switchInfo: SwitchInfo): Preference {
        val item = SwitchPreference(mContext)
        item.key = switchInfo.id
        item.title = "" + switchInfo.title
        item.summary = "" + switchInfo.desc
        item.onPreferenceClickListener = this
        item.isChecked = switchInfo.selected
        item.layoutResource = R.layout.kr_switch_list_item2

        return item
    }

    private fun createActionPreference(actionInfo: ActionInfo): Preference {
        val item = this.preferenceManager.createPreferenceScreen(mContext)
        // val item = EditTextPreference(mContext)
        item.key = actionInfo.id
        item.title = "" + actionInfo.title
        item.summary = "" + actionInfo.desc
        item.onPreferenceClickListener = this
        item.layoutResource = R.layout.kr_action_list_item2
        // item.widgetLayoutResource = R.layout.kr_action_list_item2

        return item
    }

    private fun createPreferenceGroup(groupInfo: GroupInfo): PreferenceCategory {
        val preferenceCategory = PreferenceCategory(mContext)
        preferenceCategory.title = groupInfo.separator
        preferenceCategory.layoutResource = R.layout.kr_group_list_item

        return preferenceCategory
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen?, preference: Preference?): Boolean {
        Log.d("onPreferenceTreeClick", "" + preference?.key)
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }

    fun triggerAction(id: String, onCompleted: Runnable): Boolean {
        return false
    }

    /**
     * 当switch项被点击
     */
    private fun onSwitchClick(switchInfo: SwitchInfo, onExit: Runnable) {
        val toValue = !switchInfo.selected
        if (switchInfo.confirm) {
            DialogHelper.animDialog(AlertDialog.Builder(mContext)
                    .setTitle(switchInfo.title)
                    .setMessage(switchInfo.desc)
                    .setPositiveButton(mContext.getString(R.string.btn_execute)) { _, _ ->
                        switchExecute(switchInfo, toValue, onExit)
                    }
                    .setNegativeButton(mContext.getString(R.string.btn_cancel)) { _, _ ->
                    })
        } else {
            switchExecute(switchInfo, toValue, onExit)
        }
    }

    /**
     * 执行switch的操作
     */
    private fun switchExecute(switchInfo: SwitchInfo, toValue: Boolean, onExit: Runnable) {
        val script = switchInfo.setState ?: return

        actionExecute(switchInfo, script, onExit, object : java.util.HashMap<String, String>() {
            init {
                put("state", if (toValue) "1" else "0")
            }
        })
    }


    /**
     * 单选列表点击
     */
    private fun onPickerClick(pickerInfo: PickerInfo, onExit: Runnable) {
        val paramInfo = ActionParamInfo()
        paramInfo.options = pickerInfo.options
        paramInfo.optionsSh = pickerInfo.optionsSh

        // 获取当前值
        if (pickerInfo.getState != null) {
            paramInfo.valueFromShell = executeScriptGetResult(mContext, pickerInfo.getState!!)
        }

        // 获取可选项（合并options-sh和静态options的结果）
        val coalescentOptions = getParamOptions(paramInfo)

        val options = if (coalescentOptions != null) coalescentOptions!!.map { (it["item"] as ActionParamInfo.ActionParamOption).desc }.toTypedArray() else arrayOf()
        val values = if (coalescentOptions != null) coalescentOptions!!.map { (it["item"] as ActionParamInfo.ActionParamOption).value }.toTypedArray() else arrayOf()

        var index = -1
        if (coalescentOptions != null) {
            index = LayoutRender.getParamOptionsCurrentIndex(paramInfo, coalescentOptions)
        }

        DialogHelper.animDialog(
                AlertDialog.Builder(mContext)
                        .setTitle(pickerInfo.title)
                        .setSingleChoiceItems(options, index) { _, which ->
                            index = which
                        }
                        .setPositiveButton(mContext.getString(R.string.btn_execute)) { _, _ ->
                            pickerExecute(pickerInfo, "" + (if (index > -1) values[index] else ""), onExit)
                        }
                        .setNegativeButton(mContext.getString(R.string.btn_cancel)) { _, _ ->
                        })
    }

    /**
     * 执行picker的操作
     */
    private fun pickerExecute(pickerInfo: PickerInfo, toValue: String, onExit: Runnable) {
        val script = pickerInfo.setState ?: return

        actionExecute(pickerInfo, script, onExit, object : java.util.HashMap<String, String>() {
            init {
                put("state", toValue)
            }
        })
    }

    /**
     * 列表项点击时（如果需要确认界面，则显示确认界面，否则直接准备执行）
     */
    private fun onActionClick(action: ActionInfo, onExit: Runnable) {
        if (action.confirm) {
            DialogHelper.animDialog(AlertDialog.Builder(mContext)
                    .setTitle(action.title)
                    .setMessage(action.desc)
                    .setPositiveButton(mContext.getString(R.string.btn_execute)) { _, _ ->
                        actionExecute(action, onExit)
                    }
                    .setNegativeButton(mContext.getString(R.string.btn_cancel)) { _, _ -> })
        } else {
            actionExecute(action, onExit)
        }
    }


    /**
     * action执行参数界面
     */
    private fun actionExecute(action: ActionInfo, onExit: Runnable) {
        val script = action.script ?: return

        if (action.params != null) {
            val actionParamInfos = action.params!!
            if (actionParamInfos.size > 0) {
                val layoutInflater = LayoutInflater.from(mContext)
                val linearLayout = layoutInflater.inflate(R.layout.kr_params_list, null) as LinearLayout

                val handler = Handler()
                progressBarDialog.showDialog(mContext.getString(R.string.onloading))
                Thread(Runnable {
                    for (actionParamInfo in actionParamInfos) {
                        handler.post {
                            progressBarDialog.showDialog(mContext.getString(R.string.kr_param_load) + if (!actionParamInfo.label.isNullOrEmpty()) actionParamInfo.label else actionParamInfo.name)
                        }
                        if (actionParamInfo.valueShell != null) {
                            actionParamInfo.valueFromShell = executeScriptGetResult(mContext, actionParamInfo.valueShell!!)
                        }
                        handler.post {
                            progressBarDialog.showDialog(mContext.getString(R.string.kr_param_options_load) + if (!actionParamInfo.label.isNullOrEmpty()) actionParamInfo.label else actionParamInfo.name)
                        }
                        actionParamInfo.optionsFromShell = getParamOptions(actionParamInfo) // 获取参数的可用选项
                    }
                    handler.post {
                        progressBarDialog.showDialog(mContext.getString(R.string.kr_params_render))
                    }
                    handler.post {
                        val render = LayoutRender(linearLayout)
                        render.renderList(actionParamInfos, fileChooser)
                        progressBarDialog.hideDialog()
                        if (actionShortClickHandler != null && actionShortClickHandler!!.onParamsView(action,
                                        linearLayout,
                                        Runnable { },
                                        Runnable {
                                            try {
                                                val params = render.readParamsValue(actionParamInfos)
                                                actionExecute(action, script, onExit, params)
                                            } catch (ex: java.lang.Exception) {
                                                Toast.makeText(mContext, "" + ex.message, Toast.LENGTH_LONG).show()
                                            }
                                        })) {
                        } else {
                            val dialogView = layoutInflater.inflate(R.layout.kr_params_dialog, null)
                            dialogView.findViewById<ScrollView>(R.id.kr_param_dialog).addView(linearLayout)
                            dialogView.findViewById<TextView>(R.id.kr_param_dialog_title).setText(action.title)
                            val dialog = DialogHelper.animDialog(AlertDialog.Builder(mContext).setView(dialogView))

                            dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                                dialog!!.dismiss()
                            }
                            dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                                try {
                                    val params = render.readParamsValue(actionParamInfos)
                                    dialog!!.dismiss()
                                    actionExecute(action, script, onExit, params)
                                } catch (ex: java.lang.Exception) {
                                    Toast.makeText(mContext, "" + ex.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }).start()

                return
            }
        }
        actionExecute(action, script, onExit, null)
    }

    /**
     * 获取Param的Options
     */
    private fun getParamOptions(actionParamInfo: ActionParamInfo): ArrayList<HashMap<String, Any>>? {
        val options = ArrayList<HashMap<String, Any>>()
        var shellResult = ""
        if (!actionParamInfo.optionsSh.isEmpty()) {
            shellResult = executeScriptGetResult(mContext, actionParamInfo.optionsSh)
        }

        if (!(shellResult == "error" || shellResult == "null" || shellResult.isEmpty())) {
            for (item in shellResult.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                if (item.contains("|")) {
                    val itemSplit = item.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    options.add(object : HashMap<String, Any>() {
                        init {
                            put("title", itemSplit[1])
                            put("item", object : ActionParamInfo.ActionParamOption() {
                                init {
                                    value = itemSplit[0]
                                    desc = itemSplit[1]
                                }
                            })
                        }
                    })
                } else {
                    options.add(object : HashMap<String, Any>() {
                        init {
                            put("title", item)
                            put("item", object : ActionParamInfo.ActionParamOption() {
                                init {
                                    value = item
                                    desc = item
                                }
                            })
                        }
                    })
                }
            }
        } else if (actionParamInfo.options != null) {
            for (option in actionParamInfo.options!!) {
                val opt = HashMap<String, Any>()
                opt.set("title", if (option.desc == null) "" else option.desc!!)
                opt["item"] = option
                options.add(opt)
            }
        } else {
            return null
        }

        return options
    }

    private fun executeScriptGetResult(context: Context, shellScript: String): String {
        return ScriptEnvironmen.executeResultRoot(mContext, shellScript);
    }

    private fun actionExecute(configItem: ConfigItemBase, script: String, onExit: Runnable, params: HashMap<String, String>?) {
        var shellHandler: ShellHandlerBase? = null
        if (actionShortClickHandler != null) {
            shellHandler = actionShortClickHandler!!.onExecute(configItem, onExit)
        }

        SimpleShellExecutor().execute(mContext, configItem, script, onExit, params, shellHandler)
    }
}
