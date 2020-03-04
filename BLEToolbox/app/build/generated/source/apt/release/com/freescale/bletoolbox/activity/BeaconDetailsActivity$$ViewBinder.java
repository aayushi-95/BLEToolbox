// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class BeaconDetailsActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.BeaconDetailsActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492972, "field 'mActionSpinner'");
    target.mActionSpinner = finder.castView(view, 2131492972, "field 'mActionSpinner'");
    view = finder.findRequiredView(source, 2131492973, "field 'mMessageWrapper'");
    target.mMessageWrapper = view;
    view = finder.findRequiredView(source, 2131492974, "field 'mMessage'");
    target.mMessage = finder.castView(view, 2131492974, "field 'mMessage'");
    view = finder.findRequiredView(source, 2131492975, "field 'mMessageCount'");
    target.mMessageCount = finder.castView(view, 2131492975, "field 'mMessageCount'");
    view = finder.findRequiredView(source, 2131493080, "field 'mManufacture'");
    target.mManufacture = finder.castView(view, 2131493080, "field 'mManufacture'");
    view = finder.findRequiredView(source, 2131493081, "field 'mUuid'");
    target.mUuid = finder.castView(view, 2131493081, "field 'mUuid'");
    view = finder.findRequiredView(source, 2131493082, "field 'mDataA'");
    target.mDataA = finder.castView(view, 2131493082, "field 'mDataA'");
    view = finder.findRequiredView(source, 2131493083, "field 'mDataB'");
    target.mDataB = finder.castView(view, 2131493083, "field 'mDataB'");
    view = finder.findRequiredView(source, 2131493084, "field 'mDataC'");
    target.mDataC = finder.castView(view, 2131493084, "field 'mDataC'");
    view = finder.findRequiredView(source, 2131493085, "field 'mRssi'");
    target.mRssi = finder.castView(view, 2131493085, "field 'mRssi'");
  }

  @Override public void unbind(T target) {
    target.mActionSpinner = null;
    target.mMessageWrapper = null;
    target.mMessage = null;
    target.mMessageCount = null;
    target.mManufacture = null;
    target.mUuid = null;
    target.mDataA = null;
    target.mDataB = null;
    target.mDataC = null;
    target.mRssi = null;
  }
}
