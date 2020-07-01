# 解密MIUI的温控文件
file_name="miui-thermal"
remote_url="https://vtools.oss-cn-beijing.aliyuncs.com/pio/$file_name"
download_dir="$START_DIR/thermal"
programma="$download_dir/$file_name"
output_dir="/sdcard/backups/miui-thermal/outputs"

if [[ ! -f "$programma" ]];
then
    mkdir -p "$download_dir"
    curl -o "$programma" $remote_url
fi

chmod -R 755 $download_dir
chown -R $APP_USER_ID:$APP_USER_ID $download_dir

if [[ -f "$programma" ]] && [[ ! -d "$output_dir" ]]; then
    mkdir -p "$output_dir"
    $programma -o="$output_dir" -d="true" -i="/vendor/etc"
fi