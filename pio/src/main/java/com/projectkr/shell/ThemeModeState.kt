package com.projectkr.shell

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.omarea.common.ui.ThemeMode

object ThemeModeState {
    private var themeMode: ThemeMode = ThemeMode()
    fun switchTheme (activity: Activity? = null): ThemeMode {
        if (activity != null) {
            val uiModeManager = activity.applicationContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            val nightMode = (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES)
            if (nightMode) {
                themeMode.isDarkMode = true
                themeMode.isLightStatusBar = false
                activity.setTheme(R.style.AppThemeDark)
            } else {
                themeMode.isDarkMode = false
                themeMode.isLightStatusBar = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

                activity.window.run {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    statusBarColor = Color.WHITE
                    navigationBarColor = Color.WHITE

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    } else {
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                }

                //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //getWindow().setNavigationBarColor(Color.WHITE);
            }
        }
        return themeMode
    }
    fun getThemeMode (): ThemeMode {
        return themeMode
    }
}
