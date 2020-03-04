package io.realm;


import android.util.JsonReader;
import android.util.JsonToken;
import com.freescale.bletoolbox.model.BeaconMessage;
import io.realm.RealmObject;
import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.ColumnType;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.LinkView;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Table;
import io.realm.internal.TableOrView;
import io.realm.internal.android.JsonUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BeaconMessageRealmProxy extends BeaconMessage
    implements RealmObjectProxy {

    private static long INDEX_UUID;
    private static long INDEX_DATAA;
    private static long INDEX_DATAB;
    private static long INDEX_DATAC;
    private static long INDEX_ACTIONTYPE;
    private static long INDEX_MESSAGE;
    private static Map<String, Long> columnIndices;
    private static final List<String> FIELD_NAMES;
    static {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("uuid");
        fieldNames.add("dataA");
        fieldNames.add("dataB");
        fieldNames.add("dataC");
        fieldNames.add("actionType");
        fieldNames.add("message");
        FIELD_NAMES = Collections.unmodifiableList(fieldNames);
    }

    @Override
    public String getUuid() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_UUID);
    }

    @Override
    public void setUuid(String value) {
        realm.checkIfValid();
        row.setString(INDEX_UUID, (String) value);
    }

    @Override
    public int getDataA() {
        realm.checkIfValid();
        return (int) row.getLong(INDEX_DATAA);
    }

    @Override
    public void setDataA(int value) {
        realm.checkIfValid();
        row.setLong(INDEX_DATAA, (long) value);
    }

    @Override
    public int getDataB() {
        realm.checkIfValid();
        return (int) row.getLong(INDEX_DATAB);
    }

    @Override
    public void setDataB(int value) {
        realm.checkIfValid();
        row.setLong(INDEX_DATAB, (long) value);
    }

    @Override
    public int getDataC() {
        realm.checkIfValid();
        return (int) row.getLong(INDEX_DATAC);
    }

    @Override
    public void setDataC(int value) {
        realm.checkIfValid();
        row.setLong(INDEX_DATAC, (long) value);
    }

    @Override
    public int getActionType() {
        realm.checkIfValid();
        return (int) row.getLong(INDEX_ACTIONTYPE);
    }

    @Override
    public void setActionType(int value) {
        realm.checkIfValid();
        row.setLong(INDEX_ACTIONTYPE, (long) value);
    }

    @Override
    public String getMessage() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_MESSAGE);
    }

    @Override
    public void setMessage(String value) {
        realm.checkIfValid();
        row.setString(INDEX_MESSAGE, (String) value);
    }

    public static Table initTable(ImplicitTransaction transaction) {
        if (!transaction.hasTable("class_BeaconMessage")) {
            Table table = transaction.getTable("class_BeaconMessage");
            table.addColumn(ColumnType.STRING, "uuid");
            table.addColumn(ColumnType.INTEGER, "dataA");
            table.addColumn(ColumnType.INTEGER, "dataB");
            table.addColumn(ColumnType.INTEGER, "dataC");
            table.addColumn(ColumnType.INTEGER, "actionType");
            table.addColumn(ColumnType.STRING, "message");
            table.setPrimaryKey("");
            return table;
        }
        return transaction.getTable("class_BeaconMessage");
    }

    public static void validateTable(ImplicitTransaction transaction) {
        if (transaction.hasTable("class_BeaconMessage")) {
            Table table = transaction.getTable("class_BeaconMessage");
            if (table.getColumnCount() != 6) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field count does not match - expected 6 but was " + table.getColumnCount());
            }
            Map<String, ColumnType> columnTypes = new HashMap<String, ColumnType>();
            for (long i = 0; i < 6; i++) {
                columnTypes.put(table.getColumnName(i), table.getColumnType(i));
            }

            columnIndices = new HashMap<String, Long>();
            for (String fieldName : getFieldNames()) {
                long index = table.getColumnIndex(fieldName);
                if (index == -1) {
                    throw new RealmMigrationNeededException(transaction.getPath(), "Field '" + fieldName + "' not found for type BeaconMessage");
                }
                columnIndices.put(fieldName, index);
            }
            INDEX_UUID = table.getColumnIndex("uuid");
            INDEX_DATAA = table.getColumnIndex("dataA");
            INDEX_DATAB = table.getColumnIndex("dataB");
            INDEX_DATAC = table.getColumnIndex("dataC");
            INDEX_ACTIONTYPE = table.getColumnIndex("actionType");
            INDEX_MESSAGE = table.getColumnIndex("message");

            if (!columnTypes.containsKey("uuid")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'uuid'");
            }
            if (columnTypes.get("uuid") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'uuid'");
            }
            if (!columnTypes.containsKey("dataA")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'dataA'");
            }
            if (columnTypes.get("dataA") != ColumnType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'dataA'");
            }
            if (!columnTypes.containsKey("dataB")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'dataB'");
            }
            if (columnTypes.get("dataB") != ColumnType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'dataB'");
            }
            if (!columnTypes.containsKey("dataC")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'dataC'");
            }
            if (columnTypes.get("dataC") != ColumnType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'dataC'");
            }
            if (!columnTypes.containsKey("actionType")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'actionType'");
            }
            if (columnTypes.get("actionType") != ColumnType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'actionType'");
            }
            if (!columnTypes.containsKey("message")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'message'");
            }
            if (columnTypes.get("message") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'message'");
            }
        } else {
            throw new RealmMigrationNeededException(transaction.getPath(), "The BeaconMessage class is missing from the schema for this Realm.");
        }
    }

    public static String getTableName() {
        return "class_BeaconMessage";
    }

    public static List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    public static Map<String,Long> getColumnIndices() {
        return columnIndices;
    }

    public static BeaconMessage createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        BeaconMessage obj = realm.createObject(BeaconMessage.class);
        if (json.has("uuid")) {
            if (json.isNull("uuid")) {
                obj.setUuid("");
            } else {
                obj.setUuid((String) json.getString("uuid"));
            }
        }
        if (!json.isNull("dataA")) {
            obj.setDataA((int) json.getInt("dataA"));
        }
        if (!json.isNull("dataB")) {
            obj.setDataB((int) json.getInt("dataB"));
        }
        if (!json.isNull("dataC")) {
            obj.setDataC((int) json.getInt("dataC"));
        }
        if (!json.isNull("actionType")) {
            obj.setActionType((int) json.getInt("actionType"));
        }
        if (json.has("message")) {
            if (json.isNull("message")) {
                obj.setMessage("");
            } else {
                obj.setMessage((String) json.getString("message"));
            }
        }
        return obj;
    }

    public static BeaconMessage createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        BeaconMessage obj = realm.createObject(BeaconMessage.class);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("uuid")) {
                if (reader.peek() == JsonToken.NULL) {
                    obj.setUuid("");
                    reader.skipValue();
                } else {
                    obj.setUuid((String) reader.nextString());
                }
            } else if (name.equals("dataA")  && reader.peek() != JsonToken.NULL) {
                obj.setDataA((int) reader.nextInt());
            } else if (name.equals("dataB")  && reader.peek() != JsonToken.NULL) {
                obj.setDataB((int) reader.nextInt());
            } else if (name.equals("dataC")  && reader.peek() != JsonToken.NULL) {
                obj.setDataC((int) reader.nextInt());
            } else if (name.equals("actionType")  && reader.peek() != JsonToken.NULL) {
                obj.setActionType((int) reader.nextInt());
            } else if (name.equals("message")) {
                if (reader.peek() == JsonToken.NULL) {
                    obj.setMessage("");
                    reader.skipValue();
                } else {
                    obj.setMessage((String) reader.nextString());
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return obj;
    }

    public static BeaconMessage copyOrUpdate(Realm realm, BeaconMessage object, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        if (object.realm != null && object.realm.getPath().equals(realm.getPath())) {
            return object;
        }
        return copy(realm, object, update, cache);
    }

    public static BeaconMessage copy(Realm realm, BeaconMessage newObject, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        BeaconMessage realmObject = realm.createObject(BeaconMessage.class);
        cache.put(newObject, (RealmObjectProxy) realmObject);
        realmObject.setUuid(newObject.getUuid() != null ? newObject.getUuid() : "");
        realmObject.setDataA(newObject.getDataA());
        realmObject.setDataB(newObject.getDataB());
        realmObject.setDataC(newObject.getDataC());
        realmObject.setActionType(newObject.getActionType());
        realmObject.setMessage(newObject.getMessage() != null ? newObject.getMessage() : "");
        return realmObject;
    }

    static BeaconMessage update(Realm realm, BeaconMessage realmObject, BeaconMessage newObject, Map<RealmObject, RealmObjectProxy> cache) {
        realmObject.setUuid(newObject.getUuid() != null ? newObject.getUuid() : "");
        realmObject.setDataA(newObject.getDataA());
        realmObject.setDataB(newObject.getDataB());
        realmObject.setDataC(newObject.getDataC());
        realmObject.setActionType(newObject.getActionType());
        realmObject.setMessage(newObject.getMessage() != null ? newObject.getMessage() : "");
        return realmObject;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("BeaconMessage = [");
        stringBuilder.append("{uuid:");
        stringBuilder.append(getUuid());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{dataA:");
        stringBuilder.append(getDataA());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{dataB:");
        stringBuilder.append(getDataB());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{dataC:");
        stringBuilder.append(getDataC());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{actionType:");
        stringBuilder.append(getActionType());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{message:");
        stringBuilder.append(getMessage());
        stringBuilder.append("}");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        String realmName = realm.getPath();
        String tableName = row.getTable().getName();
        long rowIndex = row.getIndex();

        int result = 17;
        result = 31 * result + ((realmName != null) ? realmName.hashCode() : 0);
        result = 31 * result + ((tableName != null) ? tableName.hashCode() : 0);
        result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeaconMessageRealmProxy aBeaconMessage = (BeaconMessageRealmProxy)o;

        String path = realm.getPath();
        String otherPath = aBeaconMessage.realm.getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;;

        String tableName = row.getTable().getName();
        String otherTableName = aBeaconMessage.row.getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (row.getIndex() != aBeaconMessage.row.getIndex()) return false;

        return true;
    }

}
