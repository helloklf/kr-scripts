#!/system/bin/sh
busybox mount -o remount,rw -t auto /system
busybox mount -o remount,rw -t auto /data

WifiConfigStore="/data/misc/wifi/WifiConfigStore.xml"
wifi="./wifi密码.txt"
lines="./lines"
awk '/<Network>/ {print NR}'  $WifiConfigStore >$lines

rm -f $wifi

echo "WIFI列表>>"  >>$wifi
echo "----------------------------------- "  >>$wifi
while read line
do
wifiNameline=$(( $line + 2 ))
wifiKeyline=$(( $line + 5 ))
sed -n ''$wifiNameline'p;'$wifiKeyline'p' $WifiConfigStore >>$wifi
echo "----------------------------------- "  >>$wifi
done < $lines

rm -f $lines
sed -i "s/<string name=\"ConfigKey\">/WIFI:/g" $wifi
sed -i "s/<string name=\"PreSharedKey\">/KEY:/g" $wifi
sed -i "s/<\/string>//g" $wifi
sed -i "s/&quot;//g" $wifi
sed -i "s/-WPA_PSK//g" $wifi
sed -i "s/NONE//g" $wifi
sed -i "s/<null name=\"PreSharedKey\" \/>//g" $wifi
cat $wifi

rm -f $wifi

