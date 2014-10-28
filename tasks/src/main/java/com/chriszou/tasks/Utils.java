package com.chriszou.tasks;

import android.content.Context;

import com.chriszou.androidlibs.Notifier;

/**
 * Created by zouyong on 10/28/14.
 */
public class Utils {
    /**
     *
     */
    public static void showTaskNotification(Context context, String title) {
        Notifier notifier = new Notifier(context);
        notifier.setOnGoing(true);
        notifier.fireActivity(R.drawable.tasks, "Tasks", title, MainActivity_.class);
    }
}
