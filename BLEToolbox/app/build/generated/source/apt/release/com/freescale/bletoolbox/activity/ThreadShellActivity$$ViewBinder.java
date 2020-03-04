// Generated code from Butter Knife. Do not modify!
package com.freescale.bletoolbox.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class ThreadShellActivity$$ViewBinder<T extends com.freescale.bletoolbox.activity.ThreadShellActivity> extends com.freescale.bletoolbox.activity.BaseServiceActivity$$ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    super.bind(finder, target, source);

    View view;
    view = finder.findRequiredView(source, 2131493074, "field 'lvShortcuts'");
    target.lvShortcuts = finder.castView(view, 2131493074, "field 'lvShortcuts'");
    view = finder.findRequiredView(source, 2131493075, "field 'lvRecent'");
    target.lvRecent = finder.castView(view, 2131493075, "field 'lvRecent'");
    view = finder.findRequiredView(source, 2131493070, "field 'lnShortcut'");
    target.lnShortcut = finder.castView(view, 2131493070, "field 'lnShortcut'");
    view = finder.findRequiredView(source, 2131493072, "field 'lnRecent'");
    target.lnRecent = finder.castView(view, 2131493072, "field 'lnRecent'");
    view = finder.findRequiredView(source, 2131493066, "field 'imgAddShortcuts'");
    target.imgAddShortcuts = finder.castView(view, 2131493066, "field 'imgAddShortcuts'");
    view = finder.findRequiredView(source, 2131493068, "field 'imgSendCommand'");
    target.imgSendCommand = finder.castView(view, 2131493068, "field 'imgSendCommand'");
    view = finder.findRequiredView(source, 2131493067, "field 'mMessageField'");
    target.mMessageField = finder.castView(view, 2131493067, "field 'mMessageField'");
    view = finder.findRequiredView(source, 2131493065, "field 'threadAllMessages'");
    target.threadAllMessages = finder.castView(view, 2131493065, "field 'threadAllMessages'");
    view = finder.findRequiredView(source, 2131493064, "field 'scrollView'");
    target.scrollView = finder.castView(view, 2131493064, "field 'scrollView'");
    view = finder.findRequiredView(source, 2131493178, "field 'status_bar'");
    target.status_bar = finder.castView(view, 2131493178, "field 'status_bar'");
    view = finder.findRequiredView(source, 2131493069, "field 'lnShortcutRecent'");
    target.lnShortcutRecent = finder.castView(view, 2131493069, "field 'lnShortcutRecent'");
    view = finder.findRequiredView(source, 2131493071, "field 'imgShowShortcuts'");
    target.imgShowShortcuts = finder.castView(view, 2131493071, "field 'imgShowShortcuts'");
    view = finder.findRequiredView(source, 2131493073, "field 'imgShowRecent'");
    target.imgShowRecent = finder.castView(view, 2131493073, "field 'imgShowRecent'");
  }

  @Override public void unbind(T target) {
    super.unbind(target);

    target.lvShortcuts = null;
    target.lvRecent = null;
    target.lnShortcut = null;
    target.lnRecent = null;
    target.imgAddShortcuts = null;
    target.imgSendCommand = null;
    target.mMessageField = null;
    target.threadAllMessages = null;
    target.scrollView = null;
    target.status_bar = null;
    target.lnShortcutRecent = null;
    target.imgShowShortcuts = null;
    target.imgShowRecent = null;
  }
}
