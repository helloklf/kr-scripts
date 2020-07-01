source $START_DIR/samples/thermal/path_config.sh

# 解密MIUI的温控文件

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