#!/system/bin/sh

if [[ $(id -u 2>&1) == '0' ]] || [[ $($UID) == '0' ]] || [[ $(whoami 2>&1) == 'root' ]] || [[ $(set | grep 'USER_ID=0') == 'USER_ID=0' ]]; then
    echo '检测结果' 'root'
else
    echo '检测结果' '非root'
fi

echo $(id -u 2>&1)
echo $($UID)
echo $(whoami 2>&1)
echo $(set | grep 'USER_ID=0')


echo 'ROOT_PERMISSION 变量只在 PIO环境下才有：'
echo "ROOT_PERMISSION=${ROOT_PERMISSION}"