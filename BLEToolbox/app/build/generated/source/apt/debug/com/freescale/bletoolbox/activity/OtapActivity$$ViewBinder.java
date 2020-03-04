// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class OtapActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.OtapActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493032, "field 'm_otapTvFileName'");
    target.m_otapTvFileName = finder.castView(view, 2131493032, "field 'm_otapTvFileName'");
    view = finder.findRequiredView(source, 2131493034, "field 'm_otapTvFileSize'");
    target.m_otapTvFileSize = finder.castView(view, 2131493034, "field 'm_otapTvFileSize'");
    view = finder.findRequiredView(source, 2131493033, "field 'm_otapTvFileVer'");
    target.m_otapTvFileVer = finder.castView(view, 2131493033, "field 'm_otapTvFileVer'");
    view = finder.findRequiredView(source, 2131493035, "field 'm_otapTvFileStatus'");
    target.m_otapTvFileStatus = finder.castView(view, 2131493035, "field 'm_otapTvFileStatus'");
    view = finder.findRequiredView(source, 2131493036, "field 'm_otapBtOpen'");
    target.m_otapBtOpen = finder.castView(view, 2131493036, "field 'm_otapBtOpen'");
    view = finder.findRequiredView(source, 2131493040, "field 'm_otapBtUpload'");
    target.m_otapBtUpload = finder.castView(view, 2131493040, "field 'm_otapBtUpload'");
    view = finder.findRequiredView(source, 2131493038, "field 'm_otapPbUpload'");
    target.m_otapPbUpload = finder.castView(view, 2131493038, "field 'm_otapPbUpload'");
    view = finder.findRequiredView(source, 2131493039, "field 'tvProgressNumber'");
    target.tvProgressNumber = finder.castView(view, 2131493039, "field 'tvProgressNumber'");
    view = finder.findRequiredView(source, 2131493037, "field 'tvSettingDelay'");
    target.tvSettingDelay = finder.castView(view, 2131493037, "field 'tvSettingDelay'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.m_otapTvFileName = null;
    target.m_otapTvFileSize = null;
    target.m_otapTvFileVer = null;
    target.m_otapTvFileStatus = null;
    target.m_otapBtOpen = null;
    target.m_otapBtUpload = null;
    target.m_otapPbUpload = null;
    target.tvProgressNumber = null;
    target.tvSettingDelay = null;
  }
}
