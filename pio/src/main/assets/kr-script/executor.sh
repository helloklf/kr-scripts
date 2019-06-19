#!/system/bin/sh

# 参数说明
# $1 脚本路径
# $2 执行的目录

# 将要执行的具体脚本，执行 executor.sh 时传入，如 ./executor.sh test.sh
script_path="$1"
# 脚本执行目录
execute_path="$2"

# 全局变量
# 框架并不需要这些变量，如果你不需要可以将其删除
# 如有需要，你也可以增加一些自己的变量定义
# 但这个文件每次运行脚本都会被执行，不建议写太复杂的过程
export START_DIR=${START_DIR}
export TEMP_DIR=${TEMP_DIR}
export ANDROID_UID=${ANDROID_UID}
export ANDROID_SDK=${ANDROID_SDK}
export SDCARD_PATH=${SDCARD_PATH}


# 判断是否有指定执行目录
if [[ "$execute_path" != "" ]] && [[ -d "$execute_path" ]]
then
    cd "$execute_path"
fi

# 运行脚本
chmod 755 "$script_path"
sh "$script_path"
