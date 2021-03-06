// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class CSCActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.CSCActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131492986, "field 'mCscSpeed'");
    target.mCscSpeed = finder.castView(view, 2131492986, "field 'mCscSpeed'");
    view = finder.findRequiredView(source, 2131492987, "field 'mCscSpeedUnit'");
    target.mCscSpeedUnit = finder.castView(view, 2131492987, "field 'mCscSpeedUnit'");
    view = finder.findRequiredView(source, 2131492988, "field 'mCscCadence'");
    target.mCscCadence = finder.castView(view, 2131492988, "field 'mCscCadence'");
    view = finder.findRequiredView(source, 2131492989, "field 'mCscCadenceUnit'");
    target.mCscCadenceUnit = finder.castView(view, 2131492989, "field 'mCscCadenceUnit'");
    view = finder.findRequiredView(source, 2131492993, "field 'mCscSensorLocation'");
    target.mCscSensorLocation = finder.castView(view, 2131492993, "field 'mCscSensorLocation'");
    view = finder.findRequiredView(source, 2131492990, "field 'mCscWheelSize'");
    target.mCscWheelSize = finder.castView(view, 2131492990, "field 'mCscWheelSize'");
    view = finder.findRequiredView(source, 2131492991, "field 'mCscWheelPicker' and method 'pickWheelSize'");
    target.mCscWheelPicker = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.pickWheelSize();
        }
      });
    view = finder.findRequiredView(source, 2131492992, "field 'mCscWheelUnit'");
    target.mCscWheelUnit = view;
    view = finder.findRequiredView(source, 2131492994, "field 'log'");
    target.log = finder.castView(view, 2131492994, "field 'log'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.mCscSpeed = null;
    target.mCscSpeedUnit = null;
    target.mCscCadence = null;
    target.mCscCadenceUnit = null;
    target.mCscSensorLocation = null;
    target.mCscWheelSize = null;
    target.mCscWheelPicker = null;
    target.mCscWheelUnit = null;
    target.log = null;
  }
}
