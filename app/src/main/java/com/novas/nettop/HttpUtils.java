package com.novas.nettop;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by novas on 15/12/5.
 */
public class HttpUtils
{
    //将输入流转化为字节数组
    public static byte[] ConvertInputStreamToBytes(InputStream is)throws Exception
    {
        byte[] bytes=new byte[1024];
        byte[] desbytes=new byte[1024];
        int length=0;
        int desloc=0;
        int lastlength=0;
        int k=0;
        while ((length=is.read(bytes))!=-1)
        {
            System.arraycopy(bytes,0,desbytes,desloc,length);
            byte[] temp=new byte[desbytes.length+1024];
            System.arraycopy(desbytes,0,temp,0,desbytes.length);
            desbytes=temp;
            k=desloc;
            desloc=desloc+length;
            lastlength=length;
        }
        bytes=new byte[k+lastlength];
        System.arraycopy(desbytes, 0, bytes, 0, bytes.length);
        is.close();
        desbytes=null;
        is=null;
        return bytes;
    }
    //将request转化为NetTopRequest这样的基本请求单元
    public static NetTopRequest parseDataToNetTopRequest(Request request)
    {
        Class cl=request.getClass();
        Field[] fields=cl.getDeclaredFields();
        Map<String,String> dataParams=new HashMap<>();
        String requestUrl=null;
        File[] files=null;
        for(int i=0;i<fields.length;i++)
        {
            fields[i].setAccessible(true);
            try
            {
                if(fields[i].getName().equals("requestUrl"))
                {
                    requestUrl=fields[i].get(request).toString();
                }
                else if(fields[i].getName().equals("files"))
                {
                    files=(File[])fields[i].get(request);
                }
                else
                {
                    dataParams.put(fields[i].getName(),fields[i].get(request).toString());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return new NetTopRequest(requestUrl,dataParams,files);
    }
}
