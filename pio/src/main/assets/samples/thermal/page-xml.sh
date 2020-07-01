source $START_DIR/samples/thermal/path_config.sh

# 构成界面

echo '<?xml version="1.0" encoding="UTF-8" ?>'
echo '<root>'

echo '
<group>
    <text>
        <slices>
            <slice>以下为从/vendor/etc 目录下找到的温控配置文件</slice>
        </slices>
    </text>
</group>
'
echo '<group title="文件列表">'

function build_node(){
    echo "
    <action>
        <title>$1</title>
        <desc>/vendor/etc/$1\n\n $output_dir/$1</desc>
        <param value-sh=\"cat $output_dir/$1\" name=\"thermal\" />
        <script>
            echo '保存' $1
            echo \"\${thermal}\" > $output_dir/$1
            echo ''
            echo '#########################################'
            cat $output_dir/$1
        </script>
    </action>"
}

if [[ -d "$output_dir" ]]; then
    for file in `ls $output_dir`; do
        build_node $file
    done
fi


echo '
<group title="读取、写入">
    <action auto-off="true" confirm="true" reload="page">
        <title>重新读取源文件</title>
        <desc>从/vendor/etc遍历并解密温控配置文件到'$output_dir'</desc>
        <script>
            echo "删除已解密的文件"
            rm -rf '$output_dir'
        </script>
    </action>

    <action confirm="true">
        <title>保存温控配置</title>
        <desc>加密'$output_dir'中的文件，回写到/vendor/etc目录</desc>
        <script>
            mount -o rw,remount / 2>/dev/null
            mount -o rw,remount /system 2>/dev/null
            mount -o rw,remount /vendor 2>/dev/null
            mount -o rw,remount /system/vendor 2>/dev/null
            '$programma -o="/cache/test-encrypt" -i="$output_dir" -d="false"'
            echo "你说巧不巧，我写到这里突然就不想写了..."
            echo "反正去/cache/test-encrypt目录下看..."
            ls /cache/test-encrypt
        </script>
    </action>
</group>'

echo '</group>'
echo '</root>'
