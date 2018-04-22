#!/system/bin/sh
state=$1

busybox mount -o remount,rw -t auto /system;
if [ $state == 1 ];then
    sed -i 's/^qemu\.hw\.mainkeys.*/qemu.hw.mainkeys=0/g' /system/build.prop
    echo '全面屏手势开启成功'
else
    sed -i 's/^qemu\.hw\.mainkeys.*/qemu.hw.mainkeys=1/g' /system/build.prop
    echo '全面屏手势关闭成功'
fi
sync;
echo ''
echo '重启后生效！'
