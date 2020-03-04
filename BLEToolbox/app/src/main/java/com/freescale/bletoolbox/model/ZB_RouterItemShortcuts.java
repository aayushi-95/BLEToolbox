package com.freescale.bletoolbox.model;

import io.realm.RealmObject;

/**
 * Created by nxf42542 on 2018/7/20.
 */

public class ZB_RouterItemShortcuts extends RealmObject implements Shortcuts {
    private String strCommand;
    private String strValue;
    private String strTime;

    public String getStrCommand() {
        return strCommand;
    }

    public void setStrCommand(String strCommand) {
        this.strCommand = strCommand;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }
}
