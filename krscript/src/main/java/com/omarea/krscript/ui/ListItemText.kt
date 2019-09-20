package com.omarea.krscript.ui

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.omarea.krscript.R
import com.omarea.krscript.model.GroupInfo
import com.omarea.krscript.model.TextInfo
import kotlinx.android.synthetic.main.kr_text_list_item.view.*
import android.graphics.Color.parseColor
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan


class ListItemText(private val context: Context,
                   private val layoutId: Int,
                   private val config: TextInfo = TextInfo()) : ListItemView(context, layoutId, config) {

    private val rowsView = layout.findViewById<TextView?>(R.id.kr_rows)

    init {
        if (config.rows.size > 0 && rowsView != null) {
            rowsView.visibility = View.VISIBLE
            for (row in config.rows) {
                val spannableString = SpannableString(if(row.breakRow) ("\n" + row.text) else row.text)

                if (row.color != -1) {
                    val foregroundColorSpan = ForegroundColorSpan(row.color)
                    spannableString.setSpan(foregroundColorSpan, 0, row.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                if (row.bold && row.italic) {
                    val style = StyleSpan(Typeface.BOLD_ITALIC)
                    spannableString.setSpan(style, 0, row.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else if (row.bold) {
                    val style = StyleSpan(Typeface.BOLD)
                    spannableString.setSpan(style, 0, row.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else if (row.italic) {
                    val style = StyleSpan(Typeface.ITALIC)
                    spannableString.setSpan(style, 0, row.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                if (row.size != -1) {
                    val size = AbsoluteSizeSpan(row.size, true)
                    spannableString.setSpan(size, 0, row.text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                rowsView.append(spannableString)
            }
        } else {
            rowsView?.visibility = View.GONE
        }
    }
}
