/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.fragment;

import com.freescale.bletoolbox.event.BLEStateEvent;

public interface IActivityToFragment {
    void upDateUI(BLEStateEvent.DataAvailable e);
    void resetDefault();
}
