#!/system/bin/sh

action=$1
busybox mount -o remount,rw -t auto /system

if [ "$action" = "ios" ]
then
    if [ -e /system/framework/framework-res.ios ] 
    then
      mv /system/framework/framework-res.apk /system/framework/framework-res.apk.org 
      mv /system/framework/framework-res.ios /system/framework/framework-res.apk
      echo  '已替换成IOS过度动画'
	     sync
	     sleep 3
      reboot
   	else
	   echo '当前已经是IOS动画，不需要替换！'
   	fi
elif [ "$action" = "default" ]
then
    if [ -e /system/framework/framework-res.apk.org ] 
    then
      mv /system/framework/framework-res.apk /system/framework/framework-res.ios
      mv /system/framework/framework-res.apk.org  /system/framework/framework-res.apk
      echo  "还原成MIUI默认过度动画"
      sync
      sleep 3
      reboot
	   else
	   echo  '已替换还原成默认动画，不需要替换！'
   	fi
else
    echo '没有选择模式！'
fi
