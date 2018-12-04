package joe.chloe;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

public class ChloeApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
}
