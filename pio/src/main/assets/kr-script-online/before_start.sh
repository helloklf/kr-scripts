#!/system/bin/sh

varsion_code="2"
package_dir="$START_DIR/w"
extract_dir="$package_dir"
package_file="$package_dir/version_$varsion_code.zip"
remote_url="https://vtools.oss-cn-beijing.aliyuncs.com/pio/version_$varsion_code.gz"

function download_last_version() {
    echo '【before_start_sh 功能演示】'
    sleep 1
    echo ' '

    echo '■ 清理旧的文件'

    mkdir -p $package_dir
    rm -rf $package_dir
    mkdir -p $package_dir

    echo '■ 下载资源文件'

    curl -o "$package_file" $remote_url
    echo '■ 资源下载完毕'

    echo '■ 解压资源文件'
    mkdir -p "$extract_dir"
    tar -zxf "$package_file" -C "$extract_dir"
    chmod 755 -R "$extract_dir"
    find "$extract_dir" -name "*.sh" | xargs dos2unix 1>/dev/null

    echo '■ 在线更新完成'
    sleep 1
}

if [[ ! -f "$package_file" ]]; then
    download_last_version
fi
