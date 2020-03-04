// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class InputFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.KW40_Fragments.InputFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493127, "field 'imgSw1'");
    target.imgSw1 = finder.castView(view, 2131493127, "field 'imgSw1'");
    view = finder.findRequiredView(source, 2131493128, "field 'imgSw2'");
    target.imgSw2 = finder.castView(view, 2131493128, "field 'imgSw2'");
    view = finder.findRequiredView(source, 2131493129, "field 'imgSw3'");
    target.imgSw3 = finder.castView(view, 2131493129, "field 'imgSw3'");
  }

  @Override public void unbind(T target) {
    target.imgSw1 = null;
    target.imgSw2 = null;
    target.imgSw3 = null;
  }
}
