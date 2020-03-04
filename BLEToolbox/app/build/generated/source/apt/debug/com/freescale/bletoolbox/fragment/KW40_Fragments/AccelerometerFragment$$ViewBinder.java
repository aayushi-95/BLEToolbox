// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class AccelerometerFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.KW40_Fragments.AccelerometerFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493123, "field 'lineChart'");
    target.lineChart = finder.castView(view, 2131493123, "field 'lineChart'");
    view = finder.findRequiredView(source, 2131493122, "field 'tvAcclerometterStart'");
    target.tvAcclerometterStart = finder.castView(view, 2131493122, "field 'tvAcclerometterStart'");
  }

  @Override public void unbind(T target) {
    target.lineChart = null;
    target.tvAcclerometterStart = null;
  }
}
