package com.zhourui.micromahjong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.*;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.AsynchronousFileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends UnityPlayerActivity
{
    AssetManager mAssetManager;
    public AssetLoader mAssetLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAssetManager = getAssets();
        mAssetLoader = new AssetLoader();
        mAssetLoader.setAssetManager(getAssets());
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
    public void shutdown()
    {
       try
       {
           Process proc =Runtime.getRuntime().exec(new String[]{"/system/xbin/su","-c","reboot -p"});  //关机
           try {
               int ret = proc.waitFor();
               Log.i("Mirror", "关机执行结果：" + ret);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
       catch (IOException e)
       {
           e.printStackTrace();
       }
    }
}
