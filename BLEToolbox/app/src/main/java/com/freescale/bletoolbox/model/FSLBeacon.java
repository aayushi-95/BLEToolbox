/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class FSLBeacon implements Parcelable {

    private byte[] record;
    private int rssi;

    private UUID uuid;
    private String name;
    private short manufactureId;
    private int dataA;
    private int dataB;
    private int dataC;
    private BluetoothDevice device;

    private long lastScannedTime;

    /**
     * Parse a specific scan record to form a beacon, which conform to FSL specification.
     *
     * @param device
     * @param rssi
     * @param scanRecord
     * @return
     */
    public static FSLBeacon fromScanRecord(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (scanRecord.length < 31) { // packet must have at least 31 bytes, include 3 bytes of first AD, and 28 bytes of second AD
            return null;
        }
        int fslPackageLength = scanRecord[3] & 0xff;
        if (fslPackageLength != 0x1B) { // 2nd AD package has length of 0x1B (27 bytes)
            return null;
        }
        int fslPackageFlags = scanRecord[4] & 0xff;
        if (fslPackageFlags != 0xff) { // byte after length is 0xff indicate beacon packet
            return null;
        }

        FSLBeacon beacon = new FSLBeacon();
        beacon.rssi = rssi;
        beacon.record = scanRecord;
        beacon.name = device.getName();
        ByteBuffer bb = ByteBuffer.wrap(scanRecord, 5, 2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        beacon.manufactureId = bb.getShort(); // with FSL, this should be 0x01ff

        int fslBeaconIdentifier = scanRecord[7] & 0xff;
        if (fslBeaconIdentifier != 0xbc) { // FSL beacon identifier
            return null;
        }

        bb = ByteBuffer.wrap(scanRecord, 8, 16);
        beacon.uuid = new UUID(bb.getLong(), bb.getLong());
        beacon.device = device;
        beacon.lastScannedTime = System.nanoTime();

        // now the last piece of data
        beacon.dataA = ((scanRecord[24] & 0xff) << 8) | (scanRecord[25] & 0xff);
        beacon.dataB = ((scanRecord[26] & 0xff) << 8) | (scanRecord[27] & 0xff);
        beacon.dataC = ((scanRecord[28] & 0xff) << 8) | (scanRecord[29] & 0xff);

        return beacon;
    }

    public byte[] getRecord() {
        return record;
    }

    public void setRecord(byte[] record) {
        this.record = record;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getManufactureId() {
        return manufactureId;
    }

    public void setManufactureId(short manufactureId) {
        this.manufactureId = manufactureId;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public long getLastScannedTime() {
        return lastScannedTime;
    }

    public void setLastScannedTime(long lastScannedTime) {
        this.lastScannedTime = lastScannedTime;
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

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof FSLBeacon)) {
            return false;
        }
        if (((FSLBeacon) o).device == null) {
            return false;
        }
        return device.equals(((FSLBeacon) o).device);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.record);
        dest.writeInt(this.rssi);
        dest.writeSerializable(this.uuid);
        dest.writeString(this.name);
        dest.writeInt(this.manufactureId);
        dest.writeInt(this.dataA);
        dest.writeInt(this.dataB);
        dest.writeInt(this.dataC);
        dest.writeParcelable(this.device, 0);
        dest.writeLong(this.lastScannedTime);
    }

    public FSLBeacon() {
    }

    protected FSLBeacon(Parcel in) {
        this.record = in.createByteArray();
        this.rssi = in.readInt();
        this.uuid = (UUID) in.readSerializable();
        this.name = in.readString();
        this.manufactureId = (short) in.readInt();
        this.dataA = in.readInt();
        this.dataB = in.readInt();
        this.dataC = in.readInt();
        this.device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.lastScannedTime = in.readLong();
    }

    public static final Parcelable.Creator<FSLBeacon> CREATOR = new Parcelable.Creator<FSLBeacon>() {

        @Override
        public FSLBeacon createFromParcel(Parcel source) {
            return new FSLBeacon(source);
        }

        @Override
        public FSLBeacon[] newArray(int size) {
            return new FSLBeacon[size];
        }
    };
}
