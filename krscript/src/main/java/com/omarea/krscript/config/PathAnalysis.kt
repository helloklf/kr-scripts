package com.omarea.krscript.config

import android.content.Context
import com.omarea.common.shared.FileWrite
import com.omarea.common.shell.KeepShellPublic
import com.omarea.common.shell.RootFile
import com.omarea.krscript.FileOwner
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class PathAnalysis(private var context: Context, private var parentDir: String = "") {
    private val ASSETS_FILE = "file:///android_asset/"

    // 解析路径时自动获得
    private var currentAbsPath: String = ""

    fun getCurrentAbsPath(): String {
        return currentAbsPath
    }

    fun parsePath(filePath: String): InputStream? {
        try {
            if (filePath.startsWith(ASSETS_FILE)) {
                return context.assets.open(filePath.substring(ASSETS_FILE.length))
            } else {
                val fileInputStream = tryOpenDiskFile(filePath)
                if (fileInputStream != null) {
                    return fileInputStream
                } else {
                    try{
                        context.assets.open(filePath).run {
                            currentAbsPath = ASSETS_FILE + filePath
                            return this
                        }
                    } catch (e:java.lang.Exception) {
                        if (parentDir.isNotEmpty()) {
                            val path = (if(parentDir.endsWith("/")) parentDir else (parentDir + "/")).replace(ASSETS_FILE, "") +
                                    (if (filePath.startsWith("./")) filePath.substring(2) else filePath)
                            context.assets.open(path).run {
                                currentAbsPath = ASSETS_FILE + filePath
                                return this
                            }
                        } else {
                            throw e
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            return null
        }
    }

    private fun useRootOpenFile(filePath: String): FileInputStream? {
        if (RootFile.fileExists(filePath)) {
            val dir = File(FileWrite.getPrivateFilePath(context, "kr-script"))
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val cachePath = FileWrite.getPrivateFilePath(context, "kr-script/outside_file.cache")
            val fileOwner = FileOwner(context).fileOwner
            KeepShellPublic.doCmdSync(
                    "cp -f \"$filePath\" \"$cachePath\"\n" +
                            "chmod 777 \"$cachePath\"\n" +
                            "chown $fileOwner:$fileOwner \"$cachePath\"\n")
            File(cachePath).run {
                if (exists() && canRead()) {
                    return inputStream()
                }
            }
        }
        return null
    }

    private fun tryOpenDiskFile(filePath: String): FileInputStream? {
        try {
            File(filePath).run {
                if (exists() && canRead()) {
                    currentAbsPath = absolutePath
                    return inputStream()
                }
            }
            if (filePath.startsWith("/")) {
                currentAbsPath = filePath
                val javaFileInfo = File(filePath)
                if (javaFileInfo.exists() && javaFileInfo.canRead()) {
                    return javaFileInfo.inputStream()
                } else {
                    return useRootOpenFile(filePath)
                }
            } else {
                // 如果当前配置文件路径不为空，则先查找相对于当前配置文件路径的路径
                if (parentDir.isNotEmpty()) {
                    // 解析成绝对路径
                    val relativePath = when {
                        !parentDir.endsWith("/") -> parentDir + "/"
                        else -> parentDir
                    } + (if (filePath.startsWith("./")) filePath.substring(2) else filePath)

                    // 尝试使用普通权限读取文件
                    File(relativePath).run {
                        if (exists() && canRead()) {
                            currentAbsPath = absolutePath
                            return inputStream()
                        }
                    }
                    useRootOpenFile(relativePath)?.run {
                        return this
                    }
                }

                // 路径相对于当前配置文件没找到文件的话，继续查找相对于数据文件根目录的文件
                val privatePath = File(FileWrite.getPrivateFileDir(context) + filePath).absolutePath
                File(privatePath).run {
                    if (exists() && canRead()) {
                        currentAbsPath = absolutePath
                        return inputStream()
                    }
                }
                useRootOpenFile(privatePath)?.run {
                    return this
                }
            }
        } catch (ex: java.lang.Exception) {
        }
        return null
    }
}
