#!/system/bin/sh

busybox mount -o rw,remount /system
busybox mount -o rw,remount /system/xbin

function uninstall() {
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

cd /system/xbin
uninstall

cd /system/bin
uninstall
