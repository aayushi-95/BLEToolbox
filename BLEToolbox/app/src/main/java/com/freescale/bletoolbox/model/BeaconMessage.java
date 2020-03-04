/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.model;

import io.realm.RealmObject;

public class BeaconMessage extends RealmObject {

    public static final int ACTION_DEFAULT_DO_NOTHING = 0;
    public static final int ACTION_SHOW_MESSAGE = 1;

    private String uuid;
    private int dataA;
    private int dataB;
    private int dataC;

    private int actionType;
    private String message;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDataA() {
        return dataA;
    }

    public void setDataA(int dataA) {
        this.dataA = dataA;
    }

    public int getDataB() {
        return dataB;
    }

    public void setDataB(int dataB) {
        this.dataB = dataB;
    }

    public int getDataC() {
        return dataC;
    }

    public void setDataC(int dataC) {
        this.dataC = dataC;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
