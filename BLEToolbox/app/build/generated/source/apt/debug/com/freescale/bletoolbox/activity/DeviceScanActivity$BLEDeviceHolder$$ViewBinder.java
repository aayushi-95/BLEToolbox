// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceScanActivity$BLEDeviceHolder$$ViewBinder<T extends com.freescale.bletoolbox.activity.DeviceScanActivity.BLEDeviceHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493102, "field 'name'");
    target.name = finder.castView(view, 2131493102, "field 'name'");
    view = finder.findRequiredView(source, 2131493103, "field 'mac'");
    target.mac = finder.castView(view, 2131493103, "field 'mac'");
    view = finder.findRequiredView(source, 2131493104, "field 'bond'");
    target.bond = finder.castView(view, 2131493104, "field 'bond'");
    view = finder.findRequiredView(source, 2131493105, "field 'rssi'");
    target.rssi = finder.castView(view, 2131493105, "field 'rssi'");
  }

  @Override public void unbind(T target) {
    target.name = null;
    target.mac = null;
    target.bond = null;
    target.rssi = null;
  }
}
