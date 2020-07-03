function config_path() {
    echo '这是3.9.2新加入的全新变量'
    echo '它表示的是配置XML存储路径'
    echo ''

    echo 'PAGE_CONFIG_DIR [配置XML来源目录]'
    echo $PAGE_CONFIG_DIR
    echo ''

    echo 'PAGE_CONFIG_FILE [配置XML来源路径]'
    echo $PAGE_CONFIG_FILE
    echo ''

    echo 'PAGE_WORK_DIR [配置XML提取目录]'
    echo $PAGE_WORK_DIR
    echo ''

    echo 'PAGE_WORK_FILE [配置XML提取目录]'
    echo $PAGE_WORK_FILE
    echo ''
}

$1
