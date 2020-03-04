// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class RSCActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.RSCActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493070, "field 'mRscInstantaneousSpeed'");
    target.mRscInstantaneousSpeed = finder.castView(view, 2131493070, "field 'mRscInstantaneousSpeed'");
    view = finder.findRequiredView(source, 2131493071, "field 'mRscInstantaneousCadence'");
    target.mRscInstantaneousCadence = finder.castView(view, 2131493071, "field 'mRscInstantaneousCadence'");
    view = finder.findRequiredView(source, 2131493072, "field 'mRunningStatus'");
    target.mRunningStatus = finder.castView(view, 2131493072, "field 'mRunningStatus'");
    view = finder.findRequiredView(source, 2131493073, "field 'mRscStrideLength'");
    target.mRscStrideLength = finder.castView(view, 2131493073, "field 'mRscStrideLength'");
    view = finder.findRequiredView(source, 2131493074, "field 'mRscTotalDistance'");
    target.mRscTotalDistance = finder.castView(view, 2131493074, "field 'mRscTotalDistance'");
    view = finder.findRequiredView(source, 2131493075, "field 'mRscSensorLocation'");
    target.mRscSensorLocation = finder.castView(view, 2131493075, "field 'mRscSensorLocation'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.mRscInstantaneousSpeed = null;
    target.mRscInstantaneousCadence = null;
    target.mRunningStatus = null;
    target.mRscStrideLength = null;
    target.mRscTotalDistance = null;
    target.mRscSensorLocation = null;
  }
}
