/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.model;

import android.bluetooth.BluetoothDevice;

public class BLEDevice {

    private BluetoothDevice internalDevice;
    private int rssi;
    private long lastScannedTime;

    public BLEDevice(BluetoothDevice internalDevice) {
        if (internalDevice == null) {
            throw new NullPointerException();
        }
        this.internalDevice = internalDevice;
    }

    public BluetoothDevice getInternalDevice() {
        return internalDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getLastScannedTime() {
        return lastScannedTime;
    }

    public void setLastScannedTime(long lastScannedTime) {
        this.lastScannedTime = lastScannedTime;
    }

    @Override
    public int hashCode() {
        return internalDevice.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof BLEDevice)) {
            return false;
        }
        if (((BLEDevice) o).internalDevice == null) {
            return false;
        }
        return internalDevice.equals(((BLEDevice) o).internalDevice);
    }
}
