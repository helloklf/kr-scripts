#!/system/bin/sh

function _uninstall() {
    if [[ -f busybox ]]; then
        node=`ls -li busybox | cut -f1 -d " "`
        for item in *; do
            item_node=`ls -li $item | cut -f1 -d " "`
            if [[ $node = $item_node ]]; then
                rm -f $item
                echo '删除' $item 1>&2
            else
                echo '跳过' $item
            fi
        done
    fi
}

function busybox_uninstall() {
    busybox mount -o rw,remount /system
    busybox mount -o rw,remount /system/xbin

    cd /system/xbin
    _uninstall

    cd /system/bin
    _uninstall
}

function busybox_version() {
    echo '当前busybox版本：'
    busybox | grep BusyBox
    echo ''
    echo '当前目录：'
    pwd
}


function root_state() {
    if [[ $(id -u 2>&1) == '0' ]] || [[ $($UID) == '0' ]] || [[ $(whoami 2>&1) == 'root' ]] || [[ $(set | grep 'USER_ID=0') == 'USER_ID=0' ]]; then
        echo '检测结果' 'root'
    else
        echo '检测结果' '非root'
    fi

    echo $(id -u 2>&1)
    echo $($UID)
    echo $(whoami 2>&1)
    echo $(set | grep 'USER_ID=0')


    echo 'ROOT_PERMISSION 变量只在 PIO环境下才有：'
    echo "ROOT_PERMISSION=${ROOT_PERMISSION}"
}

function environment() {
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
    echo "APP_USER_ID=$APP_USER_ID"
    echo "ROOT_PERMISSION=$ROOT_PERMISSION"
    echo "TOOLKIT=$TOOLKIT"
    echo ''
    echo ''
    echo ''
    sleep 1

    echo 'env 命令'
    env
    echo ''
    echo ''
    sleep 1

    echo 'set 命令\n'
    set
    echo '\n\n'
    sleep 1

    echo 'export -p 命令\n'
    export -p
    echo ''
    echo ''
    sleep 1
}

function config_path() {
    echo '这是3.9.2新加入的全新变量'
    echo '它表示的是配置XML存储路径'
    echo ''

    echo 'PAGE_CONFIG_DIR [配置XML来源目录]'
    echo $PAGE_CONFIG_DIR
    echo ''

    echo 'PAGE_CONFIG_FILE [配置XML来源路径]'
    echo $PAGE_CONFIG_FILE
    echo ''

    echo 'PAGE_WORK_DIR [配置XML提取目录]'
    echo $PAGE_WORK_DIR
    echo ''

    echo 'PAGE_WORK_FILE [配置XML提取目录]'
    echo $PAGE_WORK_FILE
    echo ''
}

$1
