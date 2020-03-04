// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class TemperatureFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.KW40_Fragments.TemperatureFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493143, "field 'txtTemperature'");
    target.txtTemperature = finder.castView(view, 2131493143, "field 'txtTemperature'");
    view = finder.findRequiredView(source, 2131493142, "field 'frameLayout'");
    target.frameLayout = finder.castView(view, 2131493142, "field 'frameLayout'");
  }

  @Override public void unbind(T target) {
    target.txtTemperature = null;
    target.frameLayout = null;
  }
}
