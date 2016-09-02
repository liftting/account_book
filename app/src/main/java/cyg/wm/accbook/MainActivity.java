package cyg.wm.accbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cyg.cygnet.base.CtExecManager;
import com.cyg.cygnet.base.CtResult;
import com.cyg.cygnet.base.ICallBack;
import com.cyg.cygnet.base.IParser;
import com.cyg.cygnet.base.parser.CtStringParser;

import cyg.wm.accbook.Hello.Hello;
import cyg.wm.accplugin.PluginConfig;
import cyg.wm.accplugin.PluginManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textView = (TextView) findViewById(R.id.textview);
        textView.setText(new Hello().say());
    }

    public void hello(View view) {
        PluginManager.loadLastVersionPlugin(PluginConfig.PLUGIN_HELLO);
        try {
            Class cls = PluginManager.mNowClassLoader.loadClass(PluginManager.getPlugin(PluginConfig.PLUGIN_HELLO).getPluginMeta().mainClass);
            Intent intent = new Intent(this, cls);
            startActivity(intent);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void net(View view) {
        CtExecManager.get("http://172.16.14.118:8080/ctwebser/learn/recent.php?uid=1", new CtStringParser(), new ICallBack<String>() {
            @Override
            public void callback(CtResult<String> result) {
                Toast.makeText(MainActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
            }
        }, null);
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
