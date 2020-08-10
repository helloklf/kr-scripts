#!/system/bin/sh

# 参数说明
# $1 脚本路径

# 将要执行的具体脚本，执行 executor.sh 时传入，如 ./executor.sh test.sh
script_path="$1"

# 全局变量 - 会由脚本引擎为其赋值
# 框架并不需要这些变量，如果你不需要可以将其删除
# 如有需要，你也可以增加一些自己的变量定义
# 但这个文件每次运行脚本都会被执行，不建议写太复杂的过程
export EXECUTOR_PATH="$({EXECUTOR_PATH})"
export START_DIR="$({START_DIR})"
export TEMP_DIR="$({TEMP_DIR})"
export ANDROID_UID="$({ANDROID_UID})"
export ANDROID_SDK="$({ANDROID_SDK})"
export SDCARD_PATH="$({SDCARD_PATH})"
export BUSYBOX="$({BUSYBOX})"
export MAGISK_PATH="$({MAGISK_PATH})"
export PACKAGE_NAME="$({PACKAGE_NAME})"
export PACKAGE_VERSION_NAME="$({PACKAGE_VERSION_NAME})"
export PACKAGE_VERSION_CODE="$({PACKAGE_VERSION_CODE})"
export APP_USER_ID="$({APP_USER_ID})"

# ROOT_PERMISSION 取值为：true 或 false
export ROOT_PERMISSION=$({ROOT_PERMISSION})

# 修复非ROOT权限执行脚本时，无法写入默认的缓存目录 /data/local/tmp
export TMPDIR="$TEMP_DIR"

# toolkit工具目录
export TOOLKIT="$({TOOLKIT})"
# 添加toolkit添加为应用程序目录
if [[ ! "$TOOLKIT" = "" ]]; then
    # export PATH="$PATH:$TOOLKIT"
    PATH="$PATH:$TOOLKIT"
fi

# 安装busybox完整功能
if [[ -f "$TOOLKIT/install_busybox.sh" ]]; then
    sh "$TOOLKIT/install_busybox.sh"
fi

# 判断是否有指定执行目录，跳转到起始目录
if [[ "$START_DIR" != "" ]] && [[ -d "$START_DIR" ]]
then
    cd "$START_DIR"
fi

# 运行脚本
if [[ -f "$script_path" ]]; then
    chmod 755 "$script_path"
    # sh "$script_path"     # 2019.09.02 before
    source "$script_path"   # 2019.09.02 after
else
    echo "${script_path} 已丢失" 1>&2
fi
