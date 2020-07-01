#!/system/bin/sh

ignored=(
"android" # 安卓系统
"com.android.systemui" # SystemUI
"com.miui.freeform" # 多窗口服务
"com.miui.touchassistant" # MIUI 悬浮球
"com.miui.securitycenter" # MIUI 安全中心
"com.miui.contentextension" # MIUI 上下文拓展服务
"com.miui.systemAdSolution" # MIUI 系统广告服务
"com.lbe.security.miui" # MIUI 授权管理
"com.miui.systemadsolution" # MIUI 系统广告服务
"com.omarea.vtools" # Scene（微工具箱2）
"com.omarea.vboot" # 微工具箱
"com.omarea.gesture" # 第三方手势导航软件
"com.zbar.lib" #
"eu.chainfire.supersu" # SuperSU
"dev.nick.app.screencast" # 第三方屏幕录制软件
"eu.chainfire.perfmon" # 性能监视器
"com.c4x.roundcorner" # 第三方圆角软件
"com.sohu.inputmethod.sogou.xiaomi" # 搜狗输入法小米版
"com.sohu.inputmethod.sogou" # 搜狗输入法
"com.tencent.gamehelper.smoba" # 王者荣耀助手
"com.waterdaaan.cpufloat" # CPU Float
"com.sec.app.screenrecorder" # 小米 屏幕录制
"com.cyngn.screencast" # CM、Los 屏幕录制
"com.bst.airmessage" # 某系统 浮动通知
"com.gamebench.metricscollector" # GameBench
"com.samsung.android.game.gametools" # 三星游戏工具箱
"com.samsung.android.app.cocktailbarservice" # 三星曲面侧屏
"com.samsung.android.app.assistantmenu" # 三星悬浮球
"com.samsung.android.app.aodservice" # 三星ADO
"com.samsung.android.MtpApplication" # 三星MTP
"com.android.vpndialogs" # VPM 弹窗
"com.android.documentsui" # 文档选择器
"com.android.wallpapercropper" # 壁纸选择器
)

performance=(
"com.tencent.tmgp.speedmobile" # QQ飞车
"com.tencent.tmgp.pubgm" # 绝地求生（天美）
"com.tencent.tmgp.pubgmhd" # 刺激战场、和平精英（光子）
"com.rarlab.rar" # WinRAR
"com.stupeflix.replay" # Go Pro 视频编辑器
"com.niksoftware.snapseed" # Snapseed 照片编辑器
"com.goseet.vidtrim" # VIDTrim 视频编辑器
"com.netease.hyxd" # 网易 荒野行动
"com.netease.dwrg" # 网易 第五人格
"com.netease.mrzh" # 网易 明日之后
"com.netease.mrzh.aligames" # 网易明日之后（阿里游戏、九游）
"com.mihoyo.bh3" # 崩坏3 官服
"com.mihoyo.bh3.mi" # 崩坏3 小米服
"com.mihoyo.bh3.uc" # 崩坏3 UC、九游
"com.miHoYo.bh3.wdj" # 崩坏3 豌豆荚服
"com.pinguo.edit.sdk" # MIX 照片编辑器
"com.ak.mi" # 小米枪战
"com.tencent.cldts" # 光荣使命
)

fast=(
"com.ludashi.benchmark" # 鲁大师跑分
"com.antutu.ABenchMark" # 安兔兔跑分
"com.antutu.benchmark.full" # 安兔兔跑分3D
"com.keramidas.TitaniumBackup" # 钛备份
"com.primatelabs.geekbench" # Geekbench
"com.andromeda.androbench2" # AndroBench
"com.xzr.La.bench" # 镧·跑分
"com.ioncannon.cpuburn.gpugflops" # Gpugflops
"com.android.packageinstaller" # 打包安装程序
"com.miui.packageinstaller" # 打包安装程序（MIUI）
"com.google.android.packageinstaller" # 打包安装程序（Google）
"com.miui.backup" # MIUI本地备份还原
"com.ludashi.vrbench" # 鲁大师VR跑分
"com.android.camera" # 系统相机
"com.futuremark.dmandroid.application" # 3DMark
)

powersave=(
"com.ruanmei.ithome" # IT之家
"com.coolapk.market" # 酷安
"com.kugou.android" # 酷狗音乐
"com.tencent.qqmusic" # QQ音乐
"tv.danmaku.bili" # 哔哩哔哩
"com.android.browser" # 浏览器
"com.google.android.apps.photos" # Google 相册
"com.miui.player" # MIUI音乐
)

balance=(
"com.miui.home" # MIUI桌面
)