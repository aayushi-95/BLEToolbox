/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freescale.bletoolbox.AppConfig;
import com.freescale.bletoolbox.BuildConfig;
import com.freescale.bletoolbox.R;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutDialog extends MaterialDialog {

    @Bind(R.id.about_info)
    TextView mAppInfo;

    @OnClick(R.id.about_fsl_link)
    public void viewFslLink() {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.FSL_HOME_PAGE));
            getContext().startActivity(browserIntent);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public AboutDialog(Builder builder) {
        super(builder);
    }

    public static AboutDialog newInstance(Context context) {
        Builder builder = new Builder(context).customView(R.layout.about, true);
        AboutDialog dialog = new AboutDialog(builder);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mAppInfo.setText(String.format(Locale.getDefault(),
                "Release Date\n%s\n\nVersion\n%s", BuildConfig.BUILD_DATE, BuildConfig.VERSION_NAME));
//        mAppInfo.setText(String.format(Locale.getDefault(),
//                "Build Version:\n%s", BuildConfig.VERSION_NAME));
    }
}