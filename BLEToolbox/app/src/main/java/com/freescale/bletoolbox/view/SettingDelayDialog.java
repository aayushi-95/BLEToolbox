package com.freescale.bletoolbox.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freescale.bletoolbox.R;

public class SettingDelayDialog extends MaterialDialog {
    static EditText edtTimeDelay;
    static SettingDelayDialog dialog;

    public SettingDelayDialog(Builder builder) {
        super(builder);
    }

    public static String getTextDelay() {
        return edtTimeDelay.getText().toString();
    }

    public static Dialog getDialog() {
        return dialog;
    }

    public static SettingDelayDialog newInstance(Context context, long _nTimeDelay, View.OnClickListener cancelClick, View.OnClickListener OKClick) {
        Builder builder = new Builder(context).customView(R.layout.dialog_setting_delay, true);
        dialog = new SettingDelayDialog(builder);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnCancel = (Button) dialog.findViewById(R.id.bt_cancel);
        Button btnOK = (Button) dialog.findViewById(R.id.bt_ok);
        edtTimeDelay = (EditText) dialog.findViewById(R.id.edt_delay);
        edtTimeDelay.setText(String.valueOf(_nTimeDelay));
        btnCancel.setOnClickListener(cancelClick);
        btnOK.setOnClickListener(OKClick);
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
