package com.novas.nettop;

import java.util.ArrayList;

/**
 * Created by novas on 16/1/19.
 */
public class test {
    public static void main(String[] args)throws  Exception
    {
        /*
        String var1="12345";
        byte[] bytes=var1.getBytes("gbk");
        var1=new String(bytes,"utf-8");
        System.out.println(var1);
        int[] temp1=new int[10];
        for(int i=0;i<10000;i++)
        {
            if(i>temp1.length*2/3)
            {
                int[] temp=new int[temp1.length*2];
                System.arraycopy(temp1,0,temp,0,temp1.length);
                temp1=temp;
            }
            temp1[i]=i;
        }
        System.out.println(temp1.length);
        */
        String temp="33";
        String[] res=split(temp,',');
        for(int i=0;i<res.length;i++)
        {
            System.out.println(res[i]);
        }
    }
    public static String[] split(String args,char splitchar)
    {
        char[] array=args.toCharArray();
        ArrayList<String> arrayList=new ArrayList<>();
        int begin=0;
        int end=-1;
        for(int i=0;i<array.length;i++)
        {
            if(array[i]==splitchar)
            {
                end=i;
                arrayList.add(args.substring(begin,end));
                begin=end+1;
            }
        }
        arrayList.add(args.substring(end+1,array.length));
        String[] res=new String[arrayList.size()];
        arrayList.toArray(res);
        return res;
    }
}
