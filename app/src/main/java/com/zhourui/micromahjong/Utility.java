package com.zhourui.micromahjong;

import com.unity3d.player.UnityPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.arraycopy;

public class Utility
{
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static String printHexBinary(byte[] data, int length, boolean space)
    {
        StringBuilder r = new StringBuilder(data.length * 2);
        int i = 0;
        for (byte b : data)
        {
            if(i++ >= length)
            {
                break;
            }
            r.append(hexCode[(b >> 4) & 0x0F]);
            r.append(hexCode[(b & 0x0F)]);
            if(space)
            {
                r.append(" ");
            }
        }
        return r.toString();
    }
    public static String loadTxtFile(String path)
    {
        byte[] buffer = loadFile(path);
        try
        {
            String str = new String(buffer, "UTF-8");
            return str;
        }
        catch (Exception e)
        {
            unityLog("loadTxtFile exception :" + path + ", message:" + e.getMessage() + ", stack:" + e.getStackTrace());
        }
        return "";
    }
    public static byte[] loadFile(String path)
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
            unityError("load file exception : " + path + ", message:" + e.getMessage() + ", stack:" + e.getStackTrace());
            return null;
        }
    }
    public static void writeFile(String path, byte[] buffer, int writeCount, boolean appendData)
    {
        try
        {
            createFile(path);
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
            unityError("writeFile exception : " + path + ", message:" + e.getMessage() + ", stack:" + e.getStackTrace());
        }
    }
    public static void writeTxtFile(String path, String str, boolean appendData)
    {
        try
        {
            byte[] bytes = str.getBytes("UTF-8");
            writeFile(path, bytes, bytes.length, appendData);
        }
        catch (UnsupportedEncodingException e)
        {
            unityError("writeTxtFile exception : " + path + ", message:" + e.getMessage() + ", stack:" + e.getStackTrace());
        }
    }
    public static void createFile(String path)
    {
        if(isFileExist(path))
        {
            return;
        }
        try
        {
            File file = new File(path);
            file.createNewFile();
        }
        catch (IOException e)
        {
            unityError("createFile exception : " + path + ", message:" + e.getMessage() + ", stack:" + e.getStackTrace());
        }
    }
    public static boolean isDirExist(String path)
    {
        File dir = new File(path);
        if(!dir.isDirectory())
        {
            return false;
        }
        return dir.exists();
    }
    public static boolean isFileExist(String path)
    {
        File dir = new File(path);
        if(!dir.isFile())
        {
            return false;
        }
        return dir.exists();
    }
    public static int getFileSize(String path)
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
            unityError("getFileSize exception : " + path + ", message:" + e.getMessage() + ", stack:" + e.getStackTrace());
            return 0;
        }
    }
    public static List<String> startFindFiles(String path, String patterns, boolean recursive)
    {
        List<String> fileList = new ArrayList<String>();
        String[] patternList = patterns.split(" ");
        findFiles(path, fileList, patternList, recursive);
        return fileList;
    }
    public static String nextFile(List<String> fileList, int index)
    {
        int count = fileList.size();
        if(index >= 0 && index < count)
        {
            return fileList.get(index);
        }
        return "";
    }
    public static void createDirectory(String path)
    {
        File file = new File(path);
        if(!file.exists())
        {
            file.mkdir();
        }
    }
    public static byte[] streamToBytes(InputStream inputStream, int length)
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
            unityError("streamToBytes exception : message:" + e.getMessage() + ", stack:" + e.getStackTrace());
            return null;
        }
    }
    public static boolean endsWith(String str, String pattern, boolean caseSensitive)
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
    public static void findFiles(String path, List<String> fileList, String[] patterns, boolean recursive)
    {
        File folder = new File(path);
        if(!path.endsWith("/"))
        {
            path += "/";
        }
        File[] fileInfoList = folder.listFiles();
        if(fileInfoList == null)
        {
            return;
        }
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
    public static String checkNumber(String str)
    {
        String ret = "";
        int count = str.length();
        for(int i = 0; i < count; ++i)
        {
            char c = str.charAt(i);
            if(c >= '0' && c <= '9' || c == '-' || c == '.')
            {
                ret += c;
            }
        }
        return ret;
    }
    public static void unityLog(String info)
    {
        UnityPlayer.UnitySendMessage("UnityLog", "log", info);
    }
    public static void unityError(String info)
    {
        UnityPlayer.UnitySendMessage("UnityLog", "logError", info);
    }
}