#!/system/bin/sh

varsion_code="1"
package_dir="$START_DIR/kr-script-online"
extract_dir="$package_dir"
package_file="$package_dir/version_$varsion_code.zip"
remote_url="https://vtools.oss-cn-beijing.aliyuncs.com/pio/version_1.gz"

if [[ -f "$extract_dir/favorites.xml" ]]; then
    echo "kr-script-online/favorites.xml"
fi
