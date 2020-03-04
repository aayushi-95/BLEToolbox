// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.view;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceInfoDialog$$ViewBinder<T extends com.freescale.bletoolbox.view.DeviceInfoDialog> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493106, "field 'mManufactureName'");
    target.mManufactureName = finder.castView(view, 2131493106, "field 'mManufactureName'");
    view = finder.findRequiredView(source, 2131493107, "field 'mModelName'");
    target.mModelName = finder.castView(view, 2131493107, "field 'mModelName'");
    view = finder.findRequiredView(source, 2131493108, "field 'mSerialNumber'");
    target.mSerialNumber = finder.castView(view, 2131493108, "field 'mSerialNumber'");
    view = finder.findRequiredView(source, 2131493109, "field 'mHardwareRev'");
    target.mHardwareRev = finder.castView(view, 2131493109, "field 'mHardwareRev'");
    view = finder.findRequiredView(source, 2131493110, "field 'mFirmwareRev'");
    target.mFirmwareRev = finder.castView(view, 2131493110, "field 'mFirmwareRev'");
    view = finder.findRequiredView(source, 2131493111, "field 'mSoftwareRev'");
    target.mSoftwareRev = finder.castView(view, 2131493111, "field 'mSoftwareRev'");
    view = finder.findRequiredView(source, 2131493112, "field 'mSystemId'");
    target.mSystemId = finder.castView(view, 2131493112, "field 'mSystemId'");
    view = finder.findRequiredView(source, 2131493113, "field 'mCertData'");
    target.mCertData = finder.castView(view, 2131493113, "field 'mCertData'");
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
