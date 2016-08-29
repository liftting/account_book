package cyg.wm.hello;

import android.app.Activity;
import android.os.Bundle;

import cyg.wm.accplugin.ZeusBaseAppCompactActivity;

public class MainActivity extends ZeusBaseAppCompactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
