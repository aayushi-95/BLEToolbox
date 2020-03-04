// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class HealthThermometerActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.HealthThermometerActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493007, "field 'mHealthValue'");
    target.mHealthValue = finder.castView(view, 2131493007, "field 'mHealthValue'");
    view = finder.findRequiredView(source, 2131493008, "field 'mHealthSensorLocation'");
    target.mHealthSensorLocation = finder.castView(view, 2131493008, "field 'mHealthSensorLocation'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.mHealthValue = null;
    target.mHealthSensorLocation = null;
  }
}
