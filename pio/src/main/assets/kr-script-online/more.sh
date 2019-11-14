#!/system/bin/sh

package_dir="$START_DIR/w"
extract_dir="$package_dir"

if [[ -f "$extract_dir/more.xml" ]]; then
    echo "w/more.xml"
fi