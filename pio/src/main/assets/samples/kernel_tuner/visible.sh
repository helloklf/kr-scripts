#!/system/bin/sh

# $1 clusterIndex

cluster_index=$1

if [[ ! -e cluster.cached ]]; then
  index=1
  for cluster in `ls /sys/devices/system/cpu/cpufreq | grep policy`; do
    echo /sys/devices/system/cpu/cpufreq/$cluster >> cluster$index
    index=$(($index + 1))
  done
  echo "" > cluster.cached
fi

if [[ -e cluster$cluster_index ]]; then
  echo 1
else
  echo 0
fi
