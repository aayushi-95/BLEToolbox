// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.view;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceInfoDialog$$ViewBinder<T extends com.freescale.bletoolbox.view.DeviceInfoDialog> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493092, "field 'mManufactureName'");
    target.mManufactureName = finder.castView(view, 2131493092, "field 'mManufactureName'");
    view = finder.findRequiredView(source, 2131493093, "field 'mModelName'");
    target.mModelName = finder.castView(view, 2131493093, "field 'mModelName'");
    view = finder.findRequiredView(source, 2131493094, "field 'mSerialNumber'");
    target.mSerialNumber = finder.castView(view, 2131493094, "field 'mSerialNumber'");
    view = finder.findRequiredView(source, 2131493095, "field 'mHardwareRev'");
    target.mHardwareRev = finder.castView(view, 2131493095, "field 'mHardwareRev'");
    view = finder.findRequiredView(source, 2131493096, "field 'mFirmwareRev'");
    target.mFirmwareRev = finder.castView(view, 2131493096, "field 'mFirmwareRev'");
    view = finder.findRequiredView(source, 2131493097, "field 'mSoftwareRev'");
    target.mSoftwareRev = finder.castView(view, 2131493097, "field 'mSoftwareRev'");
    view = finder.findRequiredView(source, 2131493098, "field 'mSystemId'");
    target.mSystemId = finder.castView(view, 2131493098, "field 'mSystemId'");
    view = finder.findRequiredView(source, 2131493099, "field 'mCertData'");
    target.mCertData = finder.castView(view, 2131493099, "field 'mCertData'");
  }

  @Override public void unbind(T target) {
    target.mManufactureName = null;
    target.mModelName = null;
    target.mSerialNumber = null;
    target.mHardwareRev = null;
    target.mFirmwareRev = null;
    target.mSoftwareRev = null;
    target.mSystemId = null;
    target.mCertData = null;
  }
}
