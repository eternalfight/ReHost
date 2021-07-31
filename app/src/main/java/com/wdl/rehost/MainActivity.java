package com.wdl.rehost;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.wdl.rehost.util.FileUtils;

public class MainActivity extends AppCompatActivity {
    private Button btn_click;
    private Button btn_open;
    private Button btn_install;
    private Button btn_uninstall;
    private ProgressBar progressBar;
    private Context mContext;

    private String furl = "http://wifi.lunyong.top:25555/?explorer/index/fileDownload&path=%7Bsource%3A26%7D%2F&accessToken=3945-A84hWU1_qoJC2KSfDo0soPnkF8EGlT9Rqv7uZyoP9tBFaI2-LQsKLdmxrxqjIQD5cX6R0AIkwygIw&modifyTime=1627736917&size=1895336&download=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getBaseContext();
        btn_click = findViewById(R.id.btn_click);
        btn_open = findViewById(R.id.b_open);
        btn_install = findViewById(R.id.btn_install);
        btn_uninstall = findViewById(R.id.btn_uninstall);
        progressBar = findViewById(R.id.progress);

        init();


        btn_click.setOnClickListener((view) -> {
            download();
        });
        btn_install.setOnClickListener((v -> {
            final ProgressDialog pd = ProgressDialog.show(MainActivity.this, "Installing...", "Please wait...", true, true);
            // FIXME: 仅用于安装流程演示 2017/7/24
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    //simulateInstallExternalPlugin();
                    PluginInfo info = RePlugin.install(getExternalCacheDir().getAbsolutePath() + "/plugin1.apk");
                    if (info != null) {
                        RePlugin.preload(info);
                    }
                    runOnUiThread(() -> {
                        if (info != null) {
                            Toast.makeText(mContext, "success install", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "install fail", Toast.LENGTH_LONG).show();
                        }
                    });

                    pd.dismiss();
                }
            }, 1000);

        }));
        btn_uninstall.setOnClickListener(v -> {
            RePlugin.uninstall("com.wdl.replugin1");
        });
        btn_open.setOnClickListener((view) -> {
            if (RePlugin.isPluginInstalled("com.wdl.replugin1")) {
                RePlugin.startActivity(MainActivity.this, RePlugin.createIntent("com.wdl.replugin1",
                        "com.wdl.replugin1.MainActivity"));
            } else {
                Toast.makeText(MainActivity.this, "You must install Plugin1 first!", Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void init() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void download() {
        Log.i("MainActivity", "download: " + getExternalCacheDir().getAbsolutePath());
        progressBar.setProgress(0);
        FileUtils.downLoadFile(furl, getExternalCacheDir().getAbsolutePath(), new FileUtils.ReqProgressCallBack() {
            @Override
            public void onProgress(long total, long current) {
                progressBar.setProgress((int) current);
            }

            @Override
            public void successCallBack(String path) {
                runOnUiThread(() -> {
                    Toast.makeText(mContext, "success download", Toast.LENGTH_LONG).show();
                });


            }

            @Override
            public void onFail(String s) {

            }
        });

    }


}