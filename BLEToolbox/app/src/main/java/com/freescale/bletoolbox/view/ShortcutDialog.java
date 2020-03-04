package com.freescale.bletoolbox.view;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freescale.bletoolbox.R;

public class ShortcutDialog extends MaterialDialog {
    private static ShortcutDialog dialog;
    private static EditText edtEditShortcuts;


    protected ShortcutDialog(Builder builder) {
        super(builder);
    }

    public static Dialog getCommontDialog() {
        return dialog;
    }

    public static String getTextShortcuts(){
        return edtEditShortcuts.getText().toString();
    }

    public static ShortcutDialog newInstance(Context context,String strCommand,String strTitle ,String type, View.OnClickListener onOKClickListener, View.OnClickListener onCancelClickListener) {
        Builder builder = new Builder(context).customView(R.layout.dialog_shortcuts, true);
        dialog = new ShortcutDialog(builder);
        TextView btnCancel = (TextView) dialog.findViewById(R.id.btnCancel);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btnOk);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        tvTitle.setText(strTitle);
        if(type.equals(context.getString(R.string.str_shell_add))){
            edtEditShortcuts = (EditText) dialog.findViewById(R.id.edtEditShortcuts);
            edtEditShortcuts.setText(strCommand);
            edtEditShortcuts.setSelection(strCommand.length());
            edtEditShortcuts.setVisibility(View.VISIBLE);
        }else{
            TextView tvShortcuts = (TextView) dialog.findViewById(R.id.tvShortcuts);
            tvShortcuts.setText(strCommand);
            tvShortcuts.setLongClickable(false);
            tvShortcuts.setVisibility(View.VISIBLE);
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnOk.setOnClickListener(onOKClickListener);
        btnCancel.setOnClickListener(onCancelClickListener);
        return dialog;
    }
}
