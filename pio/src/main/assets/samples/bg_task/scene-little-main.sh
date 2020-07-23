#!/system/bin/sh

source $PAGE_WORK_DIR/scene-little-config.sh

cd /cache
rm -rf ./.scene_watcher
mkdir ".scene_watcher"
cd ".scene_watcher"

scene_default_mode="balance"
scene_current_mode=$(getprop vtools.powercfg)
scene_current_app=$(getprop vtools.powercfg_app)

scene_powercfg_sh="/data/powercfg.sh"
scene_powercfg_base_sh="/data/powercfg_base.sh"
if [[ -f /data/data/com.omarea.vtools/files/powercfg.sh ]]; then
    scene_powercfg_sh='/data/data/com.omarea.vtools/files/powercfg.sh'
fi
if [[ -f /data/data/com.omarea.vtools/files/powercfg_base.sh ]]; then
    scene_powercfg_base_sh='/data/data/com.omarea.vtools/files/powercfg_base.sh'
fi

if [[ ! -f "$scene_powercfg_sh" ]]; then
    echo "调度切换所需的["  $scene_powercfg_sh "]文件不存在！" 1>&2
    exit 1
fi

if [[ "$scene_current_mode" = "" ]]; then
 $scene_powercfg_base_sh
fi

# 切换到指定的电源配置模式
function set_power_mode()
{
  local mode="$1"
  local app="$2"
  if [[ ! "$scene_current_mode" = "$mode" ]];
  then
    scene_current_mode="$mode"

    $scene_powercfg_sh "$mode"
    setprop vtools.powercfg "$mode"

    echo ''
    echo ''
    # clear
  fi

  setprop vtools.powercfg_app "$app"

   case "$mode" in
       "powersave")
          echo "[ 省电 ] $app"
       ;;
       "balance")
          echo "[ 均衡 ] $app"
       ;;
       "performance")
          echo "[ 性能 ] $app"
       ;;
       "fast")
          echo "[ 极速 ] $app"
       ;;
       "*")
          echo "[ 未知 ] $app"
       ;;
   esac
}

# 根据应用包名获取 电源配置模式
function trigger_scene()
{
  local app="$1"
  # 跳过忽略的应用
  for list_app in ${ignored[@]}; do
    if [[ "$list_app" = "$app" ]]; then
      return
    fi
  done

  local mode=""

  for list_app in ${performance[@]}; do
    if [[ "$list_app" = "$app" ]]; then
      set_power_mode "performance" "$app"; return;
    fi
  done

  for list_app in ${fast[@]}; do
    if [[ "$list_app" = "$app" ]]; then
      set_power_mode "fast" "$app"; return;
    fi
  done

  for list_app in ${powersave[@]}; do
    if [[ "$list_app" = "$app" ]]; then
      set_power_mode "powersave" "$app"; return;
    fi
  done

  for list_app in ${balance[@]}; do
    if [[ "$list_app" = "$app" ]]; then
      set_power_mode "balance" "$app"; return;
    fi
  done

  set_power_mode "$scene_default_mode" "$app"
}

# 检测前台应用变更
function watcher_start()
{
  local lst_app=""

  echo '现在，开始监视应用切换~'

  am monitor | while read line
  do
    if [[ $line = *Activity* ]]; then
        local current_app=$(echo $(echo "$line" | cut -f2 -d ':'))
        if [[ ! "$current_app" = "$lst_app" ]]; then
            trigger_scene "$current_app"
            local lst_app="$current_app"
        fi
    fi
  done
}
