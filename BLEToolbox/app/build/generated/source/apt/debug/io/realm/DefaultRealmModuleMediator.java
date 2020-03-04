package io.realm;


import android.util.JsonReader;
import io.realm.exceptions.RealmException;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.RealmProxyMediator;
import io.realm.internal.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.freescale.bletoolbox.model.BeaconMessage;
import com.freescale.bletoolbox.model.ItemRecent;
import com.freescale.bletoolbox.model.ItemShortcuts;
import com.freescale.bletoolbox.model.ZB_CoordinaterItemShortcuts;
import com.freescale.bletoolbox.model.ZB_EndDeviceItemShortcuts;
import com.freescale.bletoolbox.model.ZB_RouterItemShortcuts;

@io.realm.annotations.RealmModule
class DefaultRealmModuleMediator extends RealmProxyMediator {

    private static final List<Class<? extends RealmObject>> MODEL_CLASSES;
    static {
        List<Class<? extends RealmObject>> modelClasses = new ArrayList<Class<? extends RealmObject>>();
        modelClasses.add(ItemRecent.class);
        modelClasses.add(ItemShortcuts.class);
        modelClasses.add(ZB_CoordinaterItemShortcuts.class);
        modelClasses.add(BeaconMessage.class);
        modelClasses.add(ZB_RouterItemShortcuts.class);
        modelClasses.add(ZB_EndDeviceItemShortcuts.class);
        MODEL_CLASSES = Collections.unmodifiableList(modelClasses);
    }

    @Override
    public Table createTable(Class<? extends RealmObject> clazz, ImplicitTransaction transaction) {
        checkClass(clazz);

        if (clazz.equals(ItemRecent.class)) {
            return ItemRecentRealmProxy.initTable(transaction);
        } else if (clazz.equals(ItemShortcuts.class)) {
            return ItemShortcutsRealmProxy.initTable(transaction);
        } else if (clazz.equals(ZB_CoordinaterItemShortcuts.class)) {
            return ZB_CoordinaterItemShortcutsRealmProxy.initTable(transaction);
        } else if (clazz.equals(BeaconMessage.class)) {
            return BeaconMessageRealmProxy.initTable(transaction);
        } else if (clazz.equals(ZB_RouterItemShortcuts.class)) {
            return ZB_RouterItemShortcutsRealmProxy.initTable(transaction);
        } else if (clazz.equals(ZB_EndDeviceItemShortcuts.class)) {
            return ZB_EndDeviceItemShortcutsRealmProxy.initTable(transaction);
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public void validateTable(Class<? extends RealmObject> clazz, ImplicitTransaction transaction) {
        checkClass(clazz);

        if (clazz.equals(ItemRecent.class)) {
            ItemRecentRealmProxy.validateTable(transaction);
        } else if (clazz.equals(ItemShortcuts.class)) {
            ItemShortcutsRealmProxy.validateTable(transaction);
        } else if (clazz.equals(ZB_CoordinaterItemShortcuts.class)) {
            ZB_CoordinaterItemShortcutsRealmProxy.validateTable(transaction);
        } else if (clazz.equals(BeaconMessage.class)) {
            BeaconMessageRealmProxy.validateTable(transaction);
        } else if (clazz.equals(ZB_RouterItemShortcuts.class)) {
            ZB_RouterItemShortcutsRealmProxy.validateTable(transaction);
        } else if (clazz.equals(ZB_EndDeviceItemShortcuts.class)) {
            ZB_EndDeviceItemShortcutsRealmProxy.validateTable(transaction);
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public List<String> getFieldNames(Class<? extends RealmObject> clazz) {
        checkClass(clazz);

        if (clazz.equals(ItemRecent.class)) {
            return ItemRecentRealmProxy.getFieldNames();
        } else if (clazz.equals(ItemShortcuts.class)) {
            return ItemShortcutsRealmProxy.getFieldNames();
        } else if (clazz.equals(ZB_CoordinaterItemShortcuts.class)) {
            return ZB_CoordinaterItemShortcutsRealmProxy.getFieldNames();
        } else if (clazz.equals(BeaconMessage.class)) {
            return BeaconMessageRealmProxy.getFieldNames();
        } else if (clazz.equals(ZB_RouterItemShortcuts.class)) {
            return ZB_RouterItemShortcutsRealmProxy.getFieldNames();
        } else if (clazz.equals(ZB_EndDeviceItemShortcuts.class)) {
            return ZB_EndDeviceItemShortcutsRealmProxy.getFieldNames();
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public String getTableName(Class<? extends RealmObject> clazz) {
        checkClass(clazz);

        if (clazz.equals(ItemRecent.class)) {
            return ItemRecentRealmProxy.getTableName();
        } else if (clazz.equals(ItemShortcuts.class)) {
            return ItemShortcutsRealmProxy.getTableName();
        } else if (clazz.equals(ZB_CoordinaterItemShortcuts.class)) {
            return ZB_CoordinaterItemShortcutsRealmProxy.getTableName();
        } else if (clazz.equals(BeaconMessage.class)) {
            return BeaconMessageRealmProxy.getTableName();
        } else if (clazz.equals(ZB_RouterItemShortcuts.class)) {
            return ZB_RouterItemShortcutsRealmProxy.getTableName();
        } else if (clazz.equals(ZB_EndDeviceItemShortcuts.class)) {
            return ZB_EndDeviceItemShortcutsRealmProxy.getTableName();
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public <E extends RealmObject> E newInstance(Class<E> clazz) {
        checkClass(clazz);

        if (clazz.equals(ItemRecent.class)) {
            return clazz.cast(new ItemRecentRealmProxy());
        } else if (clazz.equals(ItemShortcuts.class)) {
            return clazz.cast(new ItemShortcutsRealmProxy());
        } else if (clazz.equals(ZB_CoordinaterItemShortcuts.class)) {
            return clazz.cast(new ZB_CoordinaterItemShortcutsRealmProxy());
        } else if (clazz.equals(BeaconMessage.class)) {
            return clazz.cast(new BeaconMessageRealmProxy());
        } else if (clazz.equals(ZB_RouterItemShortcuts.class)) {
            return clazz.cast(new ZB_RouterItemShortcutsRealmProxy());
        } else if (clazz.equals(ZB_EndDeviceItemShortcuts.class)) {
            return clazz.cast(new ZB_EndDeviceItemShortcutsRealmProxy());
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public List<Class<? extends RealmObject>> getModelClasses() {
        return MODEL_CLASSES;
    }

    @Override
    public Map<String, Long> getColumnIndices(Class<? extends RealmObject> clazz) {
        checkClass(clazz);

        if (clazz.equals(ItemRecent.class)) {
            return ItemRecentRealmProxy.getColumnIndices();
        } else if (clazz.equals(ItemShortcuts.class)) {
            return ItemShortcutsRealmProxy.getColumnIndices();
        } else if (clazz.equals(ZB_CoordinaterItemShortcuts.class)) {
            return ZB_CoordinaterItemShortcutsRealmProxy.getColumnIndices();
        } else if (clazz.equals(BeaconMessage.class)) {
            return BeaconMessageRealmProxy.getColumnIndices();
        } else if (clazz.equals(ZB_RouterItemShortcuts.class)) {
            return ZB_RouterItemShortcutsRealmProxy.getColumnIndices();
        } else if (clazz.equals(ZB_EndDeviceItemShortcuts.class)) {
            return ZB_EndDeviceItemShortcutsRealmProxy.getColumnIndices();
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public <E extends RealmObject> E copyOrUpdate(Realm realm, E obj, boolean update, Map<RealmObject, RealmObjectProxy> cache) {
        // This cast is correct because obj is either 
        // generated by RealmProxy or the original type extending directly from RealmObject
        @SuppressWarnings("unchecked") Class<E> clazz = (Class<E>) ((obj instanceof RealmObjectProxy) ? obj.getClass().getSuperclass() : obj.getClass());

        if (clazz.equals(ItemRecent.class)) {
            return clazz.cast(ItemRecentRealmProxy.copyOrUpdate(realm, (ItemRecent) obj, update, cache));
        } else if (clazz.equals(ItemShortcuts.class)) {
            return clazz.cast(ItemShortcutsRealmProxy.copyOrUpdate(realm, (ItemShortcuts) obj, update, cache));
        } else if (clazz.equals(ZB_CoordinaterItemShortcuts.class)) {
            return clazz.cast(ZB_CoordinaterItemShortcutsRealmProxy.copyOrUpdate(realm, (ZB_CoordinaterItemShortcuts) obj, update, cache));
        } else if (clazz.equals(BeaconMessage.class)) {
            return clazz.cast(BeaconMessageRealmProxy.copyOrUpdate(realm, (BeaconMessage) obj, update, cache));
        } else if (clazz.equals(ZB_RouterItemShortcuts.class)) {
            return clazz.cast(ZB_RouterItemShortcutsRealmProxy.copyOrUpdate(realm, (ZB_RouterItemShortcuts) obj, update, cache));
        } else if (clazz.equals(ZB_EndDeviceItemShortcuts.class)) {
            return clazz.cast(ZB_EndDeviceItemShortcutsRealmProxy.copyOrUpdate(realm, (ZB_EndDeviceItemShortcuts) obj, update, cache));
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public <E extends RealmObject> E createOrUpdateUsingJsonObject(Class<E> clazz, Realm realm, JSONObject json, boolean update)
        throws JSONException {
        checkClass(clazz);

        if (clazz.equals(ItemRecent.class)) {
            return clazz.cast(ItemRecentRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        } else if (clazz.equals(ItemShortcuts.class)) {
            return clazz.cast(ItemShortcutsRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        } else if (clazz.equals(ZB_CoordinaterItemShortcuts.class)) {
            return clazz.cast(ZB_CoordinaterItemShortcutsRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        } else if (clazz.equals(BeaconMessage.class)) {
            return clazz.cast(BeaconMessageRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        } else if (clazz.equals(ZB_RouterItemShortcuts.class)) {
            return clazz.cast(ZB_RouterItemShortcutsRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        } else if (clazz.equals(ZB_EndDeviceItemShortcuts.class)) {
            return clazz.cast(ZB_EndDeviceItemShortcutsRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

    @Override
    public <E extends RealmObject> E createUsingJsonStream(Class<E> clazz, Realm realm, JsonReader reader)
        throws IOException {
        checkClass(clazz);

        if (clazz.equals(ItemRecent.class)) {
            return clazz.cast(ItemRecentRealmProxy.createUsingJsonStream(realm, reader));
        } else if (clazz.equals(ItemShortcuts.class)) {
            return clazz.cast(ItemShortcutsRealmProxy.createUsingJsonStream(realm, reader));
        } else if (clazz.equals(ZB_CoordinaterItemShortcuts.class)) {
            return clazz.cast(ZB_CoordinaterItemShortcutsRealmProxy.createUsingJsonStream(realm, reader));
        } else if (clazz.equals(BeaconMessage.class)) {
            return clazz.cast(BeaconMessageRealmProxy.createUsingJsonStream(realm, reader));
        } else if (clazz.equals(ZB_RouterItemShortcuts.class)) {
            return clazz.cast(ZB_RouterItemShortcutsRealmProxy.createUsingJsonStream(realm, reader));
        } else if (clazz.equals(ZB_EndDeviceItemShortcuts.class)) {
            return clazz.cast(ZB_EndDeviceItemShortcutsRealmProxy.createUsingJsonStream(realm, reader));
        } else {
            throw getMissingProxyClassException(clazz);
        }
    }

}
