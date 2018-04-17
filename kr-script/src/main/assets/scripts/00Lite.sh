#!/system/bin/sh
busybox mount -o remount,rw -t auto /system
busybox mount -o remount,rw -t auto /data
#精简脚本

echo 开始精简咯，你想清楚了吗..
sleep 1
echo ''
echo ''
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
rm -rf /system/app/MiLinkService
rm -rf /system/app/WAPPushManager

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
rm -rf /system/priv-app/CellBroadcastReceiver

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

sleep 3
reboot
