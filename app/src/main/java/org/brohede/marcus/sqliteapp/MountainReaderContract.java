package org.brohede.marcus.sqliteapp;

import android.provider.BaseColumns;

/**
 * Created by marcus on 2018-04-25.
 */

public class MountainReaderContract {
    // This class should contain your database schema.
    // See: https://developer.android.com/training/data-storage/sqlite.html#DefineContract

    private MountainReaderContract() {}

    // Inner class that defines the Mountain table contents
    public static class MountainEntry implements BaseColumns {
        public static final String TABLE_NAME = "Mountain";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_HEIGTH = "height";
        public static final String COLUMN_NAME_BILD = "bild";
        public static final String COLUMN_NAME_URL = "url";
    }


}
