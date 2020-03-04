// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class LedFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.KW40_Fragments.LedFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493130, "field 'togLed2'");
    target.togLed2 = finder.castView(view, 2131493130, "field 'togLed2'");
    view = finder.findRequiredView(source, 2131493131, "field 'togLed3'");
    target.togLed3 = finder.castView(view, 2131493131, "field 'togLed3'");
    view = finder.findRequiredView(source, 2131493132, "field 'togLed4'");
    target.togLed4 = finder.castView(view, 2131493132, "field 'togLed4'");
  }

  @Override public void unbind(T target) {
    target.togLed2 = null;
    target.togLed3 = null;
    target.togLed4 = null;
  }
}
