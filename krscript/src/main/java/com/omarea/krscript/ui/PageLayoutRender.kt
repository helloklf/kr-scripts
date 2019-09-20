package com.omarea.krscript.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.omarea.krscript.R
import com.omarea.krscript.executor.ScriptEnvironmen
import com.omarea.krscript.model.*

class PageLayoutRender(private val mContext: Context,
                       private val itemConfigList: ArrayList<ConfigItemBase>,
                       private val clickListener: OnItemClickListener,
                       private val parent: ListItemView) {

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


    private val onItemClickListener: ListItemView.OnClickListener = object : ListItemView.OnClickListener{
        override fun onClick(listItemView: ListItemView) {
            val handler = Handler(Looper.getMainLooper())
            val key = listItemView.key
            try {
                val item = findItemByKey(key, itemConfigList)
                if (item == null) {
                    Log.e("onItemClick", "找不到指定ID的项 key: " + key)
                    return
                } else {
                    when (item) {
                        is PageInfo -> clickListener.onPageClick(item, Runnable {
                            handler.post {
                                if (item.descPollingShell.isNotEmpty()) {
                                    item.desc = ScriptEnvironmen.executeResultRoot(mContext, item.descPollingShell)
                                }
                                listItemView.summary = item.desc
                            }
                        })
                        is ActionInfo -> clickListener.onActionClick(item, Runnable {
                            handler.post {
                                if (item.descPollingShell.isNotEmpty()) {
                                    item.desc = ScriptEnvironmen.executeResultRoot(mContext, item.descPollingShell)
                                }
                                listItemView.summary = item.desc
                            }
                        })
                        is PickerInfo -> clickListener.onPickerClick(item, Runnable {
                            handler.post {
                                if (item.descPollingShell.isNotEmpty()) {
                                    item.desc = ScriptEnvironmen.executeResultRoot(mContext, item.descPollingShell)
                                }
                                listItemView.summary = item.desc
                            }
                        })
                        is SwitchInfo -> clickListener.onSwitchClick(item, Runnable {
                            handler.post {
                                if (item.descPollingShell.isNotEmpty()) {
                                    item.desc = ScriptEnvironmen.executeResultRoot(mContext, item.descPollingShell)
                                }
                                if (item.getState != null && !item.getState.isEmpty()) {
                                    val shellResult = ScriptEnvironmen.executeResultRoot(mContext, item.getState)
                                    item.checked = shellResult == "1" || shellResult.toLowerCase() == "true"
                                }
                                listItemView.summary = item.desc
                                (listItemView as ListItemSwitch).checked = item.checked
                            }
                        })
                    }
                }
            } catch (ex: Exception) {
            }
        }
    }

    fun render() {
        mapConfigList(parent, itemConfigList)
    }

    private fun mapConfigList(parent: ListItemView, actionInfos: ArrayList<ConfigItemBase>) {
        var smallGroup: ListItemGroup? = null
        for (index in 0 until actionInfos.size) {
            val it = actionInfos[index]
           try {
               var preference: ListItemView? = null
               if (it is PageInfo) {
                   preference = createPageItem(it)
               } else if (it is SwitchInfo) {
                   preference = createSwitchItem(it)
               } else if (it is ActionInfo) {
                   preference = createActionItem(it)
               } else if (it is PickerInfo) {
                   preference = createListItem(it)
               } else if (it is TextInfo) {
                   preference = if (parent is ListItemGroup) {
                       createTextItemWhite(it)
                   } else {
                       createTextItem(it)
                   }
               } else if (it is GroupInfo) {
                   smallGroup = createItemGroup(it)
                   parent.addView(smallGroup)
                   if (it.children.size > 0) {
                       mapConfigList(smallGroup, it.children)
                   }
               }

               if (preference != null) {
                   if (smallGroup == null) {
                       parent.addView(preference)
                   } else {
                       smallGroup.addView(preference)
                   }
               }
           } catch (ex: Exception) {
               Toast.makeText(mContext, it.title + "界面渲染异常" + ex.message, Toast.LENGTH_SHORT).show()
           }
        }
    }

    private fun createTextItem(info: TextInfo): ListItemView {
        return ListItemView(mContext, R.layout.kr_text_list_item, info).setOnClickListener(onItemClickListener)
    }

    private fun createTextItemWhite(info: TextInfo): ListItemView {
        return ListItemView(mContext, R.layout.kr_text_list_item_white, info).setOnClickListener(onItemClickListener)
    }

    private fun createListItem(info: PickerInfo): ListItemView {
        return ListItemView(mContext, R.layout.kr_action_list_item, info).setOnClickListener(onItemClickListener)
    }

    private fun createPageItem(info: PageInfo): ListItemView {
        return ListItemView(mContext, R.layout.kr_page_list_item2, info).setOnClickListener(onItemClickListener)
    }

    private fun createSwitchItem(info: SwitchInfo): ListItemView {
        return ListItemSwitch(mContext, R.layout.kr_switch_list_item2, info).setOnClickListener(onItemClickListener)
    }

    private fun createActionItem(info: ActionInfo): ListItemView {
        return ListItemView(mContext, R.layout.kr_action_list_item, info).setOnClickListener(onItemClickListener)
    }

    private fun createItemGroup(info: GroupInfo): ListItemGroup {
        return ListItemGroup(mContext, R.layout.kr_group_list_item, info).setOnClickListener(onItemClickListener) as ListItemGroup
    }
}
