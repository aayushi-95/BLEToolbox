// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.Sersor_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SensorDataFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.Sersor_Fragments.SensorDataFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493141, "field 'datacontener'");
    target.datacontener = finder.castView(view, 2131493141, "field 'datacontener'");
  }

  @Override public void unbind(T target) {
    target.datacontener = null;
  }
}
