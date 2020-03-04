// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.view;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class WheelSizePickerDialog$WheelSizeHolder$$ViewBinder<T extends com.freescale.bletoolbox.view.WheelSizePickerDialog.WheelSizeHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493187, "field 'name'");
    target.name = finder.castView(view, 2131493187, "field 'name'");
    view = finder.findRequiredView(source, 2131493185, "field 'value'");
    target.value = finder.castView(view, 2131493185, "field 'value'");
  }

  @Override public void unbind(T target) {
    target.name = null;
    target.value = null;
  }
}
