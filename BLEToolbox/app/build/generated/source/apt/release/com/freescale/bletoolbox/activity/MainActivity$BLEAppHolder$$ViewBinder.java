// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$BLEAppHolder$$ViewBinder<T extends com.freescale.bletoolbox.activity.MainActivity.BLEAppHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493087, "field 'title'");
    target.title = finder.castView(view, 2131493087, "field 'title'");
  }

  @Override public void unbind(T target) {
    target.title = null;
  }
}
