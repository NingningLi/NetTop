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
            int i=0;
            int j=0;
            System.out.println(new String(bytes,0,length,"gbk"));
            //  byte[] temp=new byte[0];
            int bytelength=0;
            for(i=0;i<length-1;i++)
            {
                if(bytes[i]==13&&bytes[i+1]==10)
                {
                    if(bytes[i+2]==13&&bytes[i+3]==10&&bytes[i+4]==13&&bytes[i+5]==10)
                    {
                        j=i+6;
                        System.out.println("===="+bytes[i+2]+"  "+bytes[i+3]+"  "+bytes[i+4]+"  "+bytes[i+5]+" "+bytes[i+6]);
                        break;
                    }
                    b=new byte[i-loc];
                  //  System.out.println("loc=" + loc + "i=" + i + "  " + bytes[i] + " " + length);
                    System.arraycopy(bytes, loc, b, 0,b.length);
                    loc=i+2;
                    // System.out.println(new String(b,"gbk"));
                    String s=new String(b,"gbk");
                    System.out.println("s=" + s);
                    if(s.contains("Content-Length"))
                    {
                        String[] args=s.split(":");
                        bytelength=Integer.parseInt(args[1].trim());
                    }
                }
            }
            int templength=length-j;
         //   System.out.println("bytelength="+bytelength+"j="+j+"templength="+templength);
         //   System.out.println("=====8888="+bytes[length-4]+"  "+bytes[length-3]+"  "+bytes[length-2]+"   "+bytes[length-1]);
            byte[] dest=new byte[bytelength-2];
            loc=0;
            System.arraycopy(bytes, j, dest, 0, templength);
       //     System.out.println("string=" + new String(dest,0,templength, "gbk"));
            while (templength<dest.length)
            {
                bytes=new byte[1024*1024];
                length=is.read(bytes);
                ///  System.out.println("length="+templength);
               // System.out.println("read=" + new String(bytes,0,length,"gbk"));
             //   for(int l=0;l<length;l++)
              //  {
               //     dest[l+templength]=bytes[l];
               // }
                System.arraycopy(bytes, 0, dest, templength, length);
                templength=templength+length;
                //  System.out.println("string=" + new String(dest,0,templength-500, "gbk"));
              //  System.out.println("string=" + new String(dest,0,templength, "gbk"));
               // System.out.println("length    ="+templength);
            }//
            byte[] desttemp=new byte[dest.length-2];
            System.arraycopy(dest,0,desttemp,0,desttemp.length);
            response=new HttpResponse(desttemp);
          //  System.out.println("string=" + new String(dest, "gbk"));
           // listener.onSuccess(response);
            center.postSuccess(response,listener);
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
