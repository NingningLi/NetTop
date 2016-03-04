package com.novas.nettop;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
        if(request.responseType==HttpResponseType.RESPONSE_TYPE_TEXT)
        {
            startHttpTextConnection(request);
        }
        else
        {
            startHttpImageDownloadConnection(request);
        }
    }
    //从服务器获取图片,get情况
    public HttpResponse startHttpImageDownloadConnection(NetTopRequest request)
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
    public HttpResponse startHttpTextConnection(NetTopRequest request) {
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
