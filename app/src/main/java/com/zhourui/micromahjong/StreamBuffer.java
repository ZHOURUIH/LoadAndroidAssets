package com.zhourui.micromahjong;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import static java.lang.System.arraycopy;

public class StreamBuffer
{
    protected int mBufferSize;
    protected byte[] mBuffer;
    protected int mDataLength;
    public StreamBuffer(int bufferSize)
    {
        resizeBuffer(bufferSize);
    }
    public byte[] getData()
    {
        return mBuffer;
    }
    public int getDataLength()
    {
        return mDataLength;
    }
    public void merge(StreamBuffer stream)
    {
        addDataToInputBuffer(stream.getData(), stream.getDataLength());
    }
    public void addDataToInputBuffer(byte[] data, int count)
    {
        // 缓冲区足够放下数据时才处理
        if (count <= mBufferSize - mDataLength)
        {
            arraycopy(data,  0, mBuffer, mDataLength, count);
            mDataLength += count;
        }
    }
    public void removeDataFromInputBuffer(int start, int count)
    {
        if (mDataLength >= start + count)
        {
            arraycopy(mBuffer, start + count, mBuffer, start, mDataLength - start - count);
            mDataLength -= count;
        }
    }
    public void clearInputBuffer()
    {
        mDataLength = 0;
    }
    //-------------------------------------------------------------------------------------------------------------
    protected void resizeBuffer(int size)
    {
        if (mBufferSize >= size)
        {
            return;
        }
        mBufferSize = size;
        if (mBuffer != null)
        {
            // 创建新的缓冲区,将原来的数据拷贝到新缓冲区中,销毁原缓冲区,指向新缓冲区
            byte[] newBuffer = new byte[mBufferSize];
            if (mDataLength > 0)
            {
                arraycopy(mBuffer, 0, newBuffer, 0, mDataLength);
            }
            mBuffer = newBuffer;
        }
        else
        {
            mBuffer = new byte[mBufferSize];
        }
    }
}