package com.novas.nettop;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by novas on 15/12/2.
 */
public class BaseNetTopBusiness {
    HttpURLConnection httpURLConnection;
    URL url;
    OutputStream outputStream;
    public NetTopListener listener;
    eventcenter center=null;
    Object object=new Object();
    public BaseNetTopBusiness(NetTopListener listener)
    {
        center=eventcenter.getCenterInstance();
        this.listener=listener;
    }
    //开启Http请求，会根据结果调用listener的方法.
    public void  startRequest(Request request)
    {
       final NetTopRequest netTopRequest=HttpUtils.parseDataToNetTopRequest(request);

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                startHttpUrlConnection(netTopRequest);
            }
        };
        center.execute(runnable);
    }
    public void  startHttpUrlConnection(NetTopRequest request)
    {
        if(request.files==null)
        {
            startSocketConnection(request);
        }
        else
        {
            if(request.responseType==HttpResponseType.REQUSET_WITH_PARAMS)
            {
                startHttpParamsConnection(request);
            }
            else
            {
                startHttpNoParamsConnection(request);
            }
        }
    }
    public HttpResponse startSocketConnection(NetTopRequest request)
    {
        System.out.println("socket");
        HttpResponse response=null;
        URL url = null;
        try {
            url = new URL(request.requesturl);
            int port = url.getPort()==-1 ? 80 : url.getPort();
            Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
            OutputStream oos=socket.getOutputStream();
            HttpHeaderBuilder httpHeaderBuilder = HttpHeaderBuilder.getHttpHeaderBuilderInstance();
            httpHeaderBuilder.buildSocket(request.dataParams, oos, url);
            InputStream is=socket.getInputStream();
            byte[] bytes=new byte[1024*1024];
            int length=is.read(bytes);
            int loc=0;
            byte[] b=null;
            for(int i=0;i<length-1;i++)
            {
                if(bytes[i]==13&&bytes[i+1]==10)
                {
                    System.out.println("loc="+loc+"i="+i+"  "+bytes[i]);
                    b=new byte[i-loc];
                    System.out.println(b.length);
                    System.arraycopy(bytes,loc,b,0,b.length);
                    loc=i+2;
                    System.out.println(new String(b,"gbk"));
                }
            }
            response=new HttpResponse(b);
            center.postSuccess(response,listener);
            /*
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String str = reader.readLine();
            StringBuilder sb=new StringBuilder();
            int length=0;
            if(str.contains("200"))
            {
                while (str!=null&&str.length()>0)
                {
                    System.out.println(str);
                    if(str.contains("Content-Length"))
                    {
                        String[] args=str.split(":");
                        length=Integer.parseInt(args[1].trim());
                    }
                    str=reader.readLine();
                }
                System.out.println(length);
                byte[] chars=new byte[length];
                reader.read(chars);
                socket.close();
                int end=0;
                if((int)chars[length-2]==13&&(int)chars[length-1]==10)
                {
                    end=length-2;
                }
                else
                {
                    end=length;
                }
                byte[] bytes=new String(chars,0,end).getBytes("gbk");
                response=new HttpResponse(bytes);
                center.postSuccess(response,listener);
            }
            else
            {
                socket.close();
                center.postFail(object,listener);
            }
            */
        } catch (MalformedURLException e) {
            e.printStackTrace();
            center.postError(object, listener);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            center.postError(object, listener);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            center.postError(object, listener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            center.postError(object, listener);
        }
        return response;
    }
    //从服务器获取图片,get情况
    public HttpResponse startHttpNoParamsConnection(NetTopRequest request)
    {
        HttpResponse response=null;
        try {
            url = new URL(request.requesturl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode()==200)
            {
                InputStream is = httpURLConnection.getInputStream();
                byte[] bytes = HttpUtils.ConvertInputStreamToBytes(is);
                response=new HttpResponse(bytes);
               // listener.onSuccess(response);
                center.postSuccess(response,listener);
            }
            else
            {
                //listener.onFail();
                center.postFail(object,listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
           // listener.onError();
               center.postError(object,listener);
        }
        return response;
    }
    //post情况
    public HttpResponse startHttpParamsConnection(NetTopRequest request) {
        HttpResponse response=null;
        try {
            url = new URL(request.requesturl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            HttpHeaderBuilder httpHeaderBuilder = HttpHeaderBuilder.getHttpHeaderBuilderInstance();
            httpHeaderBuilder.wrapHttpUrlConnection(httpURLConnection);
            httpURLConnection.connect();

            outputStream = httpURLConnection.getOutputStream();
            httpHeaderBuilder.build(request.files, request.dataParams, outputStream);
            outputStream.flush();
            outputStream.close();
            if(httpURLConnection.getResponseCode()==200)
            {
                InputStream is = httpURLConnection.getInputStream();
                byte[] bytes = HttpUtils.ConvertInputStreamToBytes(is);
                response=new HttpResponse(bytes);
               // listener.onSuccess(response);
                System.out.println("post success");
                center.postSuccess(response,listener);
            }
            else
            {
               // listener.onFail();
                center.postFail(object,listener);
            }

        } catch (Exception e) {
                e.printStackTrace();
                //listener.onError();
                center.postError(object,listener);
        }
        return response;
    }
}
