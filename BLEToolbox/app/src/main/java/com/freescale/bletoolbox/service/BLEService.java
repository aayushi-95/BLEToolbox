/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.freescale.bletoolbox.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.freescale.bletoolbox.AppConfig;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.model.Task;
import com.freescale.bletoolbox.utility.BLEConverter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;

public enum BLEService {

    /**
     * Please refer to http://stackoverflow.com/questions/70689.
     */
    INSTANCE;

    /**
     * States of BLE connection.
     */
    public interface State {

        int STATE_DISCONNECTED = 0;
        int STATE_CONNECTING = 1;
        int STATE_CONNECTED = 2;
    }

    /**
     * Request types which can be made to BLE devices.
     */
    public interface Request {

        int READ = 0;
        int WRITE = 1;
        int NOTIFY = 2;
        int INDICATE = 3;
        int WRITE_NO_RESPONSE = 4;
        int WRITE_WITH_AUTHEN = 5;
        int DISABLE_NOTIFY_INDICATE = 6;
    }

    private BluetoothManager mBluetoothManager; // centralized BluetoothManager
    private BluetoothAdapter mBluetoothAdapter; // corresponding Adapter
    private BluetoothGatt mBluetoothGatt; // GATT connection
    private BluetoothGattServer mBluetoothGattServer; // for UART, we need a server callback

    /**
     * Always use main thread to perform BLE operation to avoid issue with many Samsung devices.
     */
    private final Handler mMainLoop = new Handler(Looper.getMainLooper());

    /**
     * All awaiting operations need a timeout.
     */
    private final Runnable mTimeOutRunnable = new Runnable() {

        @Override
        public void run() {
            continueTaskExecution(false);
        }
    };

    /**
     * Also, service discovery need a timeout too.
     */
    private final Runnable mDiscoveryTimeOut = new Runnable() {

        @Override
        public void run() {
            disconnect();
        }
    };

    /**
     * We hold reference to application-level context.
     */
    private Context mContext;

    /**
     * All registered tasks are stored and release when disconnect.
     */
    private List<Task> mTaskList;

    /**
     * Internal variable to track connection state.
     */
    private int mConnectionState;

    /**
     * Sometimes, connect to a BLE device need a few tries :(
     */
    private int tryTime;

    /**
     * Internal MAC address of BLE device.
     */
    private String address;

    /**
     * We need to create a Gatt Server with UART support (service and characteristic).
     */
    private boolean needUartSupport;

    /**
     * This callback should be used whenever a GATT event is fired.
     * It will, in turn, broadcast EventBus events to other application components.
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @DebugLog
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e("device state",gatt.toString()+newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = State.STATE_CONNECTED;
                BLEStateEvent.Connected connectedEvent = new BLEStateEvent.Connected();
                connectedEvent.bondState = gatt.getDevice().getBondState();
                EventBus.getDefault().post(new BLEStateEvent.Connected());
                // after connected, ask executors to dicover BLE services
                registerTask(new Task() {

                    @Override
                    public void run() {
                        mMainLoop.removeCallbacks(mDiscoveryTimeOut);
                        if (mBluetoothGatt != null) {
                            mBluetoothGatt.discoverServices();
                            mMainLoop.postDelayed(mDiscoveryTimeOut, AppConfig.DEFAULT_REQUEST_TIMEOUT);
                        }
                    }
                });
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (status == 133 && !TextUtils.isEmpty(address) && tryTime < 10) {
                    mMainLoop.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            connect(address, needUartSupport);
                        }
                    }, 1000);
                } else {
//                    bluetoothReleaseResource();
                    disconnect();
                }
            }
        }

        @DebugLog
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("dicovery all service","SUCCESS");

                mMainLoop.removeCallbacks(mDiscoveryTimeOut);
                BLEStateEvent.ServiceDiscovered serviceDiscovered = new BLEStateEvent.ServiceDiscovered();
                serviceDiscovered.bondState = gatt.getDevice().getBondState();
                EventBus.getDefault().post(serviceDiscovered);
                // post to get all FRDM's services
                //EventBus.getDefault().post(gatt.getServices());
                EventBus.getDefault().post(new BLEStateEvent.DataAvailableFRMD(gatt));
                continueTaskExecution(false);
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION || status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
                changeDevicePairing(gatt.getDevice(), true);
                Log.e("dicovery all service","discovery failed and remove device");

            } else {
                Log.e("dicovery all service","discovery failed and remove device disconnect");

                disconnect();
            }
        }

        @DebugLog
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                EventBus.getDefault().post(new BLEStateEvent.DataAvailable(characteristic));
                continueTaskExecution(false);
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION || status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
                changeDevicePairing(gatt.getDevice(), true);
            } else {
                disconnect();
            }
        }

        @DebugLog
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            EventBus.getDefault().post(new BLEStateEvent.DataAvailable(characteristic));
        }

        @DebugLog
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                EventBus.getDefault().post(new BLEStateEvent.DataAvailable(characteristic));
                continueTaskExecution(false);
            }
        }

        @DebugLog
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                continueTaskExecution(false);
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION || status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
                changeDevicePairing(gatt.getDevice(), true);
            } else {
                disconnect();
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                EventBus.getDefault().post(new BLEStateEvent.DeviceRssiUpdated(rssi, address));
            }
        }

        @Override
        @DebugLog
        public void onMtuChanged(BluetoothGatt gatt, int mtuSize, int status) {
            super.onMtuChanged(gatt, mtuSize, status);
            Log.e("mtu", "onMtuChanged : \nmtuSize : " + mtuSize + "\nstatus : " + status);
            EventBus.getDefault().post(new BLEStateEvent.MTUUpdated(address, mtuSize, (status == BluetoothGatt.GATT_SUCCESS)));
        }
    };





    /**
     * This callback is used for Wireless UART custom profile.
     */
    private final BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @DebugLog
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        }

        @DebugLog
        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
        }

        @DebugLog
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            EventBus.getDefault().post(new BLEStateEvent.DataWritenFromClient(
                    device, requestId, characteristic, preparedWrite, responseNeeded, offset, value));
        }
    };

    /**
     * Init singleton instance using application-level context.
     * We ensure that BluetoothManager and Adapter are always available.
     *
     * @param context
     */
    public void init(@NonNull Context context) {
        if (this.mContext != null) {
            return;
        }
        this.mContext = context.getApplicationContext();
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        EventBus.getDefault().register(this);
    }

    /**
     * This instance handles bonding state event to correctly execute last failed task.
     *
     * @param e
     */
    public void onEvent(BLEStateEvent.DeviceBondStateChanged e) {
        Log.e("Bond", e.device.getAddress() + " " + e.bondState);
        if (mBluetoothGatt != null && e.device.equals(mBluetoothGatt.getDevice())) {
            // if device has bonding removed, retry last task so it can request bonding again
            if (e.bondState == BluetoothDevice.BOND_NONE) {
                continueTaskExecution(true);
            } else if (e.bondState == BluetoothDevice.BOND_BONDING) {
                mMainLoop.removeCallbacks(mTimeOutRunnable);
            } else if (e.bondState == BluetoothDevice.BOND_BONDED) {
                // do nothing because request will return result to callback now
                continueTaskExecution(true);
            }
        }
    }

    /**
     * For display purpose only.
     *
     * @return
     */
    public int getConnectionState() {
        return mConnectionState;
    }

    /**
     * Check if Bluetooth of device is enabled.
     *
     * @return
     */
    public boolean isBluetoothAvailable() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void connect(@NonNull final String address, final boolean needUartSupport) {
        this.address = address;
        this.tryTime++;
        mMainLoop.post(new Runnable() {

            @Override
            public void run() {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                Log.e("initialize ","mBluetoothGatt isn't null");

                mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
                Log.e("initialize successfully","mBluetoothGatt isn't null");

                mConnectionState = State.STATE_CONNECTING;
                EventBus.getDefault().post(new BLEStateEvent.Connecting());
                BLEService.this.needUartSupport = needUartSupport;

                if (needUartSupport) {
                    mBluetoothGattServer = mBluetoothManager.openGattServer(mContext, mGattServerCallback);
                    final BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                            BLEConverter.uuidFromAssignedNumber(BLEAttributes.UART_STREAM),
                            BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                            BluetoothGattCharacteristic.PERMISSION_WRITE);
                    final BluetoothGattService service = new BluetoothGattService(
                            BLEConverter.uuidFromAssignedNumber(BLEAttributes.WUART), BluetoothGattService.SERVICE_TYPE_PRIMARY);
                    service.addCharacteristic(characteristic);
                    if(null != mBluetoothGattServer && null != service){
                        mBluetoothGattServer.addService(service);
                    }
                }
            }
        });
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        mMainLoop.post(new Runnable() {

            @Override
            public void run() {
                if (mBluetoothGatt != null) {
                    try {

                        mBluetoothGatt.disconnect();
                        mBluetoothGatt.close();
                    } catch (Throwable ignored) {
                    }
                    mBluetoothGatt = null;
                }
                if (mBluetoothGattServer != null) {
                    try {
                        mBluetoothGattServer.close();
                    } catch (Throwable ignored) {
                    }
                    mBluetoothGattServer = null;
                }
                clearTasks();
                mConnectionState = State.STATE_DISCONNECTED;
                EventBus.getDefault().post(new BLEStateEvent.Disconnected());
                tryTime = 0;
            }
        });
    }
private void bluetoothReleaseResource(){
    try {

        mBluetoothGatt.close();
    } catch (Throwable ignored) {
    }
    mBluetoothGatt = null;
    Log.e("disconnect ","mBluetoothGatt is null");

    if (mBluetoothGattServer != null) {
        try {
            mBluetoothGattServer.close();
        } catch (Throwable ignored) {
        }
        mBluetoothGattServer = null;
    }
}
    /**
     * Check and get if a service with UUID is available within device.
     *
     * @param uuid assigned-number of a service
     * @return available service
     */
    public BluetoothGattService getService(int uuid) {
        return mBluetoothGatt == null ? null : mBluetoothGatt.getService(BLEConverter.uuidFromAssignedNumber(uuid));
    }


    public void readCustomCharacteristic() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {

            return;
        }
        /*check if the service is available on the device*/
        BluetoothGattService mCustomService = mBluetoothGatt.getService(UUID.fromString("02ff5600-ba5e-f4ee-5ca1-eb1e5e4b1ce0"));
        if(mCustomService == null){

            return;
        }
        /*get the read characteristic from the service*/
        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(UUID.fromString("02ff5700-ba5e-f4ee-5ca1-eb1e5e4b1ce0"));
        if(mBluetoothGatt.readCharacteristic(mReadCharacteristic) == false){

        }
    }




    public boolean request(String serviceUUID, int characteristicUUID, final int requestType) {
        if (mBluetoothGatt == null) {
            return false;
        }

        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(
                BLEConverter.uuidFromAssignedNumber(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            if (requestType == Request.READ) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        readCharacteristic(characteristic);
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            if (requestType == Request.INDICATE) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        boolean waitForDescriptorWrite = setCharacteristicIndication(characteristic);
                        if (!waitForDescriptorWrite) {
                            continueTaskExecution(false);
                        }
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (requestType == Request.NOTIFY) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        boolean waitForDescriptorWrite = setCharacteristicNotification(characteristic, true);
                        if (!waitForDescriptorWrite) {
                            continueTaskExecution(false);
                        }
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (requestType == Request.DISABLE_NOTIFY_INDICATE) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        boolean waitForDescriptorWrite = setCharacteristicDisableNotification(characteristic, true);
                        if (!waitForDescriptorWrite) {
                            continueTaskExecution(false);
                        }
                    }
                });
            }
        }
        return true;
    }


    public boolean request(String serviceUUID, String characteristicUUID, final int requestType) {
        if (mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }
       final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            if (requestType == Request.READ) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        readCharacteristic(characteristic);
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            if (requestType == Request.INDICATE) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        boolean waitForDescriptorWrite = setCharacteristicIndication(characteristic);
                        if (!waitForDescriptorWrite) {
                            continueTaskExecution(false);
                        }
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (requestType == Request.NOTIFY) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        boolean waitForDescriptorWrite = setCharacteristicNotification(characteristic, true);
                        if (!waitForDescriptorWrite) {
                            continueTaskExecution(false);
                        }
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (requestType == Request.DISABLE_NOTIFY_INDICATE) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        boolean waitForDescriptorWrite = setCharacteristicDisableNotification(characteristic, true);
                        if (!waitForDescriptorWrite) {
                            continueTaskExecution(false);
                        }
                    }
                });
            }
        }
        return true;
    }


    /**
     * Request READ action using predefined service UUID and characteristic UUID, can allow notification if needed.
     *
     * @param serviceUUID
     * @param characteristicUUID
     * @param requestType        value must be of {@link com.freescale.bletoolbox.service.BLEService.Request}
     * @return true if both service and characteristic are found, false if otherwise.
     */
    public boolean request(int serviceUUID, int characteristicUUID, final int requestType) {
        if (mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = getService(serviceUUID);
        if (service == null) {
            return false;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(
                BLEConverter.uuidFromAssignedNumber(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            if (requestType == Request.READ) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        readCharacteristic(characteristic);
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            if (requestType == Request.INDICATE) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        boolean waitForDescriptorWrite = setCharacteristicIndication(characteristic);
                        if (!waitForDescriptorWrite) {
                            continueTaskExecution(false);
                        }
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (requestType == Request.NOTIFY) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        boolean waitForDescriptorWrite = setCharacteristicNotification(characteristic, true);
                        if (!waitForDescriptorWrite) {
                            continueTaskExecution(false);
                        }
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (requestType == Request.DISABLE_NOTIFY_INDICATE) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        boolean waitForDescriptorWrite = setCharacteristicDisableNotification(characteristic, true);
                        if (!waitForDescriptorWrite) {
                            continueTaskExecution(false);
                        }
                    }
                });
            }
        }
        return true;
    }



    /**
     * Specific request method to be used with Wireless UART.
     *
     * @param serviceUuid
     * @param characteristicUuid
     * @param data
     * @return
     */
    public boolean requestWrite(int serviceUuid, int characteristicUuid, final byte[] data) {
        if (mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = getService(serviceUuid);
        if (service == null) {
            return false;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(
                BLEConverter.uuidFromAssignedNumber(characteristicUuid));
        if (characteristic == null) {
            return false;
        }
        int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            registerTask(new Task() {

                @Override
                public void run() {
                    if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                        return;
                    }
                    characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    characteristic.setValue(data);
                    mBluetoothGatt.writeCharacteristic(characteristic);
                }
            });
        }
        return true;
    }


    public boolean requestWrite(String serviceUuid, String characteristicUuid, final byte[] data) {
        if (mBluetoothGatt == null) {
            Log.e("command send","step 1");

            return false;
        }
        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceUuid));
        //BluetoothGattService service = getService(serviceUuid);
        if (service == null) {
            Log.e("command send","step 2");

            return false;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUuid));
       /* final BluetoothGattCharacteristic characteristic = service.getCharacteristic(
                BLEConverter.uuidFromAssignedNumber(characteristicUuid));*/
        if (characteristic == null) {
            Log.e("command send","step 3");

            return false;
        }
        int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            registerTask(new Task() {

                @Override
                public void run() {
                    if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                        return;
                    }
                    characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    characteristic.setValue(data);
                    mBluetoothGatt.writeCharacteristic(characteristic);
                }
            });
        }
        return true;
    }

    /**
     * Request current RSSI value of connected BLE device.
     *
     * @return
     */
    public boolean requestRemoteRssi() {
        return mBluetoothGatt != null && mBluetoothGatt.readRemoteRssi();
    }

    /**
     * Add a task to single executor. This taks should be timed out after a few second.
     *
     * @param task
     */
    private void registerTask(Task task) {
        if (mTaskList == null) {
            mTaskList = new ArrayList<>();
        }
        mTaskList.add(task);
        Log.e("Task", "Registered with id " + task.id);
        if (mTaskList.size() == 1) {
            continueTaskExecution(true);
        }
    }

    /**
     * Unblock current execution, retry last task if needed or skip to next task.
     *
     * @param retryLastTask
     */
    private void continueTaskExecution(final boolean retryLastTask) {
        mMainLoop.removeCallbacks(mTimeOutRunnable);
        mMainLoop.post(new Runnable() {

            @Override
            public void run() {
                if (mTaskList == null || mTaskList.isEmpty()) {
                    return;
                }
                if (!retryLastTask) {
                    mTaskList.remove(0);
                }
                if (mTaskList.isEmpty()) {
                    return;
                }
                final Task task = mTaskList.get(0);
                if (task != null) {
                    Log.e("Task", "Schedule task " + task.id);
                    task.run();
                }
            }
        });
        mMainLoop.postDelayed(mTimeOutRunnable, AppConfig.DEFAULT_REQUEST_TIMEOUT);
    }

    /**
     * Clear all task variables.
     */
    private void clearTasks() {
        if (mTaskList != null) {
            mTaskList.clear();
            mTaskList = null;
        }
        Task.sInternalId = 0;
        mMainLoop.removeCallbacks(mTimeOutRunnable);
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback. Always need to wait for callback.
     *
     * @param characteristic The characteristic to read from.
     */
    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null || characteristic == null) {
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     * @return false if no need to perform any action, true if we need to wait from callback
     */
    private boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null || characteristic == null) {
            return false;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                BLEConverter.uuidFromAssignedNumber(BLEAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            return true;
        }
        return false;
    }

    /**
     * send Disable Notification to the remove device
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     * @return false if no need to perform any action, true if we need to wait from callback
     */
    private boolean setCharacteristicDisableNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null || characteristic == null) {
            return false;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                BLEConverter.uuidFromAssignedNumber(BLEAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            return true;
        }
        return false;
    }

    /**
     * @param serviceUUID
     * @param characteristicUUID
     * @param data
     */
    public boolean writeDataWithAuthen(int serviceUUID, int characteristicUUID, final byte[] data) {
        //remove write type with authentication ( because this type only work on Sony devices)
        return writeData(serviceUUID, characteristicUUID, Request.WRITE, data);
    }


    public boolean writeData(String serviceUUID, int characteristicUUID, final int writeType, final byte[] data) {
        if (mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(
               BLEConverter.uuidFromAssignedNumber(characteristicUUID));
      //  final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        int charaProp = characteristic.getProperties();
        if (Request.WRITE_WITH_AUTHEN == writeType) {
            if ((charaProp | BluetoothGattCharacteristic.WRITE_TYPE_SIGNED) > 0) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            return;
                        }
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_SIGNED);
                        characteristic.setValue(data);
                        mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                });
            }
        } else if (Request.WRITE_NO_RESPONSE == writeType) {
            if ((charaProp | BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) > 0) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            return;
                        }
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        characteristic.setValue(data);
                        mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                });
            }
        } else if (Request.WRITE == writeType) {
            if ((charaProp | BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT) > 0) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            return;
                        }
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        characteristic.setValue(data);
                        mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                });
            }
        }
        return true;
    }


    public boolean writeData(String serviceUUID, String characteristicUUID, final int writeType, final byte[] data) {
        if (mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        int charaProp = characteristic.getProperties();
        if (Request.WRITE_WITH_AUTHEN == writeType) {
            if ((charaProp | BluetoothGattCharacteristic.WRITE_TYPE_SIGNED) > 0) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            return;
                        }
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_SIGNED);
                        characteristic.setValue(data);
                        mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                });
            }
        } else if (Request.WRITE_NO_RESPONSE == writeType) {
            if ((charaProp | BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) > 0) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            return;
                        }
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        characteristic.setValue(data);
                        mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                });
            }
        } else if (Request.WRITE == writeType) {
            if ((charaProp | BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT) > 0) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            return;
                        }
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        characteristic.setValue(data);
                        mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                });
            }
        }
        return true;
    }

    public boolean requestMTU(int mtuSize) {
        if (Build.VERSION.SDK_INT >= 21) {
            return mBluetoothGatt.requestMtu(mtuSize);
        } else {
            return false;
        }
    }


    /**
     * @param serviceUUID
     * @param characteristicUUID
     * @param writeType
     * @param data
     */
    public boolean writeData(int serviceUUID, int characteristicUUID, final int writeType, final byte[] data) {
        if (mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = getService(serviceUUID);
        if (service == null) {
            return false;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(
                BLEConverter.uuidFromAssignedNumber(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        int charaProp = characteristic.getProperties();
        if (Request.WRITE_WITH_AUTHEN == writeType) {
            if ((charaProp | BluetoothGattCharacteristic.WRITE_TYPE_SIGNED) > 0) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            return;
                        }
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_SIGNED);
                        characteristic.setValue(data);
                        mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                });
            }
        } else if (Request.WRITE_NO_RESPONSE == writeType) {
            if ((charaProp | BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) > 0) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            return;
                        }
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        characteristic.setValue(data);
                        mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                });
            }
        } else if (Request.WRITE == writeType) {
            if ((charaProp | BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT) > 0) {
                registerTask(new Task() {

                    @Override
                    public void run() {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            return;
                        }
                        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        characteristic.setValue(data);
                        mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                });
            }
        }
        return true;
    }



    public boolean writeCharacteristic(String serviceUUID, String characteristicUUID, final int requestType, final int value, final int format, final int offset) {
        if (mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service == null) {
            return false;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            if (requestType == Request.WRITE_NO_RESPONSE) {
                registerTask(new Task() {
                    @Override
                    public void run() {
                        boolean wrote = setCharacteristic(characteristic, value, format, offset);
                        continueTaskExecution(false);
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            if (requestType == Request.WRITE) {
                registerTask(new Task() {
                    @Override
                    public void run() {
                        boolean wrote = setCharacteristic(characteristic, value, format, offset);
                    }
                });
            }
        }
        return true;
    }



    public boolean writeCharacteristic(int serviceUUID, int characteristicUUID, final int requestType, final int value, final int format, final int offset) {
        if (mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = getService(serviceUUID);
        if (service == null) {
            return false;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(
                BLEConverter.uuidFromAssignedNumber(characteristicUUID));
        if (characteristic == null) {
            return false;
        }
        int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            if (requestType == Request.WRITE_NO_RESPONSE) {
                registerTask(new Task() {
                    @Override
                    public void run() {
                        boolean wrote = setCharacteristic(characteristic, value, format, offset);
                        continueTaskExecution(false);
                    }
                });
            }
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            if (requestType == Request.WRITE) {
                registerTask(new Task() {
                    @Override
                    public void run() {
                        boolean wrote = setCharacteristic(characteristic, value, format, offset);
                    }
                });
            }
        }
        return true;
    }


    /**
     * @param characteristic
     * @param value
     * @param format
     * @param offset
     * @return false if no need to perform any action, true if we need to wait from callback
     */
    @DebugLog
    private boolean setCharacteristic(BluetoothGattCharacteristic characteristic, int value, int format, int offset) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null || characteristic == null) {
            return false;
        }
        characteristic.setValue(value, format, offset);
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables indication on a give characteris
     *tic.
     *
     * @param characteristic Characteristic to act on.
     * @return false if no need to perform any action, true if we need to wait from callback
     */
    private boolean setCharacteristicIndication(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null || characteristic == null) {
            return false;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                BLEConverter.uuidFromAssignedNumber(BLEAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            return true;
        }
        return false;
    }

    private boolean changeDevicePairing(@NonNull BluetoothDevice device, boolean remove) {
        try {
            Method m = device.getClass()
                    .getMethod(remove ? "removeBond" : "createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
