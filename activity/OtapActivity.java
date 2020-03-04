/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.service.OtapController;
import com.freescale.bletoolbox.view.SettingDelayDialog;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

public class OtapActivity extends BaseServiceActivity implements View.OnClickListener, OtapController.SendChunkCallback {
    private static final String TAG = "OTActivity";
    private static final int REQUEST_CODE_OPEN_FILE = 611;
    @Bind(R.id.otapTvFileName)
    TextView m_otapTvFileName;
    @Bind(R.id.otapTvFileSize)
    TextView m_otapTvFileSize;
    @Bind(R.id.otapTvFileVer)
    TextView m_otapTvFileVer;
    @Bind(R.id.otapTvFileStatus)
    TextView m_otapTvFileStatus;
    @Bind(R.id.otapBtOpen)
    Button m_otapBtOpen;

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }

    @Bind(R.id.otapBtUpload)
    Button m_otapBtUpload;
    @Bind(R.id.otapPbUpload)
    ProgressBar m_otapPbUpload;
    @Bind(R.id.tv_progress_number)
    TextView tvProgressNumber;
    @Bind(R.id.tv_setting_delay)
    TextView tvSettingDelay;

    private SendImageTask m_sendImageTask;
    //
    private final long WAITING_NEW_REQUEST_TIME = 3000;
    private Timer m_waitNewRequesTimer;
    private TimerTask m_waitNewRequestTimerTask;
    //

    private long nTimeDelay = 100/2;
    private boolean bTouchAble = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtapController.initController();
        setContentView(R.layout.activity_otap);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.app_otap);
        initUI();
    }

    private void initUI() {
        m_otapTvFileName.setText("");
        m_otapTvFileVer.setText("");
        m_otapTvFileSize.setText("");
        m_otapTvFileStatus.setText(getString(R.string.file_status_not_loaded));
        m_otapBtOpen.setOnClickListener(this);
        m_otapBtOpen.setEnabled(false);
        m_otapBtUpload.setText(R.string.upload);
        m_otapBtUpload.setOnClickListener(this);
        m_otapBtUpload.setEnabled(false);
        m_otapPbUpload.setProgress(0);

        tvSettingDelay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    final int DRAWABLE_RIGHT = 2;
                    if (event.getRawX() >= (tvSettingDelay.getRight() - tvSettingDelay.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())
                            && bTouchAble) {
                        displayDialogDelaySetting();
                        return true;
                    }
                }
                return false;
            }
        });

        bTouchAble = false;
    }

    /**
     * display dialog to enter delay value
     */
    private void displayDialogDelaySetting() {
        View.OnClickListener okClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strDelay = SettingDelayDialog.getTextDelay().trim();
                if (TextUtils.isEmpty(strDelay)) {
                    Toast.makeText(OtapActivity.this, "Please enter delay value.", Toast.LENGTH_SHORT).show();
                }
                try {
                    long value = Long.parseLong(strDelay);
                    if (value > 0) {
                        nTimeDelay = value;
                        Dialog dialog = SettingDelayDialog.getDialog();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(OtapActivity.this, "Please enter valid delay value.", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    Toast.makeText(OtapActivity.this, "Please enter valid delay value.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        View.OnClickListener cancelClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = SettingDelayDialog.getDialog();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };

        SettingDelayDialog.newInstance(this, nTimeDelay, cancelClick, okClick).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.otapBtOpen:
                openFile();
                break;
            case R.id.otapBtUpload:
                if (getString(R.string.upload).equals(m_otapBtUpload.getText().toString())) {
                    m_otapBtUpload.setText(R.string.cancel);
                    m_otapBtOpen.setEnabled(false);
                    bTouchAble = false;
                    OtapController.getInstance().sendNewImageInfoResponse();
//                    OtapController.getInstance().sendNewImageNotification();
                } else {
                    /*OtapController.getInstance().interrupSending = true;
                    initUI();
                    m_otapBtOpen.setEnabled(true);*/
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_OPEN_FILE == requestCode && RESULT_OK == resultCode) {
            Uri fileUri = data.getData();
            File file = new File(fileUri.getPath());
//            Log.d(TAG, "file size = " + file.length() + "byte");
            try {
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(fileUri, "r");
                Log.d(TAG, "input size " + parcelFileDescriptor.getStatSize());
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
            if(file.getName().endsWith(".bleota")){
                ValidatingBinFileTask validatingBinFileTask = new ValidatingBinFileTask(fileUri);
                validatingBinFileTask.execute();
            }
            else{
                ValidatingFileTask validatingFileTask = new ValidatingFileTask(fileUri);
                validatingFileTask.execute();
            }*/

            ValidatingFileTask validatingFileTask = new ValidatingFileTask(fileUri);
            validatingFileTask.execute();
        }
    }

    @Override
    public void finish() {
        super.finish();
        OtapController.getInstance().interruptSending();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OtapController.getInstance().interruptSending();
    }

    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e) {
        super.onEventMainThread(e);
        if (e == null) return;
        BluetoothGattCharacteristic gattCharacteristic = e.characteristic;
        final String charaterUuid = gattCharacteristic.getUuid().toString();
//        Log.d(TAG, "uuid = " + gattCharacteristic.getUuid().toString());
        if (BLEAttributes.OTAP_CONTROL_POINT_CHARACTERISTIC.equals(charaterUuid)) {
            final byte[] data = gattCharacteristic.getValue();
            if (null != data && 0 < data.length) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                Log.d(TAG, "hang ve " + stringBuilder.toString());
                switch (data[0]) {
                    case OtapController.CMD_ID_NEW_IMAGE_INFO_REQUEST:
//                        Toast.makeText(this, "hang ve" + stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                        OtapController.getInstance().handleNewImageInfoRequest(data);
                        break;
                    case OtapController.CMD_ID_IMAGE_BLOCK_REQUEST:
//                        Toast.makeText(this, "hang ve" + stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                        boolean request = OtapController.getInstance().handleImageBlockRequest(data);
                        // send chunk data
                        if (request) {
                            // cancel waiting
                            if (null != m_waitNewRequestTimerTask) m_waitNewRequestTimerTask.cancel();
                            if (null != m_waitNewRequesTimer) m_waitNewRequesTimer.cancel();
                            // send image
                            new SendImageTask().execute();
                        }
                        break;
                    case OtapController.CMD_ID_IMAGE_TRANSFER_COMPLETE:
                        OtapController.getInstance().handleImageTransferComplete(data);
                        break;
                    case OtapController.CMD_ID_STOP_IMAGE_TRANSFER:
                        OtapController.getInstance().handleStopImageTransfer(data);
                        break;
                    case OtapController.CMD_ID_ERROR:
                        OtapController.getInstance().handleErrorNotification(data);
                        break;
                    case OtapController.CMD_ID_IMAGE_CHUNK:
                        Log.d(TAG, "received chunk data " + stringBuilder.toString());
                        break;
                    case OtapController.CMD_ID_NEW_IMAGE_INFO_RESPONSE:
                        Log.d(TAG, "received NEW_IMAGE_INFO_RESPONSE " + stringBuilder.toString());
                        OtapController.getInstance().sendDummyImageChunk();
                        break;
                    default:
//                        Toast.makeText(this, "hang ve" + stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    @DebugLog
    public void onEvent(BLEStateEvent.MTUUpdated mtuUpdated) {
        Log.d(TAG, "mtuUpdated = " + mtuUpdated.mtuSize + " success " + mtuUpdated.success);
    }

    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        OtapController.getInstance().interruptSending();
        m_otapBtOpen.setEnabled(false);
        bTouchAble = false;
        m_otapBtUpload.setEnabled(false);
    }

    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
        OtapController.getInstance().resetInterrupt();
        BLEService.INSTANCE.request(BLEAttributes.OTAP, BLEAttributes.OTAP_CONTROL,
                BLEService.Request.INDICATE);
    }

    public void onEventMainThread(BLEStateEvent.ServiceDiscovered e) {
        m_otapBtOpen.setEnabled(!OtapController.getInstance().isSendingInProgress());
        bTouchAble = m_otapBtOpen.isEnabled();
        if (null != OtapController.getInstance().getNewImageInfo()) {
            m_otapBtUpload.setEnabled(true);
            m_otapBtUpload.setText(R.string.upload);
        }
    }

    private void openFile() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        if (null != intent.resolveActivity(this.getPackageManager())) {
            startActivityForResult(Intent.createChooser(intent, "Open file"), REQUEST_CODE_OPEN_FILE);
        } else {
            Toast.makeText(this, "not found file manager", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSentTrunk(long startPosition, long endPosition, int frameNoInBlock, int blockSize) {
        OtapController.ImageInfo imageInfo = OtapController.getInstance().getNewImageInfo();
        if (null != imageInfo) {
            double fileSize = imageInfo.getFileSizeInKb();
            double percent = (endPosition * 100) / (fileSize * 1024);
            Log.d(TAG, "percent = " + percent);
            final int nPercent = (int) Math.round(percent);
            m_otapPbUpload.setProgress(nPercent);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvProgressNumber.setText(nPercent + "%");
                }
            });
        }
    }

    @Override
    public void onStopSending(boolean sentDone) {
        // if sent done (received cmdId 06) -> back to Device scan screen
        if (sentDone) {
            AlertDialog.Builder builder = new AlertDialog.Builder(OtapActivity.this);
            builder.setMessage(R.string.megSendBinaryDone);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.btOk, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create().show();
        } else {
            waitingNewRequestAfterError();
        }

    }

    /**
     * waiting a new request after has an error
     */
    private void waitingNewRequestAfterError() {
        if (null != m_waitNewRequestTimerTask) m_waitNewRequestTimerTask.cancel();
        if (null != m_waitNewRequesTimer) m_waitNewRequesTimer.cancel();
        m_waitNewRequesTimer = new Timer("WaitNewRequestTimerTask", false);
        m_waitNewRequestTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (OtapActivity.this.isFinishing()) return;
                if (!OtapController.getInstance().hasAnError()) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OtapActivity.this, "Has an ERROR without a new request", Toast.LENGTH_SHORT).show();
                        toggleState(false);
                    }
                });
            }
        };
        m_waitNewRequesTimer.schedule(m_waitNewRequestTimerTask, WAITING_NEW_REQUEST_TIME);
    }

    private class ValidatingFileTask extends AsyncTask<Object, Object, OtapController.ChecksumStatus> {
        ProgressDialog progressDialog = new ProgressDialog(OtapActivity.this);
        final Uri fileUri;
        Context context;
        OtapController.ImageInfo imageInfo;

        ValidatingFileTask(Uri fileUri) {
            this.fileUri = fileUri;
            this.context = getApplicationContext();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected OtapController.ChecksumStatus doInBackground(Object[] params) {
            try {
                OtapController.ChecksumStatus checksumStatus = OtapController.ChecksumStatus.Valid;
                checksumStatus = OtapController.getInstance().computeChecksumFile(context, fileUri);
                if (OtapController.ChecksumStatus.Valid != checksumStatus) {
                    return checksumStatus;
                }
                /* remove this to adapt keil format srec has no S0 line*/
//                String header = OtapController.getInstance().getHeader(context, fileUri);
                File newImgFile = OtapController.getInstance().readSrecToCreateImg(context, fileUri);
                Uri imgUri = Uri.fromFile(newImgFile);
                imageInfo = OtapController.getInstance().getNewImgImageInfo(context, imgUri);
                //
                OtapController.getInstance().setNewImageInfo(imageInfo);
                return checksumStatus;
            } catch (Exception e) {
                Log.e(TAG, "exception", e);
                return OtapController.ChecksumStatus.UnknowError;
            }
        }

        @Override
        protected void onPostExecute(OtapController.ChecksumStatus status) {
            super.onPostExecute(status);
            progressDialog.dismiss();
            Toast.makeText(OtapActivity.this, status.name(), Toast.LENGTH_SHORT).show();
            if (OtapController.ChecksumStatus.Valid != status) {
                return;
            }
            // send new image response info
            m_otapTvFileName.setText(imageInfo.getImageName());
            m_otapTvFileVer.setText("0x" + imageInfo.getImageVersionHex());
            m_otapTvFileSize.setText(imageInfo.getFileSizeInKb() + " kb");
            m_otapTvFileStatus.setText(getString(R.string.file_valid));
            m_otapBtUpload.setEnabled(true);
            m_otapPbUpload.setProgress(0);
            OtapController.getInstance().setSendChunkCallback(OtapActivity.this);
//            OtapController.getInstance().sendNewImageNotification();
        }
    }
    private class ValidatingBinFileTask extends ValidatingFileTask
    {
        ValidatingBinFileTask(Uri fileUri){
            super(fileUri);
        }

        @Override
        protected OtapController.ChecksumStatus doInBackground(Object[] params) {
            try {
                OtapController.ChecksumStatus checksumStatus = OtapController.ChecksumStatus.Valid;
//                checksumStatus = OtapController.getInstance().computeChecksumFile(context, fileUri);
                if (OtapController.ChecksumStatus.Valid != checksumStatus) {
                    return checksumStatus;
                }
                /* remove this to adapt keil format srec has no S0 line*/
//                String header = OtapController.getInstance().getHeader(context, fileUri);
                File newImgFile = OtapController.getInstance().readSrecToCreateImg(context, fileUri);
                Uri imgUri = Uri.fromFile(newImgFile);
                imageInfo = OtapController.getInstance().getNewImgImageInfo(context, fileUri);
                //
                OtapController.getInstance().setNewImageInfo(imageInfo);
                return checksumStatus;
            } catch (Exception e) {
                Log.e(TAG, "exception", e);
                return OtapController.ChecksumStatus.UnknowError;
            }
        }

    }
    private class SendImageTask extends AsyncTask<Objects, Objects, Objects> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Objects doInBackground(Objects... params) {
            if (isFinishing()) {
                return null;
            }
            try {
                OtapController.getInstance().sendImgImageChunk(OtapActivity.this, nTimeDelay);
            } catch (IOException ex) {
                Log.e(TAG, "ERROR ", ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Objects objects) {
            super.onPostExecute(objects);
        }
    }
}
