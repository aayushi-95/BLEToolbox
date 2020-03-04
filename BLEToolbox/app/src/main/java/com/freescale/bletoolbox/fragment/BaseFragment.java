/**
 * Copyright 2016 Freescale Semiconductors, Inc.
 */
package com.freescale.bletoolbox.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freescale.bletoolbox.event.BLEStateEvent;

import de.greenrobot.event.EventBus;

public class BaseFragment extends Fragment {
    protected static String BLANK = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //    EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    //    EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        // finish fragment when bluetooth disconnected
        if (getActivity() != null) {
            getActivity().getFragmentManager().popBackStackImmediate();
        }
    }

    public void onEventMainThread(BLEStateEvent.DataWritenFromClient e) {

    }
}
