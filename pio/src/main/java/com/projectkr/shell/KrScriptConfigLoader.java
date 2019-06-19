package com.projectkr.shell;

import android.content.Context;

import com.omarea.krscript.executor.ScriptEnvironmen;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

public class KrScriptConfigLoader {
    public final static String EXECUTOR_CORE = "EXECUTOR_CORE";
    public final static String PAGE_LIST_CONFIG = "PAGE_LIST_CONFIG";
    public final static String FAVORITE_CONFIG = "favorite_config";
    public final static String ALLOW_HOME_PAGE = "allow_home_page";

    private final String executorCore = "file:///android_asset/kr-script/executor.sh";
    private final String pageList = "file:///android_asset/kr-script/pages/page_list.xml";
    private final String favoriteConfig = "file:///android_asset/kr-script/pages/favorites.xml";
    private final String allowHomePage = "1";

    public HashMap<String, String> initFramework(Context context) {
        HashMap<String, String> configInfo = new HashMap<>();
        configInfo.put(EXECUTOR_CORE, executorCore);
        configInfo.put(PAGE_LIST_CONFIG, pageList);
        configInfo.put(FAVORITE_CONFIG, favoriteConfig);
        configInfo.put(ALLOW_HOME_PAGE, allowHomePage);

        try {
            InputStream inputStream = context.getAssets().open(context.getString(R.string.kr_script_config));
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String[] rows = new String(bytes, Charset.defaultCharset()).split("\n");
            for (String row : rows) {
                String rowText = row.trim();
                if (!rowText.startsWith("#") && rowText.contains("=")) {
                    int separator = rowText.indexOf("=");
                    String key = rowText.substring(0, separator);
                    String value = rowText.substring(separator + 2, rowText.length() - 1);
                    if (configInfo.containsKey(key)) {
                        configInfo.remove(key);
                    }
                    configInfo.put(key, value);
                }
            }
        } catch (Exception ex) {
        }

        ScriptEnvironmen.init(context, configInfo.get(EXECUTOR_CORE));

        return configInfo;
    }
}
