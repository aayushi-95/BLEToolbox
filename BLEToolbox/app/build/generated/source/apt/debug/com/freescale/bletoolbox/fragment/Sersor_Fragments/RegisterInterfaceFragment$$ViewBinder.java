// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.Sersor_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class RegisterInterfaceFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.Sersor_Fragments.RegisterInterfaceFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493068, "field 'registerScrollView'");
    target.registerScrollView = finder.castView(view, 2131493068, "field 'registerScrollView'");
    view = finder.findRequiredView(source, 2131493069, "field 'registerDataView'");
    target.registerDataView = finder.castView(view, 2131493069, "field 'registerDataView'");
    view = finder.findRequiredView(source, 2131493064, "field 'offset'");
    target.offset = finder.castView(view, 2131493064, "field 'offset'");
    view = finder.findRequiredView(source, 2131493065, "field 'bytes'");
    target.bytes = finder.castView(view, 2131493065, "field 'bytes'");
    view = finder.findRequiredView(source, 2131493061, "field 'sensorType'");
    target.sensorType = finder.castView(view, 2131493061, "field 'sensorType'");
    view = finder.findRequiredView(source, 2131493063, "field 'sensorOperation'");
    target.sensorOperation = finder.castView(view, 2131493063, "field 'sensorOperation'");
    view = finder.findRequiredView(source, 2131493066, "field 'go'");
    target.go = finder.castView(view, 2131493066, "field 'go'");
    view = finder.findRequiredView(source, 2131493067, "field 'clear'");
    target.clear = finder.castView(view, 2131493067, "field 'clear'");
  }

  @Override public void unbind(T target) {
    target.registerScrollView = null;
    target.registerDataView = null;
    target.offset = null;
    target.bytes = null;
    target.sensorType = null;
    target.sensorOperation = null;
    target.go = null;
    target.clear = null;
  }
}
