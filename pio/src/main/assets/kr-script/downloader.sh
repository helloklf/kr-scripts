#!/system/bin/sh

# 说明
# downloader
#   参数          说明
#   downloadUrl   下载地址
#   md5           用于对比下载的文件是否被劫持修改
#   输出
#   由于shell的函数只能返回0-255的数字，因此下载后的文件路径，你需要通过变量[downloader_result]获得
#
# 原理：通过应用内置的Activity [com.projectkr.shell.ActionPageOnline] 来调用系统自带下载管理器
#  传给Activity的参数
#   --es downloadUrl 【url】下载路径
#   --ez autoClose 【true/false】 是否下载完成后自动关闭界面，非必需
#   --ez taskId 【taskId】 用于区别下载任务和获取进度，非必需
#
# $START_DIR/downloader/path/[md5]          以md5为文件名，存储下载完成的文件所在位置
# $START_DIR/downloader/result/[taskId]     以taskId为文件名，存储下载完的文件所在位置
# $START_DIR/downloader/status/[taskId]     以taskId为文件名，存储下载任务的进度（-1表示创建下载任务失败，0~100为具体进度，如果下载过程中关闭了进度页面，此状态将不会再跟新）

# 下载文件（传入url 和 md5）
function downloader() {
    local downloadUrl="$1"
    local md5="$2"
    downloader_result="" # 清空变量，后续此变量将用于存放文件下载后的存储路径

    # 检查是否下载过相同MD5的文件，并且文件文件还存在
    # 如果存在相同md5的文件，直接输出其路径，并跳过下载
    # downloader/path 目录存储的是此前下载过的文件路径，以md5作为区分
    local hisotry="$START_DIR/downloader/path/$md5"
    if [[ -f "$hisotry" ]]; then
        local abs_path=`cat "$hisotry"`
        if [[ -f "$abs_path" ]]; then
            # 此前下载的文件还在，检查md5是否一致
            local local_file=`md5sum "$abs_path"`
            if [[ "$local_file" = "$md5" ]]; then
                downloader_result="$abs_path"
                return
            fi
        fi
    fi

    local task_id=`cat /proc/sys/kernel/random/uuid`
    # intent参数
    # --es downloadUrl 【url】下载路径
    # --ez autoClose autoClose 【true/false】 是否下载完成后自动关闭界面
    # --es taskId 【taskId】下载任务的唯一标识 用于跟踪进度

    activity="$PACKAGE_NAME/com.projectkr.shell.ActionPageOnline"
    am start -a android.intent.action.MAIN -n "$activity" --es downloadUrl "$downloadUrl" --ez autoClose true --es taskId "$task_id" 1 > /dev/null

    # 等待下载完成
    # downloader/status 存储的是所有下载任务的进度
    # 0~100 为下载进度百分比，-1表示创建下载任务失败
    local task_path="$START_DIR/downloader/status/$task_id"
    local result_path="$START_DIR/downloader/result/$task_id"
    while [[ '1' = '1' ]]
    do
        if [[ -f "$task_path" ]]; then
            local status=`cat "$task_path"`
            if [[ "$status" = 100 ]] || [[ -f "$result_path" ]]; then
                echo "progress:[$status/100]"
                break
            elif [[ "$status" -gt 0 ]]; then
                echo "progress:[$status/100]"
                # echo '已下载：'$status
            elif [[ "$status" = '-1' ]]; then
                echo '文件下载失败' 1>&2
                # 退出
                return 10
            fi
        fi
        sleep 1
    done

    # 再次检查md5，以便校验下载后的文件md5是否一致
    if [[ "$md5" != "" ]]; then
        local hisotry="$START_DIR/downloader/path/$md5"
        if [[ -f "$hisotry" ]]; then
            downloader_result=`cat "$hisotry"`
        else
            echo '下载完成，但文件MD5与预期的不一致' 1>&2
        fi
    else
        downloader_result=`cat $START_DIR/downloader/result/$task_id`
    fi
}

