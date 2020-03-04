package com.freescale.bletoolbox.fragment.Sersor_Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.activity.SenorActivity;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.fragment.BaseFragment;
import com.freescale.bletoolbox.fragment.IActivityToFragment;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nxf42542 on 2018/7/11.
 */

public class SensorDataFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener, IActivityToFragment {

//    @Bind(R.id.sensor_data_textView)
//    TextView dataView;
//
//    @Bind(R.id.sersorScrollView)
//    ScrollView scrollView;

    @Bind(R.id.data_list)
    ListView datacontener;
    private ArrayAdapter adapter;
    private List<CharSequence> data = new ArrayList<>();

    private byte[] buffering;
    private String contentString;
    private boolean acceptValue = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sensor_data, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        adapter = new ArrayAdapter<CharSequence>(getActivity(),R.layout.sensor_data_cell,data);
        datacontener.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        menu.clear();
        inflater.inflate(R.menu.menu_sensor,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        String title = item.getTitle().toString();
        switch (item.getItemId()){
            case R.id.menu_data_type:
                SenorActivity senorActivity = (SenorActivity) getActivity();
                if (title.equals("NORMAL")){
                    item.setTitle("RAW");
                    senorActivity.dataModeDataType(true);
                }else{
                    item.setTitle("NORMAL");
                    senorActivity.dataModeDataType(false);
                }
                return true;
            case R.id.menu_s_stop:
                if (title.equals("START")){
                    item.setTitle("STOP");
                    acceptValue = true;
                }else{
                    item.setTitle("START");
                    acceptValue = false;
                }
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void upDateUI(BLEStateEvent.DataAvailable e) {}


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void resetDefault() {
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onEventMainThread(BLEStateEvent.DataWritenFromClient e) {
        if (!acceptValue)   return;
        buffering = e.value;
        receiveMessage();
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        return false;
    }


    private void receiveMessage() {
        // the new UI on May 30 2016
        if (data.size() > 7443)
            data.clear();
        SpannableStringBuilder sb = new SpannableStringBuilder();
        String value = new String(buffering, Charset.forName("ASCII"));
        if (!value.contains("\n")) {
            value = value + "\n";
        }
        sb.append(value);
        sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.Deep_Red_Color)), 0, sb.length(), 0);
        data.add(sb);

        adapter.notifyDataSetChanged();
        datacontener.smoothScrollToPosition(data.size());
    }
}