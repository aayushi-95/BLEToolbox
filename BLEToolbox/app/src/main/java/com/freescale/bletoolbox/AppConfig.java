/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox;

import java.util.LinkedHashMap;

public interface AppConfig {

    String FSL_HOME_PAGE = "http://www.freescale.com/products/wireless-connectivity:WIRELESS-CONNECTIVITY";

    /**
     * Given wheel curcumferences to be used in CSC application. Size is evaluated as mm.
     */
    LinkedHashMap<String, Integer> WHEEL_CIRCUMFERENCES = new LinkedHashMap<String, Integer>() {{
        put("32-630", 2199);
        put("28-630", 2174);
        put("40-622", 2224);
        put("47-622", 2268);
        put("40-635", 2265);
        put("37-622", 2205);
        put("18-622", 2102);
        put("20-622", 2114);
        put("23-622", 2133);
        put("25-622", 2146);
        put("28-622", 2149);
        put("32-622", 2174);
        put("37-622", 2205);
        put("40-622", 2224);
        put("47-305", 1272);
        put("47-406", 1590);
        put("37-540", 1948);
        put("47-507", 1907);
        put("23-571", 1973);
        put("40-559", 2026);
        put("44-559", 2051);
        put("47-559", 2070);
        put("50-559", 2089);
        put("54-559", 2114);
        put("57-559", 2133);
        put("37-590", 2105);
        put("37-584", 2086);
        put("20-571", 1954);
    }};

    String DEFAULT_WHEEL_CIRCUMFERENCE = "32-630";

    /**
     * Time interval before a request is considered timed out.
     */
    int DEFAULT_REQUEST_TIMEOUT = 10 * 1000;

    /**
     * Time interval between heart rate update scheduler (ms).
     */
    int HEART_RATE_UPDATE_INTERVAL = 1 * 1000;

    /**
     * Minimum value for heart beat rate (8bit).
     */
    int HEART_RATE_MIN_VALUE = 0;

    /**
     * Maximum value for heart beat rate (8bit).
     */
    int HEART_RATE_MAX_VALUE = 200;

    /**
     * Time range to store and display heart rate.
     */
    int HEART_RATE_TIME_RANGE = 20;

    /**
     * Time interval between battery updates (ms).
     */
    int BATTERY_UPDATE_INTERVAL = 50 * 1000;

    /**
     * Scanning period of BLE devices (ms)
     */
    int DEVICE_SCAN_PERIOD = 30 * 1000;

    /**
     * Some devices filter BLE packets by default. We need to continuously turn on/off scanning to keep RSSI updated.
     */
    int DEVICE_CONTINUOUSLY_SCAN = 2 * 1000;

    /**
     * If a beacon can not be seen in this period, we consider that beacon is out of range.
     */
    int BEACON_OUT_OF_RANGE_PERIOD = 10 * 1000;

    /**
     * Layout for all supported beacon types inside app.
     */
    String[] SUPPORTED_BEACON_LAYOUTS = {"m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"};

    /**
     * Bluetooth Assigned Number for FSL.
     */
    int FSL_MANUFACTURER_ID = 0x01ff;

    /**
     * Time interval between PROXIMITY TX POWER update scheduler (ms).
     */
    int PROXIMITY_TX_POWER_UPDATE_INTERVAL = 3 * 1000;
    int PROXIMITY_RSSI_UPDATE_INTERVAL = 1 * 1000;

    /**
     * For Wireless UART message, we need to define a maximum value of buffered string to avoid overflow.
     */
    int MAX_WUART_BUFFER = 1000;

    public static final int qppServerBufferSize = 20;
}
