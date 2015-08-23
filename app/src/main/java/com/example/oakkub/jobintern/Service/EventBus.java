package com.example.oakkub.jobintern.Service;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by OaKKuB on 8/8/2015.
 */
public class EventBus extends Bus {

    private static EventBus instance;
    private Handler handler = new Handler(Looper.getMainLooper());

    public static EventBus getInstance() {

        if(instance == null) instance = new EventBus();

        return instance;
    }

    public void postQueue(final Object object) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                EventBus.getInstance().post(object);
            }
        });

    }

}
