package com.zhourui.micromahjong;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends UnityPlayerActivity {
    AssetManager mAssetManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAssetManager = getAssets();
    }
    public boolean isAssetExist(String path)
    {
        try
        {
            InputStream inputStream = mAssetManager.open(path);
            return inputStream != null;
        }
        catch (IOException e)
        {
            unityLog("isAssetExist exception : " + e.getStackTrace());
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
            return streamToBytes(inputStream, (int)length);
        }
        catch (IOException e)
        {
            unityLog("loadAsset exception : " + e.getStackTrace());
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
            unityLog("loadTxtAsset exception : " + e.getStackTrace());
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
    public String loadTxtFile(String path)
    {
        byte[] buffer = loadFile(path);
        try
        {
            String str = new String(buffer, "UTF-8");
            return str;
        }
        catch (Exception e)
        {
            unityLog("loadTxtFile exception : " + e.getStackTrace());
        }
        return "";
    }
    public byte[] loadFile(String path)
    {
        File file = new File(path);
        if(!file.exists())
        {
            unityError("can not find file : " + path);
            return null;
        }
        try
        {
            FileInputStream fileStream = new FileInputStream(file);
            int fileSize = fileStream.available();
            return streamToBytes(fileStream, fileSize);
        } catch (IOException e)
        {
            unityError("load file exception : " + e.getStackTrace());
            return null;
        }
    }
    public void writeFile(String path, byte[] buffer, int writeCount, boolean appendData)
    {
        try
        {
            File file = new File(path);
            FileInputStream fileStream = new FileInputStream(file);
            int fileSize = fileStream.available();
            FileOutputStream outputStream = new FileOutputStream(file);
            int offset = appendData ? fileSize : 0;
            outputStream.write(buffer, offset, writeCount);
            outputStream.close();
            fileStream.close();
        }
        catch (IOException e)
        {
            unityError("writeFile exception : " + e.getStackTrace());
        }
    }
    public void writeTxtFile(String path, String str, boolean appendData)
    {
        try
        {
            byte[] bytes = str.getBytes("UTF-8");
            writeFile(path, bytes, bytes.length, appendData);
        }
        catch (UnsupportedEncodingException e)
        {
            unityError("writeTxtFile exception : " + e.getStackTrace());
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
            unityError("getFileSize exception : " + e.getStackTrace());
            return 0;
        }
    }
    public List<String> startFindFiles(String path, String patterns, boolean recursive)
    {
        List<String> fileList = new ArrayList<String>();
        String[] patternList = patterns.split(" ");
        findFiles(path, fileList, patternList, recursive);
        return fileList;
    }
    public String nextFile(List<String> fileList, int index)
    {
        int count = fileList.size();
        if(index >= 0 && index < count)
        {
            return fileList.get(index);
        }
        return "";
    }
    public void createDirectory(String path)
    {
        File file = new File(path);
        if(!file.exists())
        {
            file.mkdir();
        }
    }
    public void unityLog(String info)
    {
        UnityPlayer.UnitySendMessage("UnityLog", "log", info);
    }
    public void unityError(String info) {UnityPlayer.UnitySendMessage("UnityLog", "logError", info);}
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    private byte[] streamToBytes(InputStream inputStream, int length)
    {
        try
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte buf[] = new byte[length];
            int len = inputStream.read(buf);
            outputStream.write(buf, 0, len);
            outputStream.close();
            inputStream.close();
            byte[] ret = outputStream.toByteArray();
            return ret;
        }
        catch(Exception e)
        {
            unityError("streamToBytes exception : " + e.getStackTrace());
           return null;
        }
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
    protected void findFiles(String path, List<String> fileList, String[] patterns, boolean recursive)
    {
        File folder = new File(path);
        if(!path.endsWith("/"))
        {
            path += "/";
        }
        File[] fileInfoList = folder.listFiles();
        int fileCount = fileInfoList.length;
        int patternCount = patterns != null ? patterns.length : 0;
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
                        if (endsWith(fileName, patterns[j], false))
                        {
                            fileList.add(path + fileName);
                            break;
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
                            if (endsWith(assetName, patterns[j], false))
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
                        findFiles(path + assetName, fileList, patterns, recursive);
                    }
                }
            }
        }
        catch(Exception e)
        {
            unityError("findAssets exception : " + e.getStackTrace());
        }
    }
}
