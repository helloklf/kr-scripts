package com.omarea.krscript.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.omarea.krscript.R
import com.omarea.krscript.model.ActionInfo
import com.omarea.krscript.model.ConfigItemBase

open class ListItemView(private val context: Context,
                        private val layoutId: Int,
                        private val config: ConfigItemBase = ConfigItemBase()) {
    protected var layout = LayoutInflater.from(context).inflate(layoutId, null)
    protected var mOnClickListener: OnClickListener? = null
    protected var mOnLongClickListener: OnLongClickListener? = null

    protected var summaryView = layout.findViewById<TextView?>(R.id.kr_desc)
    protected var titleView = layout.findViewById<TextView?>(R.id.kr_title)


    var title: String
        get(){
            return titleView?.text.toString()
        }
        set (value) {
            if (value.isEmpty()) {
                titleView?.visibility = View.GONE
            } else {
                titleView?.text = value
                titleView?.visibility = View.VISIBLE
            }
        }

    var summary: String
        get(){
            return summaryView?.text.toString()
        }
        set (value) {
            if (value.isEmpty()) {
                summaryView?.visibility = View.GONE
            } else {
                summaryView?.text = value
                summaryView?.visibility = View.VISIBLE
            }
        }

    val key: String
        get () {
            return config.id
        }

    fun getView(): View {
        return layout
    }


    fun addView(view: View): ListItemView {
        layout.findViewById<ViewGroup>(android.R.id.content).addView(view)

        return this
    }

    fun addView(item: ListItemView): ListItemView {
        val content = layout.findViewById<ViewGroup>(android.R.id.content)
        content.addView(item.getView())

        return this
    }

    fun setOnClickListener (onClickListener: OnClickListener): ListItemView {
        this.mOnClickListener = onClickListener

        return this
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener): ListItemView {
        this.mOnLongClickListener = onLongClickListener

        return this
    }

    init {
        if (summaryView == null && config is ActionInfo) {
            summaryView = layout.rootView.findViewById<TextView?>(R.id.kr_desc)
        } else {
            Log.d("summaryView", "" + summaryView?.id + ">>" + config.javaClass.canonicalName)
        }

        title = config.title
        summary = config.desc

        this.layout.setOnClickListener {
            this.mOnClickListener?.onClick(this)
        }
        this.layout.setOnLongClickListener {
            this.mOnLongClickListener?.onLongClick(this)
            true
        }
    }
    interface OnClickListener{
        fun onClick(listItemView: ListItemView)
    }

    interface OnLongClickListener{
        fun onLongClick(listItemView: ListItemView)
    }
}
