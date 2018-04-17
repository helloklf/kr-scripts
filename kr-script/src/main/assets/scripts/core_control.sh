#!/system/bin/sh

chmod 644 /sys/devices/system/cpu/cpu0/online
echo 1 > /sys/devices/system/cpu/cpu0/online
chmod 444 /sys/devices/system/cpu/cpu0/online

chmod 644 /sys/devices/system/cpu/cpu1/online
echo 1 > /sys/devices/system/cpu/cpu1/online
chmod 444 /sys/devices/system/cpu/cpu1/online

chmod 644 /sys/devices/system/cpu/cpu2/online
echo 1 > /sys/devices/system/cpu/cpu2/online
chmod 444 /sys/devices/system/cpu/cpu2/online

chmod 644 /sys/devices/system/cpu/cpu3/online
echo 1 > /sys/devices/system/cpu/cpu3/online
chmod 444 /sys/devices/system/cpu/cpu3/online


chmod 644 /sys/devices/system/cpu/cpu4/online
echo $1 > /sys/devices/system/cpu/cpu4/online
chmod 444 /sys/devices/system/cpu/cpu4/online

chmod 644 /sys/devices/system/cpu/cpu5/online
echo $2 > /sys/devices/system/cpu/cpu5/online
chmod 444 /sys/devices/system/cpu/cpu5/online

chmod 644 /sys/devices/system/cpu/cpu6/online
echo $3 > /sys/devices/system/cpu/cpu6/online
chmod 444 /sys/devices/system/cpu/cpu6/online

chmod 644 /sys/devices/system/cpu/cpu7/online
echo $4 > /sys/devices/system/cpu/cpu7/online
chmod 444 /sys/devices/system/cpu/cpu7/online

sleep 1;

echo "Little Cluster"
echo "----------------------"
echo "CPU0：        `cat /sys/devices/system/cpu/cpu0/online`"
echo "CPU1：        `cat /sys/devices/system/cpu/cpu1/online`"
echo "CPU2：        `cat /sys/devices/system/cpu/cpu2/online`"
echo "CPU3：        `cat /sys/devices/system/cpu/cpu3/online`"

echo ""
echo "Big Cluster"
echo "----------------------"
echo "CPU4：        `cat /sys/devices/system/cpu/cpu4/online`"
echo "CPU5：        `cat /sys/devices/system/cpu/cpu5/online`"
echo "CPU6：        `cat /sys/devices/system/cpu/cpu6/online`"
echo "CPU7：        `cat /sys/devices/system/cpu/cpu7/online`"
