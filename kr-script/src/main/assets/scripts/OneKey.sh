busybox mount -o remount,rw -t auto /system
busybox mount -o remount,rw -t auto /data
#懒人切换

echo 一键懒人切换
sleep 2
echo ''
echo 精简、开启IFW、温控开关、时间居中、全面屏
sleep 2
echo ''
echo 各项状态:
sleep 3
echo ''

if [ -e /system/priv-app/Settings/Settings.hole ] 
then
rm -f /system/priv-app/Settings/Settings.apk
mv /system/priv-app/Settings/Settings.hole /system/priv-app/Settings/Settings.apk
rm -rf /system/app/SecurityCoreAdd
rm -rf /system/app/SecurityAdd
fi

rm -rf /system/app/AnalyticsCore
rm -rf /system/app/mab
rm -rf /system/app/SystemAdSolution
rm -rf /system/app/GameCenter
rm -rf /system/app/BugReport
rm -rf /system/app/klobugreport
rm -rf /system/app/MiLivetalk
rm -rf /system/app/BasicDreams
rm -rf /system/app/BookmarkProvider
rm -rf /system/app/FidoCryptoService
rm -rf /system/app/FidoSuiService
rm -rf /system/app/HTMLViewer
rm -rf /system/app/PrintRecommendationService
rm -rf /system/app/SampleAuthenticatorService
rm -rf /system/app/SampleExtAuthService
rm -rf /system/app/SecureExtAuthService
rm -rf /system/app/SecureSampleAuthService
rm -rf /system/app/SYSOPT
rm -rf /system/app/UpnpService
rm -rf /system/app/WfdService
rm -rf /system/app/WMService
rm -rf /system/app/GoogleTTS
rm -rf /system/app/DMService
rm -rf /system/app/MSA
rm -rf /system/app/Updater
rm -rf /system/app/EasterEgg
rm -rf /system/app/HybridAccessory
rm -rf /system/app/HybridPlatform
rm -rf /system/app/MetokNLP
rm -rf /system/app/talkback
rm -rf /system/app/XMPass
rm -rf /system/app/uceShimService
rm -rf /system/app/PrintSpooler
rm -rf /system/app/BuiltInPrintService
rm -rf /system/app/VirtualSim
rm -rf /system/app/Userguide
rm -rf /system/app/PowerChecker

rm -rf /system/priv-app/Tag
rm -rf /system/priv-app/PicoTts
rm -rf /system/priv-app/Music
rm -rf /system/priv-app/MiVRFramework
rm -rf /system/priv-app/MiGameCenterSDKService
rm -rf /system/priv-app/MiuiVideo
#rm -rf /system/priv-app/Browser
rm -rf /system/priv-app/VirtualSim
rm -rf /system/priv-app/DMRegService
rm -rf /system/priv-app/Mipub
rm -rf /system/priv-app/OneTimeInitializer
rm -rf /system/tts

if [ -e  /system/framework/vcard.jar ]
then 
rm -rf /system/app/KSICibaEngine
#rm -rf /system/app/Mipay
rm -rf /system/app/MiuiSuperMarket
rm -rf /system/app/PhotoTable
rm -rf /system/app/SystemBaseFunctions
rm -rf /system/app/TranslationService
#rm -rf /system/app/TSMClient
rm -rf /system/app/VipAccount
rm -rf /system/app/VoiceAssist
rm -rf /system/app/Email
rm -rf /system/app/LiveWallpapersPicker
rm -rf /system/app/MiuiCompass
rm -rf /system/app/PaymentService
rm -rf /system/app/WallpaperBackup
rm -rf /system/app/XiaomiServiceFramework
rm -rf /system/app/YouDaoEngine
rm -rf /system/app/MiLinkService
rm -rf /system/app/UPTsmService
rm -rf /system/app/Calculator
rm -rf /system/app/GPSLogSave
rm -rf /system/app/MiuiScreenRecorder
#rm -rf /system/app/NextPay
#rm -rf /system/app/Whetstone
rm -rf /system/app/AppIndexProvider
rm -rf /system/app/Cit

rm -rf /system/priv-app/ContentExtension
rm -rf /system/priv-app/GlobalNetworkProxy
rm -rf /system/priv-app/QuickSearchBox
rm -rf /system/priv-app/MiDrop
rm -rf /system/priv-app/MiuiScanner
rm -rf /system/priv-app/YellowPage
rm -rf /system/priv-app/CellBroadcastReceiver

rm -f /system/framework/vcard.jar
mv /system/priv-app/CleanMaster/CleanMaster.apk /system/priv-app/CleanMaster/CleanMaster.apk.bak
echo "精简完成"
else
  echo "啥都没了，不再精简！"
fi

echo ''
sleep 2
if [ -e /data/system/ifw/miui.xml ] 
then
  rm -f /data/system/ifw/miui.xml 
  echo "删除IFW"
else
  cp -f /system/etc/miui.xml /data/system/ifw/
  chmod 644 /data/system/ifw/miui.xml
  echo "IFW注入成功"
fi

echo ''
sleep 2
if [ -e /system/priv-app/MiuiSystemUI/MiuiSystemUI.center ] 
then
mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.org 
mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.center /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk
echo "时间居中"
else
  if [ -e /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.org ] 
  then
  mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk /system/priv-app/MiuiSystemUI/MiuiSystemUI.center
  mv /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk.org  /system/priv-app/MiuiSystemUI/MiuiSystemUI.apk
  echo "时间恢复"
  fi
fi

echo ''
sleep 2
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
else
  if [ -e /system/usr/thermal8998/thermal-engine-8998.conf ] 
  then
  chmod 777 /system/usr/thermal8998
  mv /system/usr/thermal8998/* /system/vendor/etc/
  rm -rf /system/usr/thermal8998
  echo "温控已恢复"
  fi
fi

sleep 2
index=`grep qemu\.hw\.mainkeys= /system/build.prop|cut -d'=' -f2`
echo ''
if [ $index == 1 ];then
sed -i 's/^qemu\.hw\.mainkeys.*/qemu.hw.mainkeys=0/g' /system/build.prop
sed -i 's/^ro\.sf\.lcd_density.*/ro.sf.lcd_density=460/g' /system/build.prop
echo "全面屏手势开启"
else
sed -i 's/^qemu\.hw\.mainkeys.*/qemu.hw.mainkeys=1/g' /system/build.prop
sed -i 's/^ro\.sf\.lcd_density.*/ro.sf.lcd_density=480/g' /system/build.prop
echo "全面屏手势关闭"
fi

sleep 2
echo ''
echo "全部执行完成，即将重启!"
sleep 5
reboot
