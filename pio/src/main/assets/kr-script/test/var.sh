#!/system/bin/sh

echo '普通变量'
echo ''
echo "param_one=$param_one"
echo "param_two=$param_two"
echo "state=$state"
echo ''
echo ''
echo ''
sleep 1

echo '框架定义'
echo ''
echo "START_DIR=$START_DIR"
echo "TEMP_DIR=$TEMP_DIR"
echo "ANDROID_UID=$ANDROID_UID"
echo "ANDROID_SDK=$ANDROID_SDK"
echo "SDCARD_PATH=$SDCARD_PATH"
echo ''
echo ''
echo ''
sleep 1

echo 'env 命令'
env
echo ''
echo ''
echo ''
sleep 1

echo 'set 命令'
echo ''
set
echo ''
echo ''
echo ''
sleep 1

# echo 'export 命令' 1>&2
# export
# echo ''
# echo ''