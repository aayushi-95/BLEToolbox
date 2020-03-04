// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class ProximityActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.ProximityActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493024, "field 'm_tvRssi'");
    target.m_tvRssi = finder.castView(view, 2131493024, "field 'm_tvRssi'");
    view = finder.findRequiredView(source, 2131493025, "field 'm_layoutTxPower'");
    target.m_layoutTxPower = finder.castView(view, 2131493025, "field 'm_layoutTxPower'");
    view = finder.findRequiredView(source, 2131493026, "field 'm_tvTxPower'");
    target.m_tvTxPower = finder.castView(view, 2131493026, "field 'm_tvTxPower'");
    view = finder.findRequiredView(source, 2131493027, "field 'm_tvAlerLevel'");
    target.m_tvAlerLevel = finder.castView(view, 2131493027, "field 'm_tvAlerLevel'");
    view = finder.findRequiredView(source, 2131493028, "field 'm_btAlerHigh'");
    target.m_btAlerHigh = finder.castView(view, 2131493028, "field 'm_btAlerHigh'");
    view = finder.findRequiredView(source, 2131493029, "field 'm_btAlerMild'");
    target.m_btAlerMild = finder.castView(view, 2131493029, "field 'm_btAlerMild'");
    view = finder.findRequiredView(source, 2131493030, "field 'm_btAlerOff'");
    target.m_btAlerOff = finder.castView(view, 2131493030, "field 'm_btAlerOff'");
    view = finder.findRequiredView(source, 2131493031, "field 'm_layoutImmediate'");
    target.m_layoutImmediate = finder.castView(view, 2131493031, "field 'm_layoutImmediate'");
    view = finder.findRequiredView(source, 2131493032, "field 'm_btAlerHighImmed'");
    target.m_btAlerHighImmed = finder.castView(view, 2131493032, "field 'm_btAlerHighImmed'");
    view = finder.findRequiredView(source, 2131493033, "field 'm_btAlerMildImmed'");
    target.m_btAlerMildImmed = finder.castView(view, 2131493033, "field 'm_btAlerMildImmed'");
    view = finder.findRequiredView(source, 2131493034, "field 'm_btAlerOffImmed'");
    target.m_btAlerOffImmed = finder.castView(view, 2131493034, "field 'm_btAlerOffImmed'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.m_tvRssi = null;
    target.m_layoutTxPower = null;
    target.m_tvTxPower = null;
    target.m_tvAlerLevel = null;
    target.m_btAlerHigh = null;
    target.m_btAlerMild = null;
    target.m_btAlerOff = null;
    target.m_layoutImmediate = null;
    target.m_btAlerHighImmed = null;
    target.m_btAlerMildImmed = null;
    target.m_btAlerOffImmed = null;
  }
}
