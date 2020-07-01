package com.omarea.krscript.model

import java.io.File

open class ClickableNode(public var parentPageConfigPath: String) : NodeInfoBase() {

    public val parentPageConfigDir: String
        get() {
            if (parentPageConfigPath.isNotEmpty()) {
                val dir = File(parentPageConfigPath).parent
                if (dir.startsWith("file:/android_asset/")) {
                    return "file:///android_asset/" + dir.substring("file:/android_asset/".length)
                }
                return dir
            }
            return ""
        }

    // 功能图标路径（列表中）
    var iconPath = ""

    // 功能图标路径（桌面快捷）
    var logoPath = ""

    // 是否允许添加快捷方式（非false，且具有key则默认允许）
    var allowShortcut:Boolean? = null
}
