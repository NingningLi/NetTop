package com.novas.nettop;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by novas on 15/12/2.
 * 这个类是网络请求的的最终形式，所有的request最后都会转化为这个
 */
public class NetTopRequest implements Serializable
{
    /*
      在这里会进行get和post方法的区分，首先get方法是没有参数的，一般用来获取图片，根据reponsetype进行区分
     */
    private static final long serialVersionUID = -439476282014493612L;
    public String requesturl;
    public Map<String,String> dataParams;
    public File[] files;
    public int responseType;

    public NetTopRequest(String requesturl,Map<String,String> dataParams,File[] files)
    {
        this.requesturl=requesturl;
        this.dataParams=dataParams;
        this.files=files;
        if(this.dataParams==null||this.dataParams.isEmpty())
        {
            this.responseType=HttpResponseType.RESPONSE_TYPE_IMAGE;
        }
        else
        {
            this.responseType=HttpResponseType.RESPONSE_TYPE_TEXT;
        }
    }
    public void destroy()
    {
        if(requesturl!=null)
        {
            requesturl=null;
        }
        if(dataParams!=null)
        {
            dataParams=null;
        }
    }
}
