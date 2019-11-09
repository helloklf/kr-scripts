#!/system/bin/sh

# 获取指定核心所有可用频率（如果不指定核心，那就默认获取核心0的）
function available_frequencies() {
    local core_index="$1"
    if [[ "$core_index" = "" ]]; then
        local core_index=0
    fi

    for item in `cat /sys/devices/system/cpu/cpu$core_index/cpufreq/scaling_available_frequencies`
    do
        echo "${item}|$(expr $item / 1000)Mhz"
    done
}

# 获取CPU最低频率设置
function get_scaling_min_freq() {
    local core_index="$1"
    if [[ "$core_index" = "" ]]; then
        local core_index=0
    fi
    cat /sys/devices/system/cpu/cpu$core_index/cpufreq/scaling_min_freq
}

# 获取CPU最高频率设置
function get_scaling_max_freq() {
    local core_index="$1"
    if [[ "$core_index" = "" ]]; then
        local core_index=0
    fi
    cat /sys/devices/system/cpu/cpu$core_index/cpufreq/scaling_max_freq
}

$1 $2 $3
