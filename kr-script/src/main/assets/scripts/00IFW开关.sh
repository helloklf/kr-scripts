#!/system/bin/sh
busybox mount -o remount,rw -t auto /data
busybox mount -o remount,rw -t auto /system

if [ -e /data/system/ifw/miui.xml ] 
then
  rm -f /data/system/ifw/miui.xml 
  echo "删除IFW"
  echo "...."
  echo "即将重启系统"
else
  cp -f /system/etc/miui.xml /data/system/ifw/
  chmod 644 /data/system/ifw/miui.xml
  echo "IFW注入成功"
  echo "...."
  echo "即将重启系统"
fi

sleep 2
reboot

