#！/system/xbin/sh
busybox mount -o remount,rw -t auto /system
index=`grep qemu\.hw\.mainkeys= /system/build.prop|cut -d'=' -f2`
echo ''
if [ $index == 1 ];then
sed -i 's/^qemu\.hw\.mainkeys.*/qemu.hw.mainkeys=0/g' /system/build.prop
sed -i 's/^ro\.sf\.lcd_density.*/ro.sf.lcd_density=460/g' /system/build.prop
echo 全面屏手势开启成功
else
sed -i 's/^qemu\.hw\.mainkeys.*/qemu.hw.mainkeys=1/g' /system/build.prop
sed -i 's/^ro\.sf\.lcd_density.*/ro.sf.lcd_density=480/g' /system/build.prop
echo 全面屏手势关闭成功
fi
sleep 1
echo ''
echo ''
echo 即将重启...
sleep 2
reboot

