if [[ -d $START_DIR ]]; then
    chmod -R 755 $START_DIR
    chown -R $APP_USER_ID:$APP_USER_ID $START_DIR
fi
