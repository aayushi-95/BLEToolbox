// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class QppActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.QppActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493050, "field 'textDeviceName'");
    target.textDeviceName = finder.castView(view, 2131493050, "field 'textDeviceName'");
    view = finder.findRequiredView(source, 2131493051, "field 'textDeviceAddress'");
    target.textDeviceAddress = finder.castView(view, 2131493051, "field 'textDeviceAddress'");
    view = finder.findRequiredView(source, 2131493052, "field 'textConnectionStatus'");
    target.textConnectionStatus = finder.castView(view, 2131493052, "field 'textConnectionStatus'");
    view = finder.findRequiredView(source, 2131493053, "field 'textQppNotify'");
    target.textQppNotify = finder.castView(view, 2131493053, "field 'textQppNotify'");
    view = finder.findRequiredView(source, 2131493054, "field 'textQppDataRate'");
    target.textQppDataRate = finder.castView(view, 2131493054, "field 'textQppDataRate'");
    view = finder.findRequiredView(source, 2131493055, "field 'editSend'");
    target.editSend = finder.castView(view, 2131493055, "field 'editSend'");
    view = finder.findRequiredView(source, 2131493059, "field 'btnQppTextSend'");
    target.btnQppTextSend = finder.castView(view, 2131493059, "field 'btnQppTextSend'");
    view = finder.findRequiredView(source, 2131493058, "field 'textRepeatCounter'");
    target.textRepeatCounter = finder.castView(view, 2131493058, "field 'textRepeatCounter'");
    view = finder.findRequiredView(source, 2131493056, "field 'checkboxRepeat'");
    target.checkboxRepeat = finder.castView(view, 2131493056, "field 'checkboxRepeat'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.textDeviceName = null;
    target.textDeviceAddress = null;
    target.textConnectionStatus = null;
    target.textQppNotify = null;
    target.textQppDataRate = null;
    target.editSend = null;
    target.btnQppTextSend = null;
    target.textRepeatCounter = null;
    target.checkboxRepeat = null;
  }
}
