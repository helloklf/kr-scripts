#!/system/bin/sh


echo 当前日期：`date +'%F  %p  %a'|sed 's/AM/上午/g,s/PM/下午/g,s/Mon/星期一/g,s/Tue/星期二/g,s/Wed/星期三/g,s/Thu/星期四/g,s/Fri/星期五/g,s/Sat/星期六/g,s/Sun/星期日/g'`
echo "即将查看时间刷新率"
for i in $(seq 3 -1 1); do
echo $i
sleep 1
done
echo "时间        毫秒"
exit
until [[ $a == true ]]
do
m=`date +'%T %N'|cut -d ' ' -f 1`
ms=`date +'%S %N'|cut -d ' ' -f 2|cut -b 1-3`
echo "$m   ${ms}ms"
sleep 1
done
