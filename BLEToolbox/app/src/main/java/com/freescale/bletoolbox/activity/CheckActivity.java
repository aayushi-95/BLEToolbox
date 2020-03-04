/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.utility.Constants;
import com.freescale.bletoolbox.utility.SharedPreferencesUtil;

public class CheckActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new SharedPreferencesUtil(this).saveBoolean(Constants.STARTAPP,true);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            new MaterialDialog.Builder(this)
                    .title(R.string.error_title)
                    .titleColor(Color.RED)
                    .content(R.string.error_ble_required)
                    .negativeText(android.R.string.cancel)
                    .callback(new MaterialDialog.ButtonCallback() {

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            finish();
                        }
                    }).show();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
