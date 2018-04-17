#!/system/bin/sh
busybox mount -o remount,rw -t auto /system

#初始化
sed -i '1,$s/#//g' /system/etc/hosts
sed -i '3,$s/^/#&/g' /system/etc/hosts

NowModel=`sed -n '3p' /system/etc/hosts`

indexA='A'
indexB='B'
indexC='C'

result=$(echo $NowModel | grep "${indexA}")
if [[ "$result" != "" ]]
then
#强力
  sed -i '3c #model-B' /system/etc/hosts
  sed -i '4,$s/#//g' /system/etc/hosts
  echo '开启强力拦截'
else
      result=$(echo $NowModel | grep "${indexB}")
      if [[ "$result" != "" ]]
      then
      #强力
        sed -i '3c #model-C' /system/etc/hosts
        sed -i '1,$s/#//g' /system/etc/hosts
        sed -i '3,$s/^/#&/g' /system/etc/hosts  
        echo '无拦截模式'
      else
       #仅仅去广告
      sed -i '3c #model-A' /system/etc/hosts
      sed -i '200,$s/#//g' /system/etc/hosts
      echo '开启仅去广告'
      fi
fi





