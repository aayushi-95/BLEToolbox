/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.event;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;

public class BLEStateEvent {

    /**
     * This event will be fired in global-level to inform if Bluetooth state has been changed.
     */
    public static class BluetoothStateChanged extends BaseEvent {

        public final int newState;

        public BluetoothStateChanged(int newState) {
            super();
            this.newState = newState;
        }
    }

    /**
     * This event will be fired whenever a BLE device is connected.
     */
    public static class Connected extends BaseEvent {
        public int bondState;
    }

    /**
     * This event will be fired whenever phone is trying to connect to a BLE device.
     */
    public static class Connecting extends BaseEvent {
    }

    /**
     * This event will be fired whenever a current connection to BLE device has been lost.
     */
    public static class Disconnected extends BaseEvent {
    }

    /**
     * This event will be fired whenever all services of a BLE device have been discovered.
     */
    public static class ServiceDiscovered extends BaseEvent {
        public int bondState;
    }

    /**
     * This event will be fired whenever a piece of data is available through a {@link BluetoothGattCharacteristic}.
     */
    public static class DataAvailable extends BaseEvent {

        public final BluetoothGattCharacteristic characteristic;

        public DataAvailable(@NonNull BluetoothGattCharacteristic characteristic) {
            super();
            this.characteristic = characteristic;
        }
    }


    /**
     * This event will be fired whenever a piece of data is available through a {@link BluetoothGattCharacteristic}.
            */
    public static class DataAvailableFRMD extends BaseEvent {

        public final BluetoothGatt characteristic;

        public DataAvailableFRMD(@NonNull BluetoothGatt characteristic) {
            super();
            this.characteristic = characteristic;
        }
    }


    public static class DataWritenFromClient extends BaseEvent {

        public final BluetoothDevice device;
        public final int requestId;
        public final BluetoothGattCharacteristic characteristic;
        public final boolean preparedWrite;
        public final boolean responseNeeded;
        public final int offset;
        public final byte[] value;

        public DataWritenFromClient(BluetoothDevice device, int requestId,
                                    BluetoothGattCharacteristic characteristic, boolean preparedWrite,
                                    boolean responseNeeded, int offset, byte[] value) {
            this.device = device;
            this.requestId = requestId;
            this.characteristic = characteristic;
            this.preparedWrite = preparedWrite;
            this.responseNeeded = responseNeeded;
            this.offset = offset;
            this.value = value;
        }
    }

    /**
     * Global-level event, fired whenever bonding state of a bluetooth device is changed.
     */
    public static class DeviceBondStateChanged extends BaseEvent {

        public final BluetoothDevice device;
        public final int bondState;

        public DeviceBondStateChanged(BluetoothDevice device, int bondState) {
            super();
            this.device = device;
            this.bondState = bondState;
        }
    }

    public static class DeviceRssiUpdated extends BaseEvent {

        public final String device;
        public final int rssi;

        public DeviceRssiUpdated(int rssi, String device) {
            super();
            this.rssi = rssi;
            this.device = device;
        }
    }

    public static class MTUUpdated extends BaseEvent {

        public final String device;
        public final int mtuSize;
        public final boolean success;

        public MTUUpdated(String device, int mtuSize, boolean success) {
            super();
            this.device = device;
            this.mtuSize = mtuSize;
            this.success = success;
        }
    }
}
