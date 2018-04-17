#!/system/bin/sh

index=`cat /sys/devices/system/cpu/cpu4/online`
if [[ "$index" != 0 ]]
then
  chmod 644 /sys/devices/system/cpu/cpu4/online
  echo '0' > /sys/devices/system/cpu/cpu4/online
  chmod 444 /sys/devices/system/cpu/cpu4/online

  chmod 644 /sys/devices/system/cpu/cpu5/online
  echo '0' > /sys/devices/system/cpu/cpu5/online
  chmod 444 /sys/devices/system/cpu/cpu5/online

  chmod 644 /sys/devices/system/cpu/cpu6/online
  echo '0' > /sys/devices/system/cpu/cpu6/online
  chmod 444 /sys/devices/system/cpu/cpu6/online

  chmod 644 /sys/devices/system/cpu/cpu7/online
  echo '0' > /sys/devices/system/cpu/cpu7/online
  chmod 444 /sys/devices/system/cpu/cpu7/online
  echo " 四个大核已经关闭"
else
  chmod 644 /sys/devices/system/cpu/cpu4/online
  echo '1' > /sys/devices/system/cpu/cpu4/online

  chmod 644 /sys/devices/system/cpu/cpu5/online
  echo '1' > /sys/devices/system/cpu/cpu5/online

  chmod 644 /sys/devices/system/cpu/cpu6/online
  echo '1' > /sys/devices/system/cpu/cpu6/online

  chmod 644 /sys/devices/system/cpu/cpu7/online
  echo '1' > /sys/devices/system/cpu/cpu7/online
  echo " 四个大核已经开启"
fi
