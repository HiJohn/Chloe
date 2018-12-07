package joe.chloe;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

public class ChloeApp extends MultiDexApplication {

    private static ChloeApp mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static ChloeApp getApp() {
        return mApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
}
