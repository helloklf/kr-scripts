#!/system/bin/sh
busybox mount -o remount,rw -t auto /system
if [ -e /system/vendor/etc/thermal-engine-8998.conf ] 
then
mkdir /system/usr/thermal8998
chmod 777 /system/usr/thermal8998
mv  /system/vendor/etc/thermal-engine.conf /system/usr/thermal8998/
mv  /system/vendor/etc/thermal-engine-8998.conf /system/usr/thermal8998/
mv  /system/vendor/etc/thermal-engine-8998-high.conf /system/usr/thermal8998/ 
mv  /system/vendor/etc/thermal-engine-8998-map.conf /system/usr/thermal8998/ 
mv  /system/vendor/etc/thermal-engine-8998-nolimits.conf /system/usr/thermal8998/ 
echo "温控已删除"
echo "...."
echo "即将重启系统"
else
  if [ -e /system/usr/thermal8998/thermal-engine-8998.conf ] 
  then
  chmod 777 /system/usr/thermal8998
  mv /system/usr/thermal8998/* /system/vendor/etc/
  rm -rf /system/usr/thermal8998
  echo "温控已恢复"
  echo "...."
  echo "即将重启系统"
    fi
fi

sleep 3

reboot

