package com.zhourui.micromahjong;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayerActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AssetLoader
{
    AssetManager mAssetManager;
    public void setAssetManager(AssetManager assetManager){mAssetManager = assetManager;}
    public boolean isAssetExist(String path)
    {
        try
        {
            InputStream inputStream = mAssetManager.open(path);
            return inputStream != null;
        }
        catch (IOException e)
        {
            unityLog("isAssetExist exception : " + path + ", message:" + e.getMessage() + ", stack:" + e.getStackTrace());
            return false;
        }
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
            return Utility.streamToBytes(inputStream, (int)length);
        }
        catch (IOException e)
        {
            unityLog("loadAsset exception : " + path + ", message:" + e.getMessage() + ", stack:" + e.getStackTrace());
            return null;
        }
    }
    public String loadTxtAsset(String path)
    {
        try
        {
            byte[] buffer = loadAsset(path);
            if(buffer != null)
            {
                String str = new String(buffer, "UTF-8");
                return str;
            }
            else
            {
                unityError("buffer is null!");
                return "";
            }
        }
        catch (Exception e)
        {
            unityLog("loadTxtAsset exception : " + path + ", message:" + e.getMessage() + ", stack:" + e.getStackTrace());
            return "";
        }
    }
    public List<String> startFindAssets(String path, String patterns, boolean recursive)
    {
        List<String> fileList = new ArrayList<String>();
        String[] patternList = patterns.split(" ");
        findAssets(path, fileList, patternList, recursive);
        return fileList;
    }
    public String nextAsset(List<String> assetList, int index)
    {
        int count = assetList.size();
        if(index >= 0 && index < count)
        {
            return assetList.get(index);
        }
        return "";
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------
    // 以下函数只用于persistentDataPath的读写
    public static String loadTxtFile(String path)
    {
        return Utility.loadTxtFile(path);
    }
    public static byte[] loadFile(String path)
    {
        return Utility.loadFile(path);
    }
    public static void writeFile(String path, byte[] buffer, int writeCount, boolean appendData)
    {
        Utility.writeFile(path, buffer, writeCount, appendData);
    }
    public static void writeTxtFile(String path, String str, boolean appendData)
    {
        Utility.writeTxtFile(path, str, appendData);
    }
    public static boolean isDirExist(String path)
    {
        return Utility.isDirExist(path);
    }
    public static boolean isFileExist(String path)
    {
        return Utility.isFileExist(path);
    }
    public static int getFileSize(String path)
    {
       return Utility.getFileSize(path);
    }
    public static List<String> startFindFiles(String path, String patterns, boolean recursive)
    {
        return Utility.startFindFiles(path, patterns, recursive);
    }
    public static String nextFile(List<String> fileList, int index)
    {
        return Utility.nextFile(fileList, index);
    }
    public static void deleteFile(String path){Utility.deleteFile(path);}
    public static void createDirectory(String path)
    {
        Utility.createDirectory(path);
    }
    public static void unityLog(String info)
    {
        Utility.unityLog(info);
    }
    public static void unityError(String info)
    {
        Utility.unityError(info);
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    protected void findAssets(String path, List<String> fileList, String[] patterns, boolean recursive)
    {
        try
        {
            String[] assetList = mAssetManager.list(path);
            if(!path.endsWith("/"))
            {
                path += "/";
            }
            int fileCount = assetList.length;
            int patternCount = patterns != null ? patterns.length : 0;
            for(int i = 0; i < fileCount; ++i)
            {
                String assetName = assetList[i];
                // 包含后缀名的认为是文件,否则认为是文件夹,不考虑文件名不含后缀名的情况
                if(assetName.lastIndexOf(".") != -1)
                {
                    // 如果需要过滤后缀名,则判断后缀
                    if (patternCount > 0)
                    {
                        for (int j = 0; j < patternCount; ++j)
                        {
                            if (Utility.endsWith(assetName, patterns[j], false))
                            {
                                fileList.add(path + assetName);
                                break;
                            }
                        }
                    }
                    // 不需要过滤,则直接放入列表
                    else
                    {
                        fileList.add(path + assetName);
                    }
                }
                else
                {
                    // 查找所有子目录
                    if (recursive)
                    {
                        Utility.findFiles(path + assetName, fileList, patterns, recursive);
                    }
                }
            }
        }
        catch(Exception e)
        {
            unityError("findAssets exception : " + path + ", message:" + e.getMessage() + ", stack:" + e.getStackTrace());
        }
    }
}
