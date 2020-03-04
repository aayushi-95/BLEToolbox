// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ControllerFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.KW40_Fragments.ControllerFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493134, "field 'controlMute'");
    target.controlMute = finder.castView(view, 2131493134, "field 'controlMute'");
    view = finder.findRequiredView(source, 2131493135, "field 'controlPower'");
    target.controlPower = finder.castView(view, 2131493135, "field 'controlPower'");
    view = finder.findRequiredView(source, 2131493136, "field 'controlVolumeUp'");
    target.controlVolumeUp = finder.castView(view, 2131493136, "field 'controlVolumeUp'");
    view = finder.findRequiredView(source, 2131493137, "field 'controlChannelUp'");
    target.controlChannelUp = finder.castView(view, 2131493137, "field 'controlChannelUp'");
    view = finder.findRequiredView(source, 2131493138, "field 'controlVolumeDown'");
    target.controlVolumeDown = finder.castView(view, 2131493138, "field 'controlVolumeDown'");
    view = finder.findRequiredView(source, 2131493139, "field 'controlChannelDown'");
    target.controlChannelDown = finder.castView(view, 2131493139, "field 'controlChannelDown'");
    view = finder.findRequiredView(source, 2131493140, "field 'controlSpinner'");
    target.controlSpinner = finder.castView(view, 2131493140, "field 'controlSpinner'");
  }

  @Override public void unbind(T target) {
    target.controlMute = null;
    target.controlPower = null;
    target.controlVolumeUp = null;
    target.controlChannelUp = null;
    target.controlVolumeDown = null;
    target.controlChannelDown = null;
    target.controlSpinner = null;
  }
}
