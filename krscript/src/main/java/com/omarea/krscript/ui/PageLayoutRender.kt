package com.omarea.krscript.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.preference.*
import android.util.Log
import com.omarea.krscript.R
import com.omarea.krscript.executor.ScriptEnvironmen
import com.omarea.krscript.model.*
import java.lang.Exception

class PageLayoutRender(private val mContext: Context,
                       private var preferenceManager: PreferenceManager,
                       private val actionInfos: ArrayList<ConfigItemBase>,
                       private val clickListener: OnItemClickListener,
                       private val preferenceScreen: PreferenceScreen) {

    interface OnItemClickListener {
        fun onPageClick(item: PageInfo, onCompleted: Runnable)
        fun onActionClick(item: ActionInfo, onCompleted: Runnable)
        fun onSwitchClick(item: SwitchInfo, onCompleted: Runnable)
        fun onPickerClick(item: PickerInfo, onCompleted: Runnable)
    }


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


    private val preferenceClickListener:Preference.OnPreferenceClickListener = object : Preference.OnPreferenceClickListener{
        override fun onPreferenceClick(preference: Preference?): Boolean {
            val handler = Handler(Looper.getMainLooper())
            if (preference != null) {
                val key = preference.key
                try {
                    val item = findItemByKey(key, actionInfos)
                    if (item == null) {
                        Log.e("onPreferenceClick", "找不到指定ID的项 key: " + key)
                        return false
                    } else {
                        if (item is PageInfo) {
                            clickListener.onPageClick(item, Runnable {
                                handler.post {
                                    if (item.descPollingShell.isNotEmpty()) {
                                        item.desc = ScriptEnvironmen.executeResultRoot(mContext, item.descPollingShell)
                                    }
                                    preference.summary = item.desc
                                }
                            })
                        } else if (item is ActionInfo) {
                            clickListener.onActionClick(item, Runnable {
                                handler.post {
                                    if (item.descPollingShell.isNotEmpty()) {
                                        item.desc = ScriptEnvironmen.executeResultRoot(mContext, item.descPollingShell)
                                    }
                                    preference.summary = item.desc
                                }
                            })
                        } else if (item is PickerInfo) {
                            clickListener.onPickerClick(item, Runnable {
                                handler.post {
                                    if (item.descPollingShell.isNotEmpty()) {
                                        item.desc = ScriptEnvironmen.executeResultRoot(mContext, item.descPollingShell)
                                    }
                                    preference.summary = item.desc
                                }
                            })
                        } else if (item is SwitchInfo) {
                            clickListener.onSwitchClick(item, Runnable {
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
                    }
                } catch (ex: Exception) {
                }
            }
            return false
        }
    }

    fun render() {
        mapConfigList(preferenceScreen, actionInfos)
    }

    private fun mapConfigList(preferenceScreen: PreferenceGroup, actionInfos: ArrayList<ConfigItemBase>) {
        var preferenceCategory: PreferenceCategory? = null
        for (index in 0 until actionInfos.size) {
            val it = actionInfos[index]
            var preference: Preference? = null
            if (it is PageInfo) {
                preference = createPagePreference(it)
            } else if (it is SwitchInfo) {
                preference = createSwitchPreference(it)
            } else if (it is ActionInfo) {
                preference = createActionPreference(it)
            } else if (it is PickerInfo) {
                preference = createListPreference(it)
            } else if (it is TextInfo) {
                if (preferenceScreen is PreferenceCategory) {
                    preference = createTextPreferenceWhite(it)
                } else {
                    preference = createTextPreference(it)
                }
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

    private fun createTextPreference(info: TextInfo): Preference {
        val item = this.preferenceManager.createPreferenceScreen(mContext)
        item.key = info.id
        item.title = "" + info.title
        item.summary = "" + info.desc
        item.layoutResource = R.layout.kr_text_list_item

        return item
    }

    private fun createTextPreferenceWhite(info: TextInfo): Preference {
        val item = createTextPreference(info)

        item.layoutResource = R.layout.kr_text_list_item_white

        return  item
    }

    private fun createListPreference(info: PickerInfo): Preference {
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
        item.key = info.id
        item.title = "" + info.title
        item.summary = "" + info.desc
        item.onPreferenceClickListener = preferenceClickListener
        item.layoutResource = R.layout.kr_action_list_item

        return item
    }


    private fun createPagePreference(info: PageInfo): Preference {
        val item = this.preferenceManager.createPreferenceScreen(mContext)
        // val item = EditTextPreference(mContext)
        item.key = info.id
        item.title = "" + info.title
        item.summary = "" + info.desc
        item.onPreferenceClickListener = preferenceClickListener
        item.layoutResource = R.layout.kr_page_list_item2

        return item
    }

    private fun createSwitchPreference(info: SwitchInfo): Preference {
        val item = SwitchPreference(mContext)
        item.key = info.id
        item.title = "" + info.title
        item.summary = "" + info.desc
        item.onPreferenceClickListener = preferenceClickListener
        item.isChecked = info.selected
        item.layoutResource = R.layout.kr_switch_list_item2

        return item
    }

    private fun createActionPreference(info: ActionInfo): Preference {
        val item = this.preferenceManager.createPreferenceScreen(mContext)
        // val item = EditTextPreference(mContext)
        item.key = info.id
        item.title = "" + info.title
        item.summary = "" + info.desc
        item.onPreferenceClickListener = preferenceClickListener
        item.layoutResource = R.layout.kr_action_list_item

        return item
    }

    private fun createPreferenceGroup(info: GroupInfo): PreferenceCategory {
        val preferenceCategory = PreferenceCategory(mContext)
        preferenceCategory.title = info.separator
        preferenceCategory.layoutResource = R.layout.kr_group_list_item

        return preferenceCategory
    }
}
