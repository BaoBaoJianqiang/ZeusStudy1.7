package jianqiang.com.plugin1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jianqiang.mypluginlibrary.PluginManager;
import com.example.jianqiang.mypluginlibrary.Utils;
import com.example.jianqiang.mypluginlibrary.ZeusBaseActivity;
import com.jianqiang.jnihelloworld.JniUtils;

import java.io.File;

public class TestActivity1 extends ZeusBaseActivity {
    private String apkName = "plugin1.apk";    //apk名称
    private String soFileName = "libhello.so";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        File extractFile = this.getFileStreamPath(apkName);
        String dexpath = extractFile.getPath();

//        String dexpath = "/data/user/0/jianqiang.com.hostapp/files/plugin1.apk";
//        String dexpathParent = "/data/user/0/jianqiang.com.hostapp/files/";
        String libPath = Utils.UnzipSpecificFile(dexpath, extractFile.getParent());

        System.load(libPath + "/" + soFileName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

        Button button = (Button) findViewById(R.id.btnGotoActivityA);
        button.setText(new JniUtils().getString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();

                    String activityName = "jianqiang.com.plugin1.ActivityA";
                    intent.setComponent(new ComponentName("jianqiang.com.plugin1", activityName));
                    intent.putExtra("UserName", "jianqiang");

                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}