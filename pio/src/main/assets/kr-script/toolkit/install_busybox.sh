#!/system/bin/sh

function busybox_install() {
    for applet in `./busybox --list`; do
        case "$applet" in
        "sh"|"busybox")
            echo 'Skip' > /dev/null
        ;;
        *)
            ./busybox ln -sf busybox "$applet";
        ;;
        esac
    done
    echo '' > busybox_installed
}

if [[ ! "$TOOLKIT" = "" ]]; then
    cd "$TOOLKIT"
    if [[ ! -f busybox_installed ]]; then
        busybox_install
    fi
fi
