#!/system/bin/sh

package_dir="$START_DIR/w"
extract_dir="$package_dir"

if [[ -f "$extract_dir/favorites.xml" ]]; then
    echo "w/favorites.xml"
fi
