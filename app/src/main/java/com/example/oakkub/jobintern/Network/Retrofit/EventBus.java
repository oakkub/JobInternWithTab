package com.example.oakkub.jobintern.Network.Retrofit;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by OaKKuB on 8/4/2015.
 */
public final class EventBus {

    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    public static Bus getBusInstance() {
        return BUS;
    }

}
