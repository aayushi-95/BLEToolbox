package io.realm;


import android.util.JsonReader;
import android.util.JsonToken;
import com.freescale.bletoolbox.model.ZB_CoordinaterItemShortcuts;
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

public class ZB_CoordinaterItemShortcutsRealmProxy extends ZB_CoordinaterItemShortcuts
    implements RealmObjectProxy {

    private static long INDEX_STRCOMMAND;
    private static long INDEX_STRVALUE;
    private static long INDEX_STRTIME;
    private static Map<String, Long> columnIndices;
    private static final List<String> FIELD_NAMES;
    static {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("strCommand");
        fieldNames.add("strValue");
        fieldNames.add("strTime");
        FIELD_NAMES = Collections.unmodifiableList(fieldNames);
    }

    @Override
    public String getStrCommand() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_STRCOMMAND);
    }

    @Override
    public void setStrCommand(String value) {
        realm.checkIfValid();
        row.setString(INDEX_STRCOMMAND, (String) value);
    }

    @Override
    public String getStrValue() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_STRVALUE);
    }

    @Override
    public void setStrValue(String value) {
        realm.checkIfValid();
        row.setString(INDEX_STRVALUE, (String) value);
    }

    @Override
    public String getStrTime() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_STRTIME);
    }

    @Override
    public void setStrTime(String value) {
        realm.checkIfValid();
        row.setString(INDEX_STRTIME, (String) value);
    }

    public static Table initTable(ImplicitTransaction transaction) {
        if (!transaction.hasTable("class_ZB_CoordinaterItemShortcuts")) {
            Table table = transaction.getTable("class_ZB_CoordinaterItemShortcuts");
            table.addColumn(ColumnType.STRING, "strCommand");
            table.addColumn(ColumnType.STRING, "strValue");
            table.addColumn(ColumnType.STRING, "strTime");
            table.setPrimaryKey("");
            return table;
        }
        return transaction.getTable("class_ZB_CoordinaterItemShortcuts");
    }

    public static void validateTable(ImplicitTransaction transaction) {
        if (transaction.hasTable("class_ZB_CoordinaterItemShortcuts")) {
            Table table = transaction.getTable("class_ZB_CoordinaterItemShortcuts");
            if (table.getColumnCount() != 3) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field count does not match - expected 3 but was " + table.getColumnCount());
            }
            Map<String, ColumnType> columnTypes = new HashMap<String, ColumnType>();
            for (long i = 0; i < 3; i++) {
                columnTypes.put(table.getColumnName(i), table.getColumnType(i));
            }

            columnIndices = new HashMap<String, Long>();
            for (String fieldName : getFieldNames()) {
                long index = table.getColumnIndex(fieldName);
                if (index == -1) {
                    throw new RealmMigrationNeededException(transaction.getPath(), "Field '" + fieldName + "' not found for type ZB_CoordinaterItemShortcuts");
                }
                columnIndices.put(fieldName, index);
            }
            INDEX_STRCOMMAND = table.getColumnIndex("strCommand");
            INDEX_STRVALUE = table.getColumnIndex("strValue");
            INDEX_STRTIME = table.getColumnIndex("strTime");

            if (!columnTypes.containsKey("strCommand")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'strCommand'");
            }
            if (columnTypes.get("strCommand") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'strCommand'");
            }
            if (!columnTypes.containsKey("strValue")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'strValue'");
            }
            if (columnTypes.get("strValue") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'strValue'");
            }
            if (!columnTypes.containsKey("strTime")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'strTime'");
            }
            if (columnTypes.get("strTime") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'strTime'");
            }
        } else {
            throw new RealmMigrationNeededException(transaction.getPath(), "The ZB_CoordinaterItemShortcuts class is missing from the schema for this Realm.");
        }
    }

    public static String getTableName() {
        return "class_ZB_CoordinaterItemShortcuts";
    }

    public static List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    public static Map<String,Long> getColumnIndices() {
        return columnIndices;
    }

    public static ZB_CoordinaterItemShortcuts createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        ZB_CoordinaterItemShortcuts obj = realm.createObject(ZB_CoordinaterItemShortcuts.class);
        if (json.has("strCommand")) {
            if (json.isNull("strCommand")) {
                obj.setStrCommand("");
            } else {
                obj.setStrCommand((String) json.getString("strCommand"));
            }
        }
        if (json.has("strValue")) {
            if (json.isNull("strValue")) {
                obj.setStrValue("");
            } else {
                obj.setStrValue((String) json.getString("strValue"));
            }
        }
        if (json.has("strTime")) {
            if (json.isNull("strTime")) {
                obj.setStrTime("");
            } else {
                obj.setStrTime((String) json.getString("strTime"));
            }
        }
        return obj;
    }

    public static ZB_CoordinaterItemShortcuts createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        ZB_CoordinaterItemShortcuts obj = realm.createObject(ZB_CoordinaterItemShortcuts.class);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("strCommand")) {
                if (reader.peek() == JsonToken.NULL) {
                    obj.setStrCommand("");
                    reader.skipValue();
                } else {
                    obj.setStrCommand((String) reader.nextString());
                }
            } else if (name.equals("strValue")) {
                if (reader.peek() == JsonToken.NULL) {
                    obj.setStrValue("");
                    reader.skipValue();
                } else {
                    obj.setStrValue((String) reader.nextString());
                }
            } else if (name.equals("strTime")) {
                if (reader.peek() == JsonToken.NULL) {
                    obj.setStrTime("");
                    reader.skipValue();
                } else {
                    obj.setStrTime((String) reader.nextString());
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return obj;
    }

    public static ZB_CoordinaterItemShortcuts copyOrUpdate(Realm realm, ZB_CoordinaterItemShortcuts object, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        if (object.realm != null && object.realm.getPath().equals(realm.getPath())) {
            return object;
        }
        return copy(realm, object, update, cache);
    }

    public static ZB_CoordinaterItemShortcuts copy(Realm realm, ZB_CoordinaterItemShortcuts newObject, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        ZB_CoordinaterItemShortcuts realmObject = realm.createObject(ZB_CoordinaterItemShortcuts.class);
        cache.put(newObject, (RealmObjectProxy) realmObject);
        realmObject.setStrCommand(newObject.getStrCommand() != null ? newObject.getStrCommand() : "");
        realmObject.setStrValue(newObject.getStrValue() != null ? newObject.getStrValue() : "");
        realmObject.setStrTime(newObject.getStrTime() != null ? newObject.getStrTime() : "");
        return realmObject;
    }

    static ZB_CoordinaterItemShortcuts update(Realm realm, ZB_CoordinaterItemShortcuts realmObject, ZB_CoordinaterItemShortcuts newObject, Map<RealmObject, RealmObjectProxy> cache) {
        realmObject.setStrCommand(newObject.getStrCommand() != null ? newObject.getStrCommand() : "");
        realmObject.setStrValue(newObject.getStrValue() != null ? newObject.getStrValue() : "");
        realmObject.setStrTime(newObject.getStrTime() != null ? newObject.getStrTime() : "");
        return realmObject;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("ZB_CoordinaterItemShortcuts = [");
        stringBuilder.append("{strCommand:");
        stringBuilder.append(getStrCommand());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{strValue:");
        stringBuilder.append(getStrValue());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{strTime:");
        stringBuilder.append(getStrTime());
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
        ZB_CoordinaterItemShortcutsRealmProxy aZB_CoordinaterItemShortcuts = (ZB_CoordinaterItemShortcutsRealmProxy)o;

        String path = realm.getPath();
        String otherPath = aZB_CoordinaterItemShortcuts.realm.getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;;

        String tableName = row.getTable().getName();
        String otherTableName = aZB_CoordinaterItemShortcuts.row.getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (row.getIndex() != aZB_CoordinaterItemShortcuts.row.getIndex()) return false;

        return true;
    }

}
