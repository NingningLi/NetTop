package com.novas.nettop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String s="\r\n";
        for(int i=0;i<1000;i++)
        {
            s=s+"demo:"+i;
        }
        s=s+"\r\n";
        try {
            byte[] bytes=s.getBytes("gbk");
            System.out.println("length="+
                    s.length()+" "+new String(bytes,"gbk").length());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        final TextView textView=(TextView)this.findViewById(R.id.text);
        firstRequest arequest=new firstRequest();
        BaseNetTopBusiness baseNetTopBusiness=new BaseNetTopBusiness(new NetTopListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                System.out.println("成功");
                byte[] bytes=response.bytes;

                try
                {  System.out.println(bytes.length);

                    System.out.println(new String(bytes,"gbk"));
                    textView.setText(new String(bytes, "gbk").substring(3680)+"");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail() {
                System.out.println("on fail");
                textView.setText("fail");
            }

            @Override
            public void onError() {
                System.out.println("on error");
                textView.setText("error");
            }
        });
        baseNetTopBusiness.startRequest(arequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
