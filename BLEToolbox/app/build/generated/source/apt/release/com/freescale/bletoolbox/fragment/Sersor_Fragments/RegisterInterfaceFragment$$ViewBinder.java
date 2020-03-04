// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.Sersor_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class RegisterInterfaceFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.Sersor_Fragments.RegisterInterfaceFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493054, "field 'registerScrollView'");
    target.registerScrollView = finder.castView(view, 2131493054, "field 'registerScrollView'");
    view = finder.findRequiredView(source, 2131493055, "field 'registerDataView'");
    target.registerDataView = finder.castView(view, 2131493055, "field 'registerDataView'");
    view = finder.findRequiredView(source, 2131493049, "field 'offset'");
    target.offset = finder.castView(view, 2131493049, "field 'offset'");
    view = finder.findRequiredView(source, 2131493050, "field 'bytes'");
    target.bytes = finder.castView(view, 2131493050, "field 'bytes'");
    view = finder.findRequiredView(source, 2131493046, "field 'sensorType'");
    target.sensorType = finder.castView(view, 2131493046, "field 'sensorType'");
    view = finder.findRequiredView(source, 2131493048, "field 'sensorOperation'");
    target.sensorOperation = finder.castView(view, 2131493048, "field 'sensorOperation'");
    view = finder.findRequiredView(source, 2131493051, "field 'go'");
    target.go = finder.castView(view, 2131493051, "field 'go'");
    view = finder.findRequiredView(source, 2131493053, "field 'clear'");
    target.clear = finder.castView(view, 2131493053, "field 'clear'");
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
