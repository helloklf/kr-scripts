#!/system/bin/sh

#echo 收到以下参数
echo "DPI: $1,  Size: $2 * $3"

sleep 1
dpi=$1
width=$2
height=$3

wm density $dpi
wm size ${width}x${height}
