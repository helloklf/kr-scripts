#!/system/bin/sh 

busybox mount -o remount,rw -t auto /system
busybox mount -o remount,rw -t auto /data

cd /system/media/theme/.data/
mkdir "res"
mkdir "res/raw-xxhdpi"
echo

cp -af remove.png res/raw-xxhdpi/stat_sys_battery.png
cp -af remove.png res/raw-xxhdpi/stat_sys_battery_charge.png
cp -af remove.png res/raw-xxhdpi/stat_sys_battery_charge_darkmode.png
cp -af remove.png res/raw-xxhdpi/stat_sys_battery_darkmode.png
cp -af remove.png res/raw-xxhdpi/stat_sys_battery_power_save.png
cp -af remove.png res/raw-xxhdpi/stat_sys_battery_power_save_darkmode.png
echo  "隐藏电池成功"

if [ ! -e /data/system/theme/com.android.systemui ];then
  cp -af /system/media/theme/default/com.android.systemui /data/system/theme/
fi
/system/bin/zip -r /data/system/theme/com.android.systemui res > /dev/null
rm -rf res
busybox killall com.android.systemui
echo 