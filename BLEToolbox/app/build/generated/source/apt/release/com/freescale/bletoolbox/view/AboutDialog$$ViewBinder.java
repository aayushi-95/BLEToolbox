// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.view;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class AboutDialog$$ViewBinder<T extends com.freescale.bletoolbox.view.AboutDialog> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492970, "field 'mAppInfo'");
    target.mAppInfo = finder.castView(view, 2131492970, "field 'mAppInfo'");
    view = finder.findRequiredView(source, 2131492969, "method 'viewFslLink'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.viewFslLink();
        }
      });
  }

  @Override public void unbind(T target) {
    target.mAppInfo = null;
  }
}
