// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.fragment.KW40_Fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ControllerFragment$$ViewBinder<T extends com.freescale.bletoolbox.fragment.KW40_Fragments.ControllerFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493120, "field 'controlMute'");
    target.controlMute = finder.castView(view, 2131493120, "field 'controlMute'");
    view = finder.findRequiredView(source, 2131493121, "field 'controlPower'");
    target.controlPower = finder.castView(view, 2131493121, "field 'controlPower'");
    view = finder.findRequiredView(source, 2131493122, "field 'controlVolumeUp'");
    target.controlVolumeUp = finder.castView(view, 2131493122, "field 'controlVolumeUp'");
    view = finder.findRequiredView(source, 2131493123, "field 'controlChannelUp'");
    target.controlChannelUp = finder.castView(view, 2131493123, "field 'controlChannelUp'");
    view = finder.findRequiredView(source, 2131493124, "field 'controlVolumeDown'");
    target.controlVolumeDown = finder.castView(view, 2131493124, "field 'controlVolumeDown'");
    view = finder.findRequiredView(source, 2131493125, "field 'controlChannelDown'");
    target.controlChannelDown = finder.castView(view, 2131493125, "field 'controlChannelDown'");
    view = finder.findRequiredView(source, 2131493126, "field 'controlSpinner'");
    target.controlSpinner = finder.castView(view, 2131493126, "field 'controlSpinner'");
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
