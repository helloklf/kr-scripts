#!/system/bin/sh
busybox mount -o remount,rw -t auto /system
if [ -e /system/vendor/etc/wifi/WCNSS_qcom_cfg.ini.bak ] 
then
mv /system/vendor/etc/wifi/WCNSS_qcom_cfg.ini /system/vendor/etc/wifi/WCNSS_qcom_cfg.ini.org 
mv /system/vendor/etc/wifi/WCNSS_qcom_cfg.ini.bak /system/vendor/etc/wifi/WCNSS_qcom_cfg.ini

echo  WIFI增强

else
  if [ -e /system/vendor/etc/wifi/WCNSS_qcom_cfg.ini.org ] 
  then
  mv /system/vendor/etc/wifi/WCNSS_qcom_cfg.ini /system/vendor/etc/wifi/WCNSS_qcom_cfg.ini.bak 
  mv /system/vendor/etc/wifi/WCNSS_qcom_cfg.ini.org /system/vendor/etc/wifi/WCNSS_qcom_cfg.ini

echo  WIFI恢复
    fi

fi

sleep 3
reboot
