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

    private fun tryOpenDiskFile(filePath: String): FileInputStream? {
        try {
            File(filePath).run {
                if (exists() && canRead()) {
                    currentAbsPath = absolutePath
                    return inputStream()
                }
            }

            if (!filePath.startsWith("/")) {
                if (parentDir.isNotEmpty()) {
                    val relativePath = when {
                        !parentDir.endsWith("/") -> parentDir + "/"
                        else -> parentDir
                    } + (if (filePath.startsWith("./")) filePath.substring(2) else filePath)

                    File(relativePath).run {
                        if (exists() && canRead()) {
                            currentAbsPath = absolutePath
                            return inputStream()
                        }
                    }
                }

                val privatePath = FileWrite.getPrivateFileDir(context) + filePath
                File(privatePath).run {
                    if (exists() && canRead()) {
                        currentAbsPath = absolutePath
                        return inputStream()
                    }
                }
            }

            val parent = when {
                !parentDir.endsWith("/") -> parentDir + "/"
                else -> parentDir
            }

            var relativePath: String? = null
            if (parentDir.isNotEmpty() && !filePath.startsWith("/")) {
                relativePath = when {
                    !parentDir.endsWith("/") -> parentDir + "/"
                    else -> parentDir
                } + (if (filePath.startsWith("./")) filePath.substring(2) else filePath)
            }

            when {
                RootFile.fileExists(filePath) -> {
                    currentAbsPath = filePath
                    filePath
                }
                relativePath != null && RootFile.fileExists(relativePath) -> {
                    currentAbsPath = relativePath
                    relativePath
                }
                else -> null
            }.run {
                val dir = File(FileWrite.getPrivateFilePath(context, "kr-script"))
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val cachePath = FileWrite.getPrivateFilePath(context, "kr-script/outside_file.cache")
                val fileOwner = FileOwner(context).fileOwner
                KeepShellPublic.doCmdSync(
                        "cp -f \"$this\" \"$cachePath\"\n" +
                        "chmod 777 \"$cachePath\"\n" +
                        "chown $fileOwner:$fileOwner \"$cachePath\"\n")
                File(cachePath).run {
                    if (exists() && canRead()) {
                        return inputStream()
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
        }
        return null
    }
}
