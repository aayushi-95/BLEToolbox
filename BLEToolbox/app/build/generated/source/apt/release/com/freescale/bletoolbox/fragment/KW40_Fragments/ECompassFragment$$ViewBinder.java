// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ECompassFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.KW40_Fragments.ECompassFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493116, "field 'imageViewCompass'");
    target.imageViewCompass = finder.castView(view, 2131493116, "field 'imageViewCompass'");
    view = finder.findRequiredView(source, 2131493113, "field 'tvCompassStop'");
    target.tvCompassStop = finder.castView(view, 2131493113, "field 'tvCompassStop'");
    view = finder.findRequiredView(source, 2131493119, "field 'tvDigit'");
    target.tvDigit = finder.castView(view, 2131493119, "field 'tvDigit'");
  }

  @Override public void unbind(T target) {
    target.imageViewCompass = null;
    target.tvCompassStop = null;
    target.tvDigit = null;
  }
}
