// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SenorActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.SenorActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493062, "field 'recyclerSENSOR'");
    target.recyclerSENSOR = finder.castView(view, 2131493062, "field 'recyclerSENSOR'");
    view = finder.findRequiredView(source, 2131492998, "field 'framelayout'");
    target.framelayout = finder.castView(view, 2131492998, "field 'framelayout'");
    view = finder.findRequiredView(source, 2131492996, "field 'progressBar'");
    target.progressBar = finder.castView(view, 2131492996, "field 'progressBar'");
    view = finder.findRequiredView(source, 2131493178, "field 'bottom_status_bar'");
    target.bottom_status_bar = view;
    view = finder.findRequiredView(source, 2131493063, "field 'registerBtn'");
    target.registerBtn = finder.castView(view, 2131493063, "field 'registerBtn'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.recyclerSENSOR = null;
    target.framelayout = null;
    target.progressBar = null;
    target.bottom_status_bar = null;
    target.registerBtn = null;
  }
}
