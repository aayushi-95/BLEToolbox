package com.freescale.bletoolbox.utility;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.freescale.bletoolbox.R;
import com.freescale.bletoolbox.activity.ThreadShellActivity;
import com.freescale.bletoolbox.activity.ZigbeeActivity;
import com.freescale.bletoolbox.model.ItemRecent;
import com.freescale.bletoolbox.model.ItemShortcuts;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class DBUtil {

    public static void deleteAllRecent() {
        try {
            List<ItemRecent> recentList;
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<ItemRecent> query = realm.where(ItemRecent.class);
            recentList = query.findAll();
            if (recentList.size() > 0) {
                realm.beginTransaction();
                recentList.clear();
                realm.commitTransaction();
            }
        } catch (Exception ex) {

        }
    }

    public static void deleteAllShortCuts() {
        try {
            List<ItemShortcuts> shortcutsList;
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<ItemShortcuts> query = realm.where(ItemShortcuts.class);
            shortcutsList = query.findAll();
            if (shortcutsList.size() > 0) {
                realm.beginTransaction();
                shortcutsList.clear();
                realm.commitTransaction();
            }
        } catch (Exception ex) {

        }
    }

    public static void deleteAllShortCuts(Class cls)
    {
        try {
            List<ItemShortcuts> shortcutsList;
            Realm realm = Realm.getDefaultInstance();
            RealmQuery query = realm.where(cls);
            shortcutsList = query.findAll();
            if (shortcutsList.size() > 0) {
                realm.beginTransaction();
                shortcutsList.clear();
                realm.commitTransaction();
            }
        } catch (Exception ex) {
        }
    }

    public static RealmResults<ItemRecent> getAllItemRecent(ThreadShellActivity.RecentAdapter recentAdapter,RecyclerView lvRecent) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ItemRecent> recentsList = realm.where(ItemRecent.class).findAll();
        if (null != recentsList && recentsList.size() > 0) {
            recentsList.sort("strTime", RealmResults.SORT_ORDER_DESCENDING);
            ThreadShellActivity.setHeightListView(lvRecent, recentsList.size());
            recentAdapter.setData(recentsList);
        }
        return recentsList;
    }
    public static RealmResults<ItemRecent> getAllItemRecent(ZigbeeActivity.RecentAdapter recentAdapter,RecyclerView lvRecent) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ItemRecent> recentsList = realm.where(ItemRecent.class).findAll();
        if (null != recentsList && recentsList.size() > 0) {
            recentsList.sort("strTime", RealmResults.SORT_ORDER_DESCENDING);
            ThreadShellActivity.setHeightListView(lvRecent, recentsList.size());
            recentAdapter.setData(recentsList);
        }
        return recentsList;
    }
    public static RealmResults<ItemShortcuts> getAllItemShortcuts(ThreadShellActivity.ShortcutsAdapter shortcutsAdapter,RecyclerView lvShortcuts) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ItemShortcuts> shortcutsList = realm.where(ItemShortcuts.class).findAll();
        if (null != shortcutsList && shortcutsList.size() > 0) {
            shortcutsList.sort("strTime", RealmResults.SORT_ORDER_DESCENDING);
            ThreadShellActivity.setHeightListView(lvShortcuts, shortcutsList.size());
            shortcutsAdapter.setData(shortcutsList);
        }
        return shortcutsList;
    }
    public static RealmResults getAllItemShortcuts(ZigbeeActivity.ShortcutsAdapter shortcutsAdapter, RecyclerView lvShortcuts, Class cls) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults shortcutsList = realm.where(cls).findAll();
        if (null != shortcutsList && shortcutsList.size() > 0) {
            shortcutsList.sort("strTime", RealmResults.SORT_ORDER_DESCENDING);
            ZigbeeActivity.setHeightListView(lvShortcuts, shortcutsList.size());
            shortcutsAdapter.setData(shortcutsList);
        }
        return shortcutsList;
    }
    public static boolean delete_Get_ItemShortcuts_FromID(Activity activity, String key, String strdel) {
        try {
            RealmResults<ItemShortcuts> result;
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<ItemShortcuts> query = realm.where(ItemShortcuts.class);
            query.equalTo("strCommand", key);
            result = query.findAll();
            if (result.size() > 0) {
                if (strdel.equals(activity.getString(R.string.str_shell_del))) {
                    realm.beginTransaction();
                    result.clear();
                    realm.commitTransaction();
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean delete_Sensor_ItemShortcuts_FromID(Activity activity, Class cls, String key, String strdel) {
        try {
            RealmResults<ItemShortcuts> result;
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<ItemShortcuts> query = realm.where(cls);
            query.equalTo("strCommand", key);
            result = query.findAll();
            if (result.size() > 0) {
                if (strdel.equals(activity.getString(R.string.str_shell_del))) {
                    realm.beginTransaction();
                    result.clear();
                    realm.commitTransaction();
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean delete_ItemRecent_FromID(String key,String time) {
        try {
            RealmResults<ItemRecent> result;
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<ItemRecent> query = realm.where(ItemRecent.class);
            query.equalTo("strCommand", key);
            query.equalTo("strTime",time);
            result = query.findAll();
            if (result.size() > 0) {
                realm.beginTransaction();
                result.clear();
                realm.commitTransaction();
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public static void addRecent(String command, String strValue, boolean isChange_List_Recent) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ItemRecent itemRecent = realm.createObject(ItemRecent.class);
        itemRecent.setStrCommand(command);
        itemRecent.setStrTime(strValue);
        realm.commitTransaction();
        isChange_List_Recent = true;
    }
}
