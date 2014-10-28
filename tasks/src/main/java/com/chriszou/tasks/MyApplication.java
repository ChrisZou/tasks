package com.chriszou.tasks;

import android.content.Context;
import android.content.Intent;

import com.chriszou.androidlibs.BootReceiver;
import com.chriszou.androidlibs.UtilApplication;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.UiThread;

/**
 * Created by zouyong on 10/28/14.
 */
@EApplication
public class MyApplication extends UtilApplication implements BootReceiver.OnBootCompletedListener{


    @Override
    public void onCreate() {
        super.onCreate();
        BootReceiver.setOnBootCompletedListener(this);
    }

    @Override
    public void onBootCompleted(Context context, Intent intent) {
        showNotification(null);
    }

    @Background
    void showNotification(Context context) {
        try {
            Task t = Task.all().get(0);
            showNotification(context, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void showNotification(Context context, Task t) {
        Utils.showTaskNotification(context, t.title);
    }
}
