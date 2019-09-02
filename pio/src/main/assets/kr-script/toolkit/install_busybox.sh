#!/system/bin/sh

function busybox_install() {
    for applet in `./busybox --list`; do
        ./busybox ln -sf busybox "$applet";
    done
    echo '' > busybox_installed
}

if [[ ! "$TOOLKIT" = "" ]]; then
    cd "$TOOLKIT"
    if [[ ! -f busybox_installed ]]; then
        busybox_install
    fi
fi
