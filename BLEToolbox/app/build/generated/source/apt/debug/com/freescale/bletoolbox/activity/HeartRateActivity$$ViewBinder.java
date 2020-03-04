// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class HeartRateActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.HeartRateActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493022, "field 'mHeartRateMeasurement'");
    target.mHeartRateMeasurement = finder.castView(view, 2131493022, "field 'mHeartRateMeasurement'");
    view = finder.findRequiredView(source, 2131493023, "field 'mHeartRateUnit'");
    target.mHeartRateUnit = finder.castView(view, 2131493023, "field 'mHeartRateUnit'");
    view = finder.findRequiredView(source, 2131493024, "field 'mHeartRateSensorLocation'");
    target.mHeartRateSensorLocation = finder.castView(view, 2131493024, "field 'mHeartRateSensorLocation'");
    view = finder.findRequiredView(source, 2131493025, "field 'mChartContainer'");
    target.mChartContainer = view;
    view = finder.findRequiredView(source, 2131493026, "field 'mHeartRateChart'");
    target.mHeartRateChart = finder.castView(view, 2131493026, "field 'mHeartRateChart'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.mHeartRateMeasurement = null;
    target.mHeartRateUnit = null;
    target.mHeartRateSensorLocation = null;
    target.mChartContainer = null;
    target.mHeartRateChart = null;
  }
}
