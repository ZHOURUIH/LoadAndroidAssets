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
import java.util.List;

public class MainActivity extends UnityPlayerActivity {
    AssetManager mAssetManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAssetManager = getAssets();
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
    public boolean isDirExist(String path)
    {
        File dir = new File(path);
        if(!dir.isDirectory())
        {
            return false;
        }
        return dir.exists();
    }
    public boolean isFileExist(String path)
    {
        File dir = new File(path);
        if(!dir.isFile())
        {
            return false;
        }
        return dir.exists();
    }
    public int getFileSize(String path)
    {
        File file = new File(path);
        if(!file.isFile())
        {
            return 0;
        }
        try
        {
            FileInputStream fileStream = new FileInputStream(file);
            return fileStream.available();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return 0;
        }
    }
    public void findFiles(String path, List<String> fileList, List<String> patterns, boolean recursive)
    {
        File folder = new File(path);
        if(!path.endsWith("/"))
        {
            path += "/";
        }
        File[] fileInfoList = folder.listFiles();
        int fileCount = fileInfoList.length;
        int patternCount = patterns != null ? patterns.size() : 0;
        for (int i = 0; i < fileCount; ++i)
        {
            File file = fileInfoList[i];
            String fileName = file.getName();
            if(file.isFile())
            {
                // 如果需要过滤后缀名,则判断后缀
                if (patternCount > 0)
                {
                    for (int j = 0; j < patternCount; ++j)
                    {
                        if (endsWith(fileName, patterns.get(j), false))
                        {
                            fileList.add(path + fileName);
                        }
                    }
                }
                // 不需要过滤,则直接放入列表
                else
                {
                    fileList.add(path + fileName);
                }
            }
            else if(file.isDirectory())
            {
                // 查找所有子目录
                if (recursive)
                {
                    findFiles(path + fileName, fileList, patterns, recursive);
                }
            }
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
    protected boolean endsWith(String str, String pattern, boolean caseSensitive)
    {
        if(caseSensitive)
        {
            String newStr = str.toLowerCase();
            return newStr.endsWith(pattern.toLowerCase());
        }
        else
        {
            return str.endsWith(pattern);
        }
    }
}
