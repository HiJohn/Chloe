package joe.chloe.util;

import android.os.AsyncTask;

import java.util.concurrent.Executor;

/**
 * Created by takashi on 2017/4/21.
 */

public class TaskExecutor {


    private TaskExecutor(){}

    public static void execute(Runnable r, boolean parallel){
        Executor executor = parallel? AsyncTask.THREAD_POOL_EXECUTOR: AsyncTask.SERIAL_EXECUTOR;
        executor.execute(r);
    }

    public static void executeSerial(Runnable r){
        AsyncTask.SERIAL_EXECUTOR.execute(r);
    }

    public static void executeParallel(Runnable r){
        AsyncTask.THREAD_POOL_EXECUTOR.execute(r);
    }




    public static <T> void execute(AsyncTask<T,?,?> asyncTask, boolean parallel, T... params){
        if (parallel){
            executeParallel(asyncTask,params);
        }else {
            executeSerial(asyncTask,params);
        }
    }

    public static <T> void  executeParallel(AsyncTask<T,?,?> asyncTask, T... params){
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);
    }

    public static <T> void executeSerial(AsyncTask<T,?,?> asyncTask, T... params){
        asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,params);
    }

}
