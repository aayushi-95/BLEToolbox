/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.utility;

public class CRC_16CITT {
    public static short OTA_CrcCompute(byte[] data, short crcValueOld) {
        for (int index = 0; index < data.length; index++) {
            crcValueOld ^= data[index] << 8;
            for (byte i = 0; i < 8; ++i) {
                if ((crcValueOld & 0x8000) != 0) {
                    crcValueOld = (short) ((crcValueOld << 1) ^ 0x1021);
                } else {
                    crcValueOld = (short) (crcValueOld << 1);
                }
            }
        }
        return crcValueOld;
    }

    public static int crc16(final byte[] buffer, int crcOldValue) {
        for (int j = 0; j < buffer.length; j++) {
            crcOldValue = ((crcOldValue >>> 8) | (crcOldValue << 8)) & 0xFFFF;
            crcOldValue ^= (buffer[j] & 0xFF);//byte to int, trunc sign
            crcOldValue ^= ((crcOldValue & 0xFF) >> 4);
            crcOldValue ^= (crcOldValue << 12) & 0xFFFF;
            crcOldValue ^= ((crcOldValue & 0xFF) << 5) & 0xFFFF;
        }
        crcOldValue &= 0xFFFF;
        return crcOldValue;
    }

    public static short OTA_CrcCompute_Java(byte[] pData, short lenData, short crcValueOld) {
        int i;
        int index = 0;
        while (lenData-- != 0) {
            crcValueOld ^= pData[index] << 8;
            index++;
            for (i = 0; i < 8; ++i) {
                if ((crcValueOld & 0x8000) != 0) {
                    crcValueOld = (short) ((crcValueOld << 1) ^ 0x1021);
                } else {
                    crcValueOld = (short) (crcValueOld << 1);
                }
            }
        }
        return crcValueOld;
    }
}
