package com.freescale.bletoolbox.model;

import io.realm.Realm;
import io.realm.RealmMigration;
import io.realm.internal.ColumnType;
import io.realm.internal.Table;

public class MyMigration implements RealmMigration {
    private static final long SCHEMA_VERSION = 1;
    @Override
    public long execute(Realm realm, long version) {

        if(SCHEMA_VERSION > version){
            Table table = realm.getTable(ItemRecent.class);
            table.addColumn(ColumnType.STRING,"strCommand");
            table.addColumn(ColumnType.STRING,"strTime");

            Table table2 = realm.getTable(ItemShortcuts.class);
            table2.addColumn(ColumnType.STRING,"strCommand");
            table2.addColumn(ColumnType.STRING,"strValue");
            table2.addColumn(ColumnType.STRING,"strTime");
            version++;
        }
        return version;
    }
}
