package com.freescale.bletoolbox.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.event.BLEStateEvent;
import com.freescale.bletoolbox.model.BLEAttributes;
import com.freescale.bletoolbox.model.ItemRecent;
import com.freescale.bletoolbox.model.ItemShortcuts;
import com.freescale.bletoolbox.service.BLEService;
import com.freescale.bletoolbox.utility.Constants;
import com.freescale.bletoolbox.utility.DBUtil;
import com.freescale.bletoolbox.utility.SharedPreferencesUtil;
import com.freescale.bletoolbox.view.ShortcutDialog;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class ThreadShellActivity extends BaseServiceActivity implements View.OnClickListener {

    @Bind(R.id.lvShortcuts)
    RecyclerView lvShortcuts;
    @Bind(R.id.lvRecent)
    RecyclerView lvRecent;
    @Bind(R.id.lnShortcut)
    LinearLayout lnShortcut;
    @Bind(R.id.lnRecent)
    LinearLayout lnRecent;
    @Bind(R.id.imgAddShortcuts)
    ImageView imgAddShortcuts;
    @Bind(R.id.imgSendCommand)
    ImageView imgSendCommand;
    @Bind(R.id.thread_field)
    EditText mMessageField;
    @Bind(R.id.thread_all_messages)
    TextView threadAllMessages;
    @Bind(R.id.sv_thread_shell)
    ScrollView scrollView;
    @Bind(R.id.status_bar)
    LinearLayout status_bar;
    @Bind(R.id.lnShortcutRecent)
    LinearLayout lnShortcutRecent;
    @Bind(R.id.imgShowShortcuts)
    ImageView imgShowShortcuts;
    @Bind(R.id.imgShowRecent)
    ImageView imgShowRecent;

    private byte[] buffering;
    private boolean isChange_List_Shortcuts = false;
    private boolean isChange_List_Recent = false;
    private String recent_Time;
    private String recent_Command;
    private RecentAdapter recentAdapter;
    private ShortcutsAdapter shortcutsAdapter;
    private RealmResults<ItemShortcuts> shortcutsList;
    private RealmResults<ItemRecent> recentsList;
    private String mDeviceName;
    private boolean isShowListView;
    private boolean isForcusAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_shell);
        ButterKnife.bind(this);
        initView();
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(this);

        if (sharedPreferencesUtil.getBoolean(Constants.STARTAPP, false)) {
            DBUtil.deleteAllRecent();
            sharedPreferencesUtil.saveBoolean(Constants.STARTAPP, false);
        }
        if (sharedPreferencesUtil.getBoolean(Constants.THE_FIRST_RUN, false)) {
            DBUtil.deleteAllShortCuts();
            createListDefaulShortCuts();
            sharedPreferencesUtil.saveBoolean(Constants.THE_FIRST_RUN, false);
        }
    }

    class ItemShortCutsDefault {
        private String strCommand;

        ItemShortCutsDefault(String _strCommand) {
            strCommand = _strCommand;
        }

        public String getStrCommand() {
            return strCommand;
        }
    }

    private void createListDefaulShortCuts() {
        List<ItemShortCutsDefault> listItemShortCutsDefault = new ArrayList<>();
        listItemShortCutsDefault.add(new ItemShortCutsDefault("thr set masterkey"));
        listItemShortCutsDefault.add(new ItemShortCutsDefault("thr get masterkey"));
        listItemShortCutsDefault.add(new ItemShortCutsDefault("thr join"));
        listItemShortCutsDefault.add(new ItemShortCutsDefault("thr create"));
        listItemShortCutsDefault.add(new ItemShortCutsDefault("break"));
        listItemShortCutsDefault.add(new ItemShortCutsDefault("version"));
        listItemShortCutsDefault.add(new ItemShortCutsDefault("help"));
        listItemShortCutsDefault.add(new ItemShortCutsDefault("identify"));
        listItemShortCutsDefault.add(new ItemShortCutsDefault("ifconfig"));

        Realm realm = Realm.getDefaultInstance();
        for (ItemShortCutsDefault item : listItemShortCutsDefault) {
            realm.beginTransaction();
            ItemShortcuts itemShortcuts = realm.createObject(ItemShortcuts.class);
            itemShortcuts.setStrCommand(item.getStrCommand());
            itemShortcuts.setStrValue("No");
            itemShortcuts.setStrTime(Long.toString(System.currentTimeMillis()));
            realm.commitTransaction();
        }
        isChange_List_Shortcuts = true;
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(R.string.thread_shell);
        mMessageField.setEnabled(false);
        mMessageField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    recent_Command = mMessageField.getText().toString();
                    sendMessage(mMessageField.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        mMessageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShowListView = false;
                defaultView();
                runHandler(150);
            }
        });
        mMessageField.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                defaultView();
                return false;
            }
        });

        mMessageField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (isForcusAuto) {
                    if (!isShowListView) {
                        defaultView();
                        showKeyboard(mMessageField);
                    }
                } else {
                    defaultView();
                }
                isForcusAuto = false;
            }
        });

        imgAddShortcuts.setOnClickListener(this);
        imgSendCommand.setOnClickListener(this);
        status_bar.setVisibility(View.GONE);
        lnShortcut.setOnClickListener(this);
        lnRecent.setOnClickListener(this);
        lvShortcuts.setVisibility(View.GONE);
        lvRecent.setVisibility(View.GONE);
        isChange_List_Shortcuts = true;
        isChange_List_Recent = true;

        // create ShortcutsAdapter
        LinearLayoutManager layoutManagerPlan = new LinearLayoutManager(this);
        layoutManagerPlan.setOrientation(LinearLayoutManager.VERTICAL);
        shortcutsAdapter = new ShortcutsAdapter(ThreadShellActivity.this);
        lvShortcuts.setHasFixedSize(true);
        lvShortcuts.setAdapter(shortcutsAdapter);
        lvShortcuts.setLayoutManager(layoutManagerPlan);
        lvShortcuts.addItemDecoration(new CustomHeightItemDecoration(this, 1));

        // create RecentAdapter
        LinearLayoutManager layoutManagerPlanRecent = new LinearLayoutManager(this);
        layoutManagerPlanRecent.setOrientation(LinearLayoutManager.VERTICAL);
        recentAdapter = new RecentAdapter(ThreadShellActivity.this);
        lvRecent.setHasFixedSize(true);
        lvRecent.setAdapter(recentAdapter);
        lvRecent.setLayoutManager(layoutManagerPlanRecent);
        lvRecent.addItemDecoration(new CustomHeightItemDecoration(this, 1));
        lnShortcutRecent.setVisibility(View.GONE);

        mDeviceName = getIntent().getStringExtra(INTENT_KEY_NAME);
        if (null != mInfoButton) {
            mInfoButton.setVisibility(View.GONE);
        }
    }

    public void runHandler(final int value) {
        Thread timeScrollView = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(value);
                        scrollViewToBottom();
                    }
                } catch (InterruptedException ex) {
                }
            }
        });
        timeScrollView.start();
    }

    private void defaultView() {
        lvRecent.setVisibility(View.GONE);
        lvShortcuts.setVisibility(View.GONE);
        imgShowRecent.setImageResource(R.drawable.nomal_list);
        imgShowShortcuts.setImageResource(R.drawable.nomal_list);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lnShortcut:
                showData(getString(R.string.str_shell_shortcuts).toString());
                break;
            case R.id.lnRecent:
                showData(getString(R.string.str_shell_recent).toString());
                break;
            case R.id.imgAddShortcuts:
                defaultView();
                ShortcutDialog.newInstance(ThreadShellActivity.this, mMessageField.getText().toString(), getString(R.string.title_add_shortcut_in_dialog), getString(R.string.str_shell_add), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkValidate(ShortcutDialog.getTextShortcuts())) {
                            mMessageField.setText(ShortcutDialog.getTextShortcuts());
                            mMessageField.setSelection(ShortcutDialog.getTextShortcuts().length());
                            ShortcutDialog.getCommontDialog().dismiss();
                            addShortcuts();
                        }
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShortcutDialog.getCommontDialog().dismiss();
                    }
                }).show();
                break;
            case R.id.imgSendCommand:
                recent_Command = mMessageField.getText().toString();
                sendMessage(mMessageField.getText().toString().trim());
                break;
        }
    }

    private boolean checkValidate(String str) {
        if (null != str && !str.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private void addShortcuts() {
        String str = mMessageField.getText().toString();
        if (!DBUtil.delete_Get_ItemShortcuts_FromID(ThreadShellActivity.this, str, getString(R.string.str_shell_get))) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            ItemShortcuts itemShortcuts = realm.createObject(ItemShortcuts.class);
            itemShortcuts.setStrCommand(str);
            itemShortcuts.setStrValue(str);
            itemShortcuts.setStrTime(Long.toString(System.currentTimeMillis()));
            realm.commitTransaction();
            isChange_List_Shortcuts = true;
        } else {
            new MaterialDialog.Builder(this).title(R.string.error_title)
                    .titleColor(Color.RED)
                    .content(R.string.error_command_exists)
                    .positiveText(android.R.string.ok).show();
        }
    }

    public static void setHeightListView(RecyclerView listView, int countRow) {
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (countRow < 5) {
            params.height = 130 * countRow;
        } else {
            params.height = 130 * 5;
        }
        listView.setLayoutParams(params);
    }

    static class CustomHeightItemDecoration extends RecyclerView.ItemDecoration {
        private final int mHeight;
        private final Drawable mDivider;

        public CustomHeightItemDecoration(@NonNull Context context, int mVerticalSpaceHeight) {
            this.mHeight = mVerticalSpaceHeight;
            this.mDivider = ContextCompat.getDrawable(context, R.drawable.list_divider_thin);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = mHeight;
            }
        }

        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    private void showData(String value) {
        hideKeyboard();
        isShowListView = true;
        if (value.contains(getString(R.string.str_shell_shortcuts))) {
            lvRecent.setVisibility(View.GONE);
            imgShowRecent.setImageResource(R.drawable.nomal_list);
            if (lvShortcuts.getVisibility() == View.VISIBLE) {
                lvShortcuts.setVisibility(View.GONE);
                isChange_List_Shortcuts = false;
                imgShowShortcuts.setImageResource(R.drawable.nomal_list);
            } else {
                lvShortcuts.setVisibility(View.VISIBLE);
                imgShowShortcuts.setImageResource(R.drawable.down_list);
                if (isChange_List_Shortcuts) {
                    shortcutsList = DBUtil.getAllItemShortcuts(shortcutsAdapter, lvShortcuts);
                }
            }
        } else {
            lvShortcuts.setVisibility(View.GONE);
            imgShowShortcuts.setImageResource(R.drawable.nomal_list);
            if (lvRecent.getVisibility() == View.VISIBLE) {
                lvRecent.setVisibility(View.GONE);
                isChange_List_Recent = false;
                imgShowRecent.setImageResource(R.drawable.nomal_list);
            } else {
                lvRecent.setVisibility(View.VISIBLE);
                imgShowRecent.setImageResource(R.drawable.down_list);
                if (isChange_List_Recent) {
                    recentsList = DBUtil.getAllItemRecent(recentAdapter, lvRecent);
                }
            }
        }
        scrollViewToBottom();
    }

    public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewPlanHolder> {
        private List<ItemRecent> listItemRecent;
        private Activity activity;
        private ViewGroup.LayoutParams layoutparams;

        public RecentAdapter(Activity mActivity) {
            activity = mActivity;
        }

        public void setData(List<ItemRecent> _listItemHistory) {
            listItemRecent = _listItemHistory;
            notifyDataSetChanged();
        }

        public class ViewPlanHolder extends RecyclerView.ViewHolder {
            public TextView txtName;
            public ImageView imgDel;

            public ViewPlanHolder(View view) {
                super(view);
                txtName = (TextView) view.findViewById(R.id.tvName);
                imgDel = (ImageView) view.findViewById(R.id.imgDel);
            }
        }

        @Override
        public int getItemCount() {
            if (null != listItemRecent) {
                return listItemRecent.size();
            } else {
                return 0;
            }
        }

        @Override
        public ViewPlanHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = activity.getLayoutInflater().inflate(R.layout.item_history, viewGroup, false);
            layoutparams = view.getLayoutParams();
            layoutparams.height = 130;
            view.setLayoutParams(layoutparams);
            return new ViewPlanHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewPlanHolder viewPlanHolder, int i) {
            ItemRecent item = listItemRecent.get(i);
            if (null != item && null != item.getStrCommand()) {
                final String strCommand = item.getStrCommand();
                final String strTime = item.getStrTime();
                viewPlanHolder.txtName.setText(item.getStrCommand());
                viewPlanHolder.txtName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMessageField.setText("");
                        mMessageField.setText(strCommand);
                        mMessageField.setSelection(mMessageField.getText().length());
                    }
                });
                viewPlanHolder.imgDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShortcutDialog.newInstance(ThreadShellActivity.this, strCommand.toString(), getString(R.string.title_del_shortcut_in_dialog), getString(R.string.str_shell_del), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (DBUtil.delete_ItemRecent_FromID(strCommand.toString(),strTime)) {
                                    isChange_List_Recent = true;
                                    if (null != recentsList) {
                                        setHeightListView(lvRecent, recentsList.size());
                                        notifyDataSetChanged();
                                    }
                                }
                                ShortcutDialog.getCommontDialog().dismiss();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ShortcutDialog.getCommontDialog().dismiss();
                            }
                        }).show();
                    }
                });
            }
        }
    }

    public class ShortcutsAdapter extends RecyclerView.Adapter<ShortcutsAdapter.ViewPlanHolder> {
        private List<ItemShortcuts> listItemShortcuts;
        private ViewGroup.LayoutParams layoutparams;
        private Activity activity;

        public ShortcutsAdapter(Activity mActivity) {
            activity = mActivity;
        }

        public void setData(List<ItemShortcuts> _listItemShortcuts) {
            listItemShortcuts = _listItemShortcuts;
            notifyDataSetChanged();
        }

        public class ViewPlanHolder extends RecyclerView.ViewHolder {
            public TextView txtName;
            public ImageView imgDelShortcut;

            public ViewPlanHolder(View view) {
                super(view);
                txtName = (TextView) view.findViewById(R.id.tvName);
                imgDelShortcut = (ImageView) view.findViewById(R.id.imgDel);
            }
        }

        @Override
        public int getItemCount() {
            if (null != listItemShortcuts) {
                return listItemShortcuts.size();
            } else {
                return 0;
            }
        }

        @Override
        public ViewPlanHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = activity.getLayoutInflater().inflate(R.layout.item_history, viewGroup, false);
            layoutparams = view.getLayoutParams();
            layoutparams.height = 130;
            view.setLayoutParams(layoutparams);
            return new ViewPlanHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewPlanHolder viewPlanHolder, int i) {
            ItemShortcuts item = listItemShortcuts.get(i);
            if (null != item && null != item.getStrCommand()) {
                final StringBuilder strCommand = new StringBuilder();
                strCommand.append(item.getStrCommand());
                viewPlanHolder.txtName.setText(item.getStrCommand());
                viewPlanHolder.txtName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (strCommand.toString().trim().equals(getString(R.string.str_shell_ping))) {
                            showKeyboard(mMessageField);
                            lvShortcuts.setVisibility(View.GONE);
                            strCommand.append(" ");
                            lvShortcuts.setVisibility(View.GONE);
                            isChange_List_Shortcuts = false;
                            imgShowShortcuts.setImageResource(R.drawable.nomal_list);
                        } else {
                            hideKeyboard();
                        }
                        mMessageField.setText(strCommand);
                        mMessageField.setSelection(mMessageField.getText().length());
                    }
                });
                if (item.getStrValue().equals("No")) {
                    viewPlanHolder.imgDelShortcut.setVisibility(View.GONE);
                } else {
                    viewPlanHolder.imgDelShortcut.setVisibility(View.VISIBLE);
                    viewPlanHolder.imgDelShortcut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ShortcutDialog.newInstance(ThreadShellActivity.this, strCommand.toString(), getString(R.string.title_del_shortcut_in_dialog), getString(R.string.str_shell_del), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (DBUtil.delete_Get_ItemShortcuts_FromID(ThreadShellActivity.this, strCommand.toString(), getString(R.string.str_shell_del))) {
                                        isChange_List_Shortcuts = true;
                                        if (null != shortcutsList) {
                                            setHeightListView(lvShortcuts, shortcutsList.size());
                                            notifyDataSetChanged();
                                        }
                                    }
                                    ShortcutDialog.getCommontDialog().dismiss();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ShortcutDialog.getCommontDialog().dismiss();
                                }
                            }).show();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onEventMainThread(BLEStateEvent.Connecting e) {
        super.onEventMainThread(e);
        if (null != threadAllMessages) {
            StringBuilder strConnecting = new StringBuilder();
            strConnecting.append(getString(R.string.state_connecting));
            strConnecting.append(Constants.DOTS);
            strConnecting.append("\n");
            if (!threadAllMessages.getText().toString().endsWith(strConnecting.toString())) {
                SpannableStringBuilder strStatusConnect = new SpannableStringBuilder();
                strStatusConnect.append(threadAllMessages.getText());
                strStatusConnect.append(mDeviceName);
                strStatusConnect.append("\n");
                strStatusConnect.append(strConnecting);
                threadAllMessages.setText(strStatusConnect);
            }
            scrollViewToBottom();
        }
        if (null != mInfoButton) {
            mInfoButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEventMainThread(BLEStateEvent.Connected e) {
        super.onEventMainThread(e);
        if (null != threadAllMessages) {
            SpannableStringBuilder strStatusConnect = new SpannableStringBuilder();
            strStatusConnect.append(threadAllMessages.getText());
            strStatusConnect.append(getString(R.string.state_connected));
            strStatusConnect.append("\n");
            threadAllMessages.setText(strStatusConnect);
            scrollViewToBottom();
        }
        imgAddShortcuts.setEnabled(true);
        imgSendCommand.setEnabled(true);
        if (null != mInfoButton) {
            mInfoButton.setVisibility(View.GONE);
        }
    }

    private void scrollViewToBottom() {
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.scrollTo(threadAllMessages.getMeasuredWidth(), threadAllMessages.getMeasuredHeight());
            }
        });
    }

    @Override
    public void onEventMainThread(BLEStateEvent.Disconnected e) {
        super.onEventMainThread(e);
        mMessageField.setEnabled(false);
        mMessageField.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideKeyboard();
            }
        }, 400);
        hideKeyboard();
        imgAddShortcuts.setEnabled(false);
        imgSendCommand.setEnabled(false);
        if (null != threadAllMessages) {
            StringBuilder strDisconnected = new StringBuilder();
            strDisconnected.append(getString(R.string.state_disconnected));
            strDisconnected.append("\n");
            if (!threadAllMessages.getText().toString().endsWith(strDisconnected.toString())) {
                SpannableStringBuilder strStatusConnect = new SpannableStringBuilder();
                strStatusConnect.append(threadAllMessages.getText());
                strStatusConnect.append(strDisconnected);
                threadAllMessages.setText(strStatusConnect);
            }
        }
        lnShortcutRecent.setVisibility(View.GONE);
        if (null != mInfoButton) {
            mInfoButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEvent(BLEStateEvent.ServiceDiscovered e) {
        super.onEvent(e);
    }

    public void onEventMainThread(BLEStateEvent.ServiceDiscovered e) {
        if (BLEService.INSTANCE.getService(BLEAttributes.DEVICE_INFORMATION_SERVICE) != null) {
            mMessageField.setEnabled(true);
            mMessageField.requestFocus();
            mMessageField.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showKeyboard(mMessageField);
                }
            }, 400);
            if (null != lvRecent) {
                lvRecent.setVisibility(View.GONE);
            }
            if (null != lvShortcuts) {
                lvShortcuts.setVisibility(View.GONE);
            }
            lnShortcutRecent.setVisibility(View.VISIBLE);
            threadAllMessages.setEnabled(true);
            defaultView();
            runHandler(700);
        }
        if (null != mInfoButton) {
            mInfoButton.setVisibility(View.GONE);
        }
    }

    public void onEventMainThread(BLEStateEvent.DataWritenFromClient e) {
        if (buffering == null) {
            buffering = e.value;
        } else {
            byte[] destination = new byte[buffering.length + e.value.length];
            System.arraycopy(buffering, 0, destination, 0, buffering.length);
            System.arraycopy(e.value, 0, destination, buffering.length, e.value.length);
            buffering = destination;
        }
        receiveMessage();
    }

    @Override
    public void onEventMainThread(BLEStateEvent.DataAvailable e) {
        super.onEventMainThread(e);
    }

    @Override
    protected boolean needUartSupport() {
        return true;
    }

    private void sendMessage(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        recent_Time = Long.toString(System.currentTimeMillis());
        // the new UI on May 30 2016
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(threadAllMessages.getText());
        int start = sb.length();
        sb.append("\n").append(content).append("\n");
        // change text color
        sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.Philips_Blue_Color)), start, sb.length(), 0);
        isForcusAuto = true;
        threadAllMessages.setText(sb);

        // send data
        byte[] c = content.getBytes();
        byte[] data = new byte[c.length + 2];
        System.arraycopy(c, 0, data, 0, c.length);
        data[c.length] = 0x0D;  // \r
        data[c.length + 1] = 0x0A;  // \n

        int chunkCount = data.length / 20 + 1;
        for (int i = 0; i < chunkCount; ++i) {
            byte[] chunk = Arrays.copyOfRange(data, i * 20, Math.min(data.length, i * 20 + 20));
            BLEService.INSTANCE.requestWrite(BLEAttributes.SHELL_UUID, BLEAttributes.SHELL_CHARACTERISTIC, chunk);
        }
        isChange_List_Recent = true;
        DBUtil.addRecent(recent_Command, recent_Time, isChange_List_Recent);

        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        isForcusAuto = true;
        mMessageField.setText("");
        mMessageField.requestFocus();
        refreshRecentList();
    }

    private void refreshRecentList() {
        if (lvRecent.getVisibility() == View.VISIBLE) {
            recentsList = DBUtil.getAllItemRecent(recentAdapter, lvRecent);
        }
    }

    private void receiveMessage() {
        // the new UI on May 30 2016
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(threadAllMessages.getText());
        int start = sb.length();
        String value = new String(buffering, Charset.forName("ASCII"));
        if (!value.contains("\n")) {
            value = value + "\n";
        }
        sb.append(value);

        // change text color
        sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.Deep_Red_Color)), start, sb.length(), 0);
        threadAllMessages.setText(sb);
        buffering = null;
        scrollViewToBottom();
        mMessageField.requestFocus();
    }
}
