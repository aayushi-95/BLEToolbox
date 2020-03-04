// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class BloodPressureActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.BloodPressureActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131492976, "field 'tvSystolic'");
    target.tvSystolic = finder.castView(view, 2131492976, "field 'tvSystolic'");
    view = finder.findRequiredView(source, 2131492977, "field 'tvUnitSystolic'");
    target.tvUnitSystolic = finder.castView(view, 2131492977, "field 'tvUnitSystolic'");
    view = finder.findRequiredView(source, 2131492978, "field 'tvDiastolic'");
    target.tvDiastolic = finder.castView(view, 2131492978, "field 'tvDiastolic'");
    view = finder.findRequiredView(source, 2131492979, "field 'tvUnitDiastolic'");
    target.tvUnitDiastolic = finder.castView(view, 2131492979, "field 'tvUnitDiastolic'");
    view = finder.findRequiredView(source, 2131492980, "field 'tvMAP'");
    target.tvMAP = finder.castView(view, 2131492980, "field 'tvMAP'");
    view = finder.findRequiredView(source, 2131492981, "field 'tvUnitMAP'");
    target.tvUnitMAP = finder.castView(view, 2131492981, "field 'tvUnitMAP'");
    view = finder.findRequiredView(source, 2131492982, "field 'tvPulseRate'");
    target.tvPulseRate = finder.castView(view, 2131492982, "field 'tvPulseRate'");
    view = finder.findRequiredView(source, 2131492985, "field 'tvDate'");
    target.tvDate = finder.castView(view, 2131492985, "field 'tvDate'");
    view = finder.findRequiredView(source, 2131492984, "field 'tvTime'");
    target.tvTime = finder.castView(view, 2131492984, "field 'tvTime'");
    view = finder.findRequiredView(source, 2131492983, "field 'tvDateTimeDisconnected'");
    target.tvDateTimeDisconnected = finder.castView(view, 2131492983, "field 'tvDateTimeDisconnected'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.tvSystolic = null;
    target.tvUnitSystolic = null;
    target.tvDiastolic = null;
    target.tvUnitDiastolic = null;
    target.tvMAP = null;
    target.tvUnitMAP = null;
    target.tvPulseRate = null;
    target.tvDate = null;
    target.tvTime = null;
    target.tvDateTimeDisconnected = null;
  }
}
