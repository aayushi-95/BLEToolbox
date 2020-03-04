// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.view;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class WheelSizePickerDialog$$ViewBinder<T extends com.freescale.bletoolbox.view.WheelSizePickerDialog> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493186, "field 'mRecyclerView'");
    target.mRecyclerView = finder.castView(view, 2131493186, "field 'mRecyclerView'");
  }

  @Override public void unbind(T target) {
    target.mRecyclerView = null;
  }
}
