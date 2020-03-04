// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class BaseServiceActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.BaseServiceActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findOptionalView(source, 2131493179, null);
    target.mStatusConnection = finder.castView(view, 2131493179, "field 'mStatusConnection'");
    view = finder.findOptionalView(source, 2131493180, null);
    target.mStatusBattery = finder.castView(view, 2131493180, "field 'mStatusBattery'");
    view = finder.findRequiredView(source, 2131493151, "method 'viewDeviceInfo'");
    target.mInfoButton = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.viewDeviceInfo();
        }
      });
  }

  @Override public void unbind(T target) {
    target.mStatusConnection = null;
    target.mStatusBattery = null;
    target.mInfoButton = null;
  }
}
