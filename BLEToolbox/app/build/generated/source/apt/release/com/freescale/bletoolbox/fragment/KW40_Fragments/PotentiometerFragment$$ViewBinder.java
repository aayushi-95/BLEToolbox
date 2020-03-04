// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PotentiometerFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.KW40_Fragments.PotentiometerFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493133, "field 'ivPercent'");
    target.ivPercent = finder.castView(view, 2131493133, "field 'ivPercent'");
    view = finder.findRequiredView(source, 2131493134, "field 'tvPercent'");
    target.tvPercent = finder.castView(view, 2131493134, "field 'tvPercent'");
  }

  @Override public void unbind(T target) {
    target.ivPercent = null;
    target.tvPercent = null;
  }
}
