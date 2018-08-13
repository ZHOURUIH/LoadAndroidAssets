package com.zhourui.micromahjong;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.unity3d.player.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private byte[] readTextBytes(InputStream inputStream, int length)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte [length];
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
    //读取assetbund并且返回字节数组
    public byte[] loadAB(String path)
    {
        InputStream inputStream = null;
        long length = 0;
        try
        {
            inputStream = mAssetManager.open(path);
            length = mAssetManager.openFd(path).getLength();
        }
        catch (IOException e)
        {
            unityLog(e.getMessage());
        }
        return readTextBytes(inputStream, (int)length);
    }
    public void unityLog(String info)
    {
        UnityPlayer.UnitySendMessage("UnityLog", "log", info);
    }
    public void unityError(String info)
    {
        UnityPlayer.UnitySendMessage("UnityLog", "logError", info);
    }
}
