// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class BeaconActivity$BeaconHolder$$ViewBinder<T extends com.freescale.bletoolbox.activity.BeaconActivity.BeaconHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493094, "field 'manufacture'");
    target.manufacture = finder.castView(view, 2131493094, "field 'manufacture'");
    view = finder.findRequiredView(source, 2131493095, "field 'uuid'");
    target.uuid = finder.castView(view, 2131493095, "field 'uuid'");
    view = finder.findRequiredView(source, 2131493096, "field 'dataA'");
    target.dataA = finder.castView(view, 2131493096, "field 'dataA'");
    view = finder.findRequiredView(source, 2131493097, "field 'dataB'");
    target.dataB = finder.castView(view, 2131493097, "field 'dataB'");
    view = finder.findRequiredView(source, 2131493098, "field 'dataC'");
    target.dataC = finder.castView(view, 2131493098, "field 'dataC'");
    view = finder.findRequiredView(source, 2131493099, "field 'rssi'");
    target.rssi = finder.castView(view, 2131493099, "field 'rssi'");
    view = finder.findRequiredView(source, 2131493100, "field 'message'");
    target.message = finder.castView(view, 2131493100, "field 'message'");
  }

  @Override public void unbind(T target) {
    target.manufacture = null;
    target.uuid = null;
    target.dataA = null;
    target.dataB = null;
    target.dataC = null;
    target.rssi = null;
    target.message = null;
  }
}
