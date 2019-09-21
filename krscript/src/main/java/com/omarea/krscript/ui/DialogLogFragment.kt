package com.omarea.krscript.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.omarea.krscript.R
import com.omarea.krscript.executor.SimpleShellExecutor
import com.omarea.krscript.model.ConfigItemBase
import com.omarea.krscript.model.ShellHandlerBase
import kotlinx.android.synthetic.main.kr_dialog_log.*


class DialogLogFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // val view = inflater.inflate(R.layout.kr_dialog_log, container, false)

        return currentView
    }

    private var running = false
    private lateinit var configItem: ConfigItemBase
    private lateinit var onExit: Runnable
    private lateinit var script: String
    private var params: HashMap<String, String>? = null
    private lateinit var currentView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(activity!!, R.style.kr_full_screen_dialog)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.kr_dialog_log, null)
        dialog.setView(view)
        currentView = view

        return dialog.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val shellHandler = openExecutor()

        if (shellHandler != null) {
            SimpleShellExecutor().execute(this.activity, configItem, script, onExit, params, shellHandler)
        }
    }

    fun openExecutor(): ShellHandlerBase? {
        var forceStopRunnable: Runnable? = null

        btn_hide.setOnClickListener {
            closeView()
        }
        btn_exit.setOnClickListener {
            if (running && forceStopRunnable != null) {
                forceStopRunnable!!.run()
            }
            closeView()
        }

        btn_copy.setOnClickListener {
            try {
                val myClipboard: ClipboardManager = this.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val myClip: ClipData = ClipData.newPlainText("text", shell_output.text.toString())
                myClipboard.primaryClip = myClip
                Toast.makeText(context, getString(R.string.copy_success), Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(context, getString(R.string.copy_fail), Toast.LENGTH_SHORT).show()
            }
        }
        if (configItem.interruptible) {
            btn_hide?.visibility = View.VISIBLE
            btn_exit?.visibility = View.VISIBLE
        } else {
            btn_hide?.visibility = View.GONE
            btn_exit?.visibility = View.GONE
        }

        title.text = configItem.title
        action_progress.isIndeterminate = true
        return MyShellHandler(object : IActionEventHandler {
            override fun onExit() {
                running = false

                onExit.run()
                if (btn_hide != null) {
                    btn_hide.visibility = View.GONE
                    btn_exit.visibility = View.VISIBLE
                    action_progress.visibility = View.GONE

                    if (configItem.autoOff) {
                        closeView()
                    }
                }

                isCancelable = true
            }

            override fun onStart(forceStop: Runnable?) {
                running = true

                if (configItem.interruptible && forceStop != null) {
                    btn_exit.visibility = View.VISIBLE
                } else {
                    btn_exit.visibility = View.GONE
                }
                forceStopRunnable = forceStop
            }

        }, shell_output, action_progress)
    }

    @FunctionalInterface
    interface IActionEventHandler {
        fun onStart(forceStop: Runnable?)
        fun onExit()
    }

    class MyShellHandler(private var actionEventHandler: IActionEventHandler, private var logView: TextView, private var shellProgress: ProgressBar) : ShellHandlerBase() {
        private val context = logView.context
        override fun onStart(forceStop: Runnable?) {
            actionEventHandler.onStart(forceStop)
        }

        override fun onProgress(current: Int, total: Int) {
            if (current == -1) {
                this.shellProgress.visibility = View.VISIBLE
                this.shellProgress.isIndeterminate = true
            } else if (current == total) {
                this.shellProgress.visibility = View.GONE
            } else {
                this.shellProgress.visibility = View.VISIBLE
                this.shellProgress.isIndeterminate = false
                this.shellProgress.max = total
                this.shellProgress.progress = current
            }
        }

        override fun onStart(msg: Any?) {
            this.logView.text = ""
            // updateLog(msg, Color.GRAY)
        }

        override fun onExit(msg: Any?) {
            updateLog(context.getString(R.string.kr_shell_completed), Color.BLUE)
            actionEventHandler.onExit()
        }

        override fun updateLog(msg: SpannableString?) {
            if (msg != null) {
                this.logView.post {
                    logView.append(msg)
                    (logView.parent as ScrollView).fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        }
    }

    private fun closeView() {
        try {
            dismiss()
        } catch (ex: java.lang.Exception) {
        }
    }

    companion object {
        fun create(configItem: ConfigItemBase, onExit: Runnable, script: String, params: HashMap<String, String>?): DialogLogFragment {
            val fragment = DialogLogFragment()
            fragment.configItem = configItem
            fragment.onExit = onExit
            fragment.script = script
            fragment.params = params

            return fragment
        }
    }
}
