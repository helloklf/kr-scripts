#!/system/bin/sh

action=$1
busybox mount -o remount,rw -t auto /system

if [ "$action" = "time_center" ]
then
    if [ -e /system/priv-app/MiuiSystemUI/MiuiSystemUI.center ]
    then
        mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.org
        mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.center /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk
        echo  '已设置居中，SystemUI重启完成后生效！'
        sync
        sleep 2
        busybox killall com.android.systemui
    else
        echo '当前已经是默认布局，不需要恢复！'
    fi
elif [ "$action" = "default" ]
then
    if [ -e /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.org ]
    then
        mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk /system/priv-app/MiuiSystemUI/MiuiSystemUI.center
        mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.org  /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk
        echo  '已恢复默认布局，SystemUI重启完成后生效！'
        sync
        sleep 2
        busybox killall com.android.systemui
    else
        echo  '没有设置时间居中，不需要恢复！'
    fi
else
    echo '没有选择模式！'
fi
