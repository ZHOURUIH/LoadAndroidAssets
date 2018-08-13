package com.zhourui.micromahjong;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.unity3d.player.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.AsynchronousFileChannel;

public class MainActivity extends UnityPlayerActivity {
    AssetManager mAssetManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAssetManager = getAssets();
    }
    public int add(int a, int b)
    {
        return a + b;
    }
    //读取assetbund并且返回字节数组
    public byte[] loadAsset(String path)
    {
        try
        {
            InputStream inputStream = mAssetManager.open(path);
            if(inputStream == null)
            {
                unityError("can not open file : " + path);
                return null;
            }
            long length = mAssetManager.openFd(path).getLength();
            return streamToBytes(inputStream, (int)length);
        }
        catch (IOException e)
        {
            unityLog(e.getMessage());
            return null;
        }
    }
    public String loadTxtAsset(String path)
    {
        try
        {
            byte[] buffer = loadAsset(path);
            String str = new String(buffer, "UTF-8");
            return str;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    public String loadTxtFile(String path)
    {
        byte[] buffer = loadFile(path);
        try
        {
            String str = new String(buffer, "UTF-8");
            return str;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    public byte[] loadFile(String path)
    {
        unityError(path);
        File file = new File(path);
        if(!file.exists())
        {
            return null;
        }
        try
        {
            FileInputStream fileStream = new FileInputStream(file);
            int fileSize = fileStream.available();
            return streamToBytes(fileStream, fileSize);
        } catch (IOException e)
        {
            unityError("load file error : " + e.getMessage());
            return null;
        }
    }
    public void unityLog(String info)
    {
        UnityPlayer.UnitySendMessage("UnityLog", "log", info);
    }
    public void unityError(String info)
    {
        UnityPlayer.UnitySendMessage("UnityLog", "logError", info);
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private byte[] streamToBytes(InputStream inputStream, int length)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[length];
        try
        {
            int len;
            while ((len = inputStream.read(buf)) != -1)
            {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        }
        catch (IOException e)
        {
            unityLog(e.getMessage());
        }
        return outputStream.toByteArray();
    }
}
