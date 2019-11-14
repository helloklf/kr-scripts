#!/system/bin/sh

package_dir="$START_DIR/kr-script-online"
extract_dir="$package_dir"

if [[ -f "$extract_dir/favorites.xml" ]]; then
    echo "kr-script-online/favorites.xml"
fi
