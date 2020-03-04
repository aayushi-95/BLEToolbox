// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class WuartActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.WuartActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493093, "field 'mMessageField'");
    target.mMessageField = finder.castView(view, 2131493093, "field 'mMessageField'");
    view = finder.findRequiredView(source, 2131493092, "field 'wuartAllMessages'");
    target.wuartAllMessages = finder.castView(view, 2131493092, "field 'wuartAllMessages'");
    view = finder.findRequiredView(source, 2131493091, "field 'scrollView'");
    target.scrollView = finder.castView(view, 2131493091, "field 'scrollView'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.mMessageField = null;
    target.wuartAllMessages = null;
    target.scrollView = null;
  }
}
