#!/system/bin/sh

# $1 method
# $2 clusterIndex
# $3 value

method=$1
cluster_index=$2
value=$3

if [[ "$method" = "" ]] || [[ "$cluster_index" = "" ]]; then
    return
fi

function load_cluster() {
    cluster_path=$(cat cluster$cluster_index)
}
# 获取指定核心所有可用频率（如果不指定核心，那就默认获取核心0的）
function options() {
    if [[ ! -f frequencies$cluster_index ]]; then
        load_cluster
        for item in `cat $cluster_path/scaling_available_frequencies`
        do
            echo "${item}|$(expr $item / 1000)Mhz" >> frequencies$cluster_index
        done
    fi
    cat frequencies$cluster_index
}

# 获取CPU最低频率
function get_min() {
    load_cluster
    cat $cluster_path/scaling_min_freq
}

# 设置CPU最搞频率
function set_min() {
    load_cluster
    echo $value > $cluster_path/scaling_min_freq
}

# 获取CPU最高频率
function get_max() {
    load_cluster
    cat $cluster_path/scaling_max_freq
}

# 设置CPU最高频率
function set_max() {
    load_cluster
    echo $value > $cluster_path/scaling_max_freq
}

$method
