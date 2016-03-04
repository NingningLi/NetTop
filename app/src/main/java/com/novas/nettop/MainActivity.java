package com.novas.nettop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView=(TextView)this.findViewById(R.id.text);
        firstRequest arequest=new firstRequest();
        BaseNetTopBusiness baseNetTopBusiness=new BaseNetTopBusiness(new NetTopListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                System.out.println("成功");
                byte[] bytes=response.bytes;
                System.out.println(new String(bytes));
                textView.setText(new String(bytes));
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
