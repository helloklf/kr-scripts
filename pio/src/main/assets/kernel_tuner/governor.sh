#!/system/bin/sh

# 获取可用的调度器
function governors() {
    local items=`cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors`
    for item in $items
    do
        echo $item
    done
}

# 获取cpu核心当前使用的调度器（如果不指定核心，则默认获取核心0的调度器）
function get_governor() {
    local core_index="$1"
    if [[ "$core_index" = "" ]]; then
        local core_index=0
    fi

    cat /sys/devices/system/cpu/cpu$core_index/cpufreq/scaling_governor
}

$1 $2 $3