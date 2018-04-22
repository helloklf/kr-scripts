package com.projectkr.shell;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.Toast;

import com.projectkr.shell.action.ActionConfigReader;
import com.projectkr.shell.action.ActionListConfig;
import com.projectkr.shell.switchs.ActionInfo;
import com.projectkr.shell.switchs.SwitchConfigReader;
import com.projectkr.shell.switchs.SwitchListConfig;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //supportActionBar!!.elevation = 0f
            Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setTitle(R.string.app_name);

            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
            window.setNavigationBarColor(Color.WHITE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //getWindow().setNavigationBarColor(Color.WHITE);
        } catch (Exception ex) {
        }
        TabHost tabHost = findViewById(R.id.main_tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab0").setContent(R.id.main_tabhost_tab0).setIndicator("", getDrawable(R.drawable.shell)));
        tabHost.addTab(tabHost.newTabSpec("tab1").setContent(R.id.main_tabhost_tab1).setIndicator("", getDrawable(R.drawable.switchs)));

        new ActionListConfig(this).setListData(ActionConfigReader.readActionConfigXml(this));
        ArrayList<ActionInfo> actionInfos = SwitchConfigReader.readActionConfigXml(this);
        if (actionInfos != null && actionInfos.size() != 0) {
            new SwitchListConfig(this).setListData(actionInfos);
        } else {
            tabHost.getTabWidget().setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_menu_info: {
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                new AlertDialog.Builder(this)
                        //.setTitle("关于")
                        //.setMessage("以下脚本，均出自Kr-Yaodao Rom")
                        .setView(layoutInflater.inflate(R.layout.dialog_about, null))
                        .create()
                        .show();
                break;
            }
            case R.id.option_menu_reboot: {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.reboot_confirm)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Runtime.getRuntime().exec("reboot");
                                } catch (IOException e) {
                                    Toast.makeText(getApplicationContext(), R.string.reboot_fail, Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
