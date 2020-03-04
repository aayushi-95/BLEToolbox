/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class BLEApp {

    public interface Type {

        int BEACON = 1;
        int BLOOD_PRESSURE = 2;
        int CYCLING_SPEED = 3;
        int GLUCOSE = 4;
        int THERMOMETER = 5;
        int HEART_RATE = 6;
        int PROXIMITY = 7;
        int RUNNING_SPEED = 8;
        int WUART = 9;
        int OTAP = 10;
        int FRDM_DEMO = 11;
        int SHELL = 12;
        int QPP = 13;
        int SENSOR = 14;
        int ZIGBEE = 15;
        int IPconfig = 16;
    }

    public final int type;

    @DrawableRes
    public final int icon;

    @StringRes
    public final int title;

    /**
     * This value is used during development and release process.
     */
    public final boolean available;

    public BLEApp(int type, int icon, int title) {
        this(type, icon, title, false);
    }

    public BLEApp(int type, int icon, int title, boolean available) {
        this.type = type;
        this.icon = icon;
        this.title = title;
        this.available = available;
    }
}
