// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class FRMDActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.FRMDActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493010, "field 'recyclerFRDM'");
    target.recyclerFRDM = finder.castView(view, 2131493010, "field 'recyclerFRDM'");
    view = finder.findRequiredView(source, 2131493011, "field 'framelayout'");
    target.framelayout = finder.castView(view, 2131493011, "field 'framelayout'");
    view = finder.findRequiredView(source, 2131493009, "field 'progressBar'");
    target.progressBar = finder.castView(view, 2131493009, "field 'progressBar'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.recyclerFRDM = null;
    target.framelayout = null;
    target.progressBar = null;
  }
}
