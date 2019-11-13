#!/system/bin/sh

varsion_code="1"
package_dir="$START_DIR/kr-script-online"
extract_dir="$package_dir"
package_file="$package_dir/version_$varsion_code.gz"
remote_url="https://vtools.oss-cn-beijing.aliyuncs.com/pio/version_1.gz"

function download_last_version() {
    echo '清理垃圾文件'

    mkdir -p $package_dir
    rm -rf $package_dir
    mkdir -p $package_dir

    echo '准备下载资源文件'

    curl -o "$package_file" $remote_url
    echo '文件下载结束'

    echo '解压资源文件'
    mkdir -p "$extract_dir"
    tar -zxf "$package_file" -C "$extract_dir"
    chmod 755 -R "$extract_dir"
    find "$extract_dir" -name "*.sh" | xargs dos2unix 1>/dev/null

    echo '完成！'
    sleep 1
}

if [[ ! -f "$package_file" ]]; then
    download_last_version
fi
