package joe.chloe.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class WeakRefHandler<T extends Activity> extends Handler {

    private WeakReference<T> weakReferenceActivity = null;

    public WeakRefHandler(T t) {
        this.weakReferenceActivity = new WeakReference<T>(t);
    }

    public abstract void handleMessage(Message msg, T weakReferenceActivity);

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        handleMessage(msg, weakReferenceActivity.get());
    }



}
