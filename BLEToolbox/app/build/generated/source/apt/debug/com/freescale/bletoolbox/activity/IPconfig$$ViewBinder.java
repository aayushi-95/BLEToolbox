// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class IPconfig$$ViewBinder<T extends com.freescale.bletoolbox.activity.IPconfig> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131492990, "field 'submit'");
    target.submit = finder.castView(view, 2131492990, "field 'submit'");
    view = finder.findRequiredView(source, 2131492987, "field 'IP'");
    target.IP = finder.castView(view, 2131492987, "field 'IP'");
    view = finder.findRequiredView(source, 2131492988, "field 'Subnet'");
    target.Subnet = finder.castView(view, 2131492988, "field 'Subnet'");
    view = finder.findRequiredView(source, 2131492989, "field 'Gateway'");
    target.Gateway = finder.castView(view, 2131492989, "field 'Gateway'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.submit = null;
    target.IP = null;
    target.Subnet = null;
    target.Gateway = null;
  }
}
