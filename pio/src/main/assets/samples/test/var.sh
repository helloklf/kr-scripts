#!/system/bin/sh

echo '框架定义'
echo ''
echo "EXECUTOR_PATH=$EXECUTOR_PATH"
echo "START_DIR=$START_DIR"
echo "TEMP_DIR=$TEMP_DIR"
echo "ANDROID_UID=$ANDROID_UID"
echo "ANDROID_SDK=$ANDROID_SDK"
echo "SDCARD_PATH=$SDCARD_PATH"
echo "PACKAGE_NAME=$PACKAGE_NAME"
echo "PACKAGE_VERSION_NAME=$PACKAGE_VERSION_NAME"
echo "PACKAGE_VERSION_CODE=$PACKAGE_VERSION_CODE"
echo "TOOLKIT=$TOOLKIT"
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

echo 'export -p 命令'
echo ''
export -p
echo ''
echo ''
echo ''
sleep 1
