// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class GlucoseActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.GlucoseActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493001, "field 'tvGMvalue'");
    target.tvGMvalue = finder.castView(view, 2131493001, "field 'tvGMvalue'");
    view = finder.findRequiredView(source, 2131493002, "field 'tvGMTime'");
    target.tvGMTime = finder.castView(view, 2131493002, "field 'tvGMTime'");
    view = finder.findRequiredView(source, 2131493003, "field 'tvGMDate'");
    target.tvGMDate = finder.castView(view, 2131493003, "field 'tvGMDate'");
    view = finder.findRequiredView(source, 2131493006, "field 'btnGet'");
    target.btnGet = finder.castView(view, 2131493006, "field 'btnGet'");
    view = finder.findRequiredView(source, 2131493005, "field 'ivNext'");
    target.ivNext = finder.castView(view, 2131493005, "field 'ivNext'");
    view = finder.findRequiredView(source, 2131493004, "field 'ivPrevious'");
    target.ivPrevious = finder.castView(view, 2131493004, "field 'ivPrevious'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.tvGMvalue = null;
    target.tvGMTime = null;
    target.tvGMDate = null;
    target.btnGet = null;
    target.ivNext = null;
    target.ivPrevious = null;
  }
}
