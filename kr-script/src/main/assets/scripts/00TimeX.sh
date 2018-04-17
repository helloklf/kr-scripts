#!/system/bin/sh

action=$1
busybox mount -o remount,rw -t auto /system
if [ ! -e /system/priv-app/MiuiSystemUI/MiuiSystemUI.org ]
then
  cp -af /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk /system/priv-app/MiuiSystemUI/MiuiSystemUI.org
fi

if [ "$action" = "time_center" ]
then
  cp -af /system/priv-app/MiuiSystemUI/MiuiSystemUI.center /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk
  echo  '已设置为普通居中模式！'
  busybox killall com.android.systemui
elif [ "$action" = "default" ]
then
  cp -af /system/priv-app/MiuiSystemUI/MiuiSystemUI.org  /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk
 echo  '已设置为默认模式！'
  busybox killall com.android.systemui
elif [ "$action" = "time_only" ]
then
  cp -af /system/priv-app/MiuiSystemUI/MiuiSystemUI.only /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk
  echo  '已设置为单卡居中模式！'
  busybox killall com.android.systemui
fi
