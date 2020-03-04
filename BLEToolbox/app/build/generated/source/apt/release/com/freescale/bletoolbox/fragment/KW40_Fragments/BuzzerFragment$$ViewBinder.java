// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class BuzzerFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.KW40_Fragments.BuzzerFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493110, "field 'lnBuzzer'");
    target.lnBuzzer = finder.castView(view, 2131493110, "field 'lnBuzzer'");
    view = finder.findRequiredView(source, 2131493111, "field 'imgBuzzer'");
    target.imgBuzzer = finder.castView(view, 2131493111, "field 'imgBuzzer'");
    view = finder.findRequiredView(source, 2131493112, "field 'txtBuzzer'");
    target.txtBuzzer = finder.castView(view, 2131493112, "field 'txtBuzzer'");
  }

  @Override public void unbind(T target) {
    target.lnBuzzer = null;
    target.imgBuzzer = null;
    target.txtBuzzer = null;
  }
}
