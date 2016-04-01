package com.novas.nettop;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by novas on 15/12/3.
 */
public class HttpHeaderBuilder {
    private static HttpHeaderBuilder httpHeaderBuilder;
    String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成
    String PREFIX = "--" , LINE_END = "\r\n";
    String CONTENT_TYPE = "multipart/form-data"; //内容类型
    private HttpHeaderBuilder()
    {

    }
    /*
      设置允许进行输入输出content-type是必须进行设置的
     */
    public  void wrapHttpUrlConnection(HttpURLConnection httpURLConnection)
    {
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setChunkedStreamingMode(1024);
       // httpURLConnection.setRequestProperty("connection", "keep-alive");
        httpURLConnection.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
    }
    public static HttpHeaderBuilder getHttpHeaderBuilderInstance()
    {
        if(httpHeaderBuilder==null)
        {
            httpHeaderBuilder=new HttpHeaderBuilder();
        }
        return httpHeaderBuilder;
    }
    //支持传输多个文件和对应的字符串参数
    public void build(File file,OutputStream stream)throws Exception
    {
        build(new File[]{file},null,stream);
    }
    public void build(File file,Map<String,String> map,OutputStream stream)throws Exception
    {
        build(new File[]{file},map,stream);
    }
    public void build(Map<String,String> map,OutputStream outputStream)throws Exception
    {
        build(new File[]{},map,outputStream);
    }
    public byte[] convertFileToByte(File file)throws Exception
    {
        InputStream is = new FileInputStream(file);
        byte[] bytes=HttpUtils.ConvertInputStreamToBytes(is);
        return bytes;
    }
    /*

    将文件，map中包含的post参数依次写入到输出流中，http协议头包含固定的格式
    每个单元开头是

    --边界标识符\r\n
    Content-......\r\n
    Content-......\r\n
    \r\n
    文件内容
    \r\n

    以上是一个http请求头的格式，当所有的参数写入完成之后，写入文件结束标识符
    --边界标识符--\r\n

    http协议结束
     */
    public void build(File[] files,Map<String,String> map,OutputStream outputStream)throws Exception
    {
        String LINE=PREFIX+BOUNDARY+LINE_END;
        if(files!=null)
        {
            for(File file:files)
            {
                System.out.println("添加图片");
                if(file!=null)
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append(LINE);
                    sb.append("Content-Disposition: form-data; name=\"img\"; filename="+file.getName()+"\""+LINE_END);
                    sb.append("Content-Type: application/octet-stream;"+LINE_END);
                    sb.append(LINE_END);
                    byte[] headerbytes=sb.toString().getBytes();
                    byte[] bytes=convertFileToByte(file);

                    byte[] end_data = (LINE_END).getBytes();
                    byte[] all=new byte[headerbytes.length+bytes.length+end_data.length];
                    System.arraycopy(headerbytes,0,all,0,headerbytes.length);
                    System.arraycopy(bytes, 0, all, headerbytes.length, bytes.length);
                    System.arraycopy(end_data, 0, all, headerbytes.length + bytes.length, end_data.length);
                    outputStream.write(all);
                }
            }
        }

        for(Map.Entry<String,String> entry:map.entrySet())
        {
            StringBuffer sb = new StringBuffer();
            sb.append(LINE);
            sb.append("Content-Disposition: form-data; name="+entry.getKey() + LINE_END);
           // sb.append(LINE_END);
            sb.append(entry.getValue());
            sb.append(LINE_END);
            outputStream.write(sb.toString().getBytes());
        }
        byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
        outputStream.write(end_data);
    }
    public void buildSocket(Map<String,String> map,OutputStream outputStream,URL url)throws Exception
    {
        StringBuilder sb=new StringBuilder();
        String params="";
        for(Map.Entry<String,String> entry:map.entrySet())
        {
            params=params+"&"+entry.getKey()+"="+entry.getValue();
        }
        params=params.substring(1);
        System.out.println("params=" + params);
        sb.append("POST "+url.getPath()+" HTTP/1.1\r\n");
        int port = url.getPort()==-1 ? 80 : url.getPort();
        sb.append("HOST: "+url.getHost()+":"+port+"\r\n");
        sb.append("Content-Length: "+params.length()+"\r\n");
        sb.append("Content-Type: application/x-www-form-urlencoded\r\n");
        sb.append("\r\n");
        sb.append(params+"\r\n");
        sb.append("\r\n");
        sb.append("\r\n");
        outputStream.write(sb.toString().getBytes());
    }
}
