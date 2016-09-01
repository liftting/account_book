package cyg.wm.accbook;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.cyg.cygnet.base.CtExecManager;
import com.cyg.cygnet.service.OkhttpStack;

import cyg.wm.accplugin.ZeusBaseApplication;


/**
 * Created by wm on 16/8/23.
 */
public class AccountApplication extends ZeusBaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        CtExecManager.init(OkhttpStack.getInstance(this));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
}
