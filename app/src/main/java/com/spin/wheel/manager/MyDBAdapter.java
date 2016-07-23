package com.spin.wheel.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class MyDBAdapter {
    private static final String DATABASE_CREATE_ITEMS = "create table itemTable (item_id integer primary key autoincrement, item_name text not null, name_ref_ID integer not null);";
    private static final String DATABASE_CREATE_NAMES = "create table nameTable (names_id integer primary key autoincrement, list_name text not null);";
    private static final String DATABASE_NAME = "listInfo.db";
    private static final String DATABASE_TABLE_ITEMS = "itemTable";
    private static final String DATABASE_TABLE_NAMES = "nameTable";
    private static final int DATABASE_VERSION = 2;
    public static final String FOR_KEY_NAME_ID = "name_ref_ID";
    public static final int ITEM_COLUMN = 1;
    public static final int KEY_ID_COLUMN = 0;
    public static final String KEY_ID_ITEMS = "item_id";
    public static final String KEY_ID_NAMES = "names_id";
    public static final String KEY_ITEM = "item_name";
    public static final String KEY_NAME = "list_name";
    public static final int NAME_COLUMN = 1;
    public static final int NAME_REF = 2;
    private final Context context;
    private SQLiteDatabase db;
    private myDBHelper dbHelper;

    private static class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(MyDBAdapter.DATABASE_CREATE_NAMES);
            _db.execSQL(MyDBAdapter.DATABASE_CREATE_ITEMS);
            createSamples(_db);
        }

        public int getIDofListName(String listName, SQLiteDatabase _db) {
            String str = MyDBAdapter.DATABASE_TABLE_NAMES;
            String[] strArr = new String[MyDBAdapter.NAME_REF];
            strArr[MyDBAdapter.KEY_ID_COLUMN] = MyDBAdapter.KEY_ID_NAMES;
            strArr[MyDBAdapter.NAME_COLUMN] = MyDBAdapter.KEY_NAME;
            String[] strArr2 = new String[MyDBAdapter.NAME_COLUMN];
            strArr2[MyDBAdapter.KEY_ID_COLUMN] = listName;
            Cursor result = _db.query(str, strArr, "list_name=?", strArr2, null, null, null);
            if (result.moveToNext()) {
                return result.getInt(MyDBAdapter.KEY_ID_COLUMN);
            }
            return -1;
        }

        private void createSamples(SQLiteDatabase _db) {
            insertListNameSample("Coin Toss", _db);
            insertListNameSample("Yes, No, Maybe", _db);
            insertListNameSample("What should we do tonight?", _db);
            insertListNameSample("What should we eat?", _db);
            insertListItemSample("Heads", "Coin Toss", _db);
            insertListItemSample("Tails", "Coin Toss", _db);
            insertListItemSample("Yes", "Yes, No, Maybe", _db);
            insertListItemSample("No", "Yes, No, Maybe", _db);
            insertListItemSample("Maybe", "Yes, No, Maybe", _db);
            insertListItemSample("Go dancing", "What should we do tonight?", _db);
            insertListItemSample("Sing karaoke", "What should we do tonight?", _db);
            insertListItemSample("Stay in", "What should we do tonight?", _db);
            insertListItemSample("Go bowling", "What should we do tonight?", _db);
            insertListItemSample("See a movie", "What should we do tonight?", _db);
            insertListItemSample("Mexican", "What should we eat?", _db);
            insertListItemSample("Italian", "What should we eat?", _db);
            insertListItemSample("Pizza", "What should we eat?", _db);
            insertListItemSample("Burgers", "What should we eat?", _db);
            insertListItemSample("Vegetarian", "What should we eat?", _db);
            insertListItemSample("Japanese", "What should we eat?", _db);
            insertListItemSample("Thai", "What should we eat?", _db);
            insertListItemSample("Buffet", "What should we eat?", _db);
            insertListItemSample("Chinese", "What should we eat?", _db);
            insertListItemSample("Middle Eastern", "What should we eat?", _db);
        }

        public long insertListNameSample(String listName, SQLiteDatabase _db) {
            ContentValues newValues = new ContentValues();
            newValues.put(MyDBAdapter.KEY_NAME, listName);
            return _db.insert(MyDBAdapter.DATABASE_TABLE_NAMES, null, newValues);
        }

        public long insertListItemSample(String itemName, String listName, SQLiteDatabase _db) {
            ContentValues newValues = new ContentValues();
            newValues.put(MyDBAdapter.FOR_KEY_NAME_ID, Integer.valueOf(getIDofListName(listName, _db)));
            newValues.put(MyDBAdapter.KEY_ITEM, itemName);
            return _db.insert(MyDBAdapter.DATABASE_TABLE_ITEMS, null, newValues);
        }

        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion + " to " + _newVersion + ", which will destroy all old data");
            _db.execSQL("DROP TABLE IF EXISTS nameTable");
            _db.execSQL("DROP TABLE IF EXISTS itemTable");
            onCreate(_db);
        }
    }

    public MyDBAdapter(Context _context) {
        this.context = _context;
        this.dbHelper = new myDBHelper(this.context, DATABASE_NAME, null, NAME_REF);
    }

    public MyDBAdapter open() throws SQLException {
        this.db = this.dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.db.close();
    }

    public long insertListName(String listName) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, listName);
        return this.db.insert(DATABASE_TABLE_NAMES, null, newValues);
    }

    public long insertListItem(String itemName, String listName) {
        ContentValues newValues = new ContentValues();
        newValues.put(FOR_KEY_NAME_ID, Integer.valueOf(getIDofListName(listName)));
        newValues.put(KEY_ITEM, itemName);
        return this.db.insert(DATABASE_TABLE_ITEMS, null, newValues);
    }

    public boolean updateListName(int listNameID, String listNameNew) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, listNameNew);
        return this.db.update(DATABASE_TABLE_NAMES, newValues, new StringBuilder("names_id=").append(listNameID).toString(), null) > 0;
    }

    public boolean updateListItem(int listItemID, String listItemNew) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_ITEM, listItemNew);
        return this.db.update(DATABASE_TABLE_ITEMS, newValues, new StringBuilder("item_id=").append(listItemID).toString(), null) > 0;
    }

    public boolean removeRow(int rowID, String DBName) {
        if (DBName.equals(DATABASE_TABLE_NAMES)) {
            this.db.delete(DATABASE_TABLE_ITEMS, "name_ref_ID=" + rowID, null);
            if (this.db.delete(DBName, "names_id=" + rowID, null) > 0) {
                return true;
            }
            return false;
        } else if (!DBName.equals(DATABASE_TABLE_ITEMS)) {
            return false;
        } else {
            if (this.db.delete(DBName, "item_id=" + rowID, null) <= 0) {
                return false;
            }
            return true;
        }
    }

    public Cursor getAllNames() {
        SQLiteDatabase sQLiteDatabase = this.db;
        String str = DATABASE_TABLE_NAMES;
        String[] strArr = new String[NAME_REF];
        strArr[KEY_ID_COLUMN] = KEY_ID_NAMES;
        strArr[NAME_COLUMN] = KEY_NAME;
        return sQLiteDatabase.query(str, strArr, null, null, null, null, null);
    }

    public List<String> getAllNamesStr() {
        Cursor myPointer = getAllNames();
        List<String> myList = new ArrayList();
        while (myPointer.moveToNext()) {
            myList.add(myPointer.getString(NAME_COLUMN));
        }
        return myList;
    }

    public List<String> getAllItemsForListNameStr(String listName) {
        List<String> myList = new ArrayList();
        Cursor myPointer = getAllItemsForNameStr(listName);
        while (myPointer.moveToNext()) {
            myList.add(myPointer.getString(NAME_COLUMN));
        }
        return myList;
    }

    public Cursor getAllItems() {
        SQLiteDatabase sQLiteDatabase = this.db;
        String str = DATABASE_TABLE_ITEMS;
        String[] strArr = new String[NAME_REF];
        strArr[KEY_ID_COLUMN] = KEY_ID_ITEMS;
        strArr[NAME_COLUMN] = KEY_ITEM;
        return sQLiteDatabase.query(str, strArr, null, null, null, null, null);
    }

    public List<String> getAllItemsStr() {
        Cursor myPointer = getAllItems();
        List<String> myList = new ArrayList();
        while (myPointer.moveToNext()) {
            myList.add(myPointer.getString(NAME_COLUMN));
        }
        return myList;
    }

    public Cursor getAllItemsForNameStr(String listName) {
        return this.db.query(DATABASE_TABLE_ITEMS, new String[]{KEY_ID_ITEMS, KEY_ITEM, FOR_KEY_NAME_ID}, "name_ref_ID=" + getIDofListName(listName), null, null, null, null);
    }

    public void clearDB() {
        this.db.delete(DATABASE_TABLE_NAMES, null, null);
        this.db.delete(DATABASE_TABLE_ITEMS, null, null);
    }

    public int getIDofListName(String listName) {
        SQLiteDatabase sQLiteDatabase = this.db;
        String str = DATABASE_TABLE_NAMES;
        String[] strArr = new String[NAME_REF];
        strArr[KEY_ID_COLUMN] = KEY_ID_NAMES;
        strArr[NAME_COLUMN] = KEY_NAME;
        String[] strArr2 = new String[NAME_COLUMN];
        strArr2[KEY_ID_COLUMN] = listName;
        Cursor result = sQLiteDatabase.query(str, strArr, "list_name=?", strArr2, null, null, null);
        if (result.moveToNext()) {
            return result.getInt(KEY_ID_COLUMN);
        }
        return -1;
    }

    public String getStrofListNameID(int listNameID) {
        SQLiteDatabase sQLiteDatabase = this.db;
        String str = DATABASE_TABLE_NAMES;
        String[] strArr = new String[NAME_REF];
        strArr[KEY_ID_COLUMN] = KEY_ID_NAMES;
        strArr[NAME_COLUMN] = KEY_NAME;
        Cursor result = sQLiteDatabase.query(str, strArr, "names_id=" + listNameID, null, null, null, null);
        if (result.moveToNext()) {
            return result.getString(NAME_COLUMN);
        }
        return "";
    }

    public int getIDofListItem(String listItem) {
        SQLiteDatabase sQLiteDatabase = this.db;
        String str = DATABASE_TABLE_ITEMS;
        String[] strArr = new String[]{KEY_ID_ITEMS, KEY_ITEM, FOR_KEY_NAME_ID};
        String[] strArr2 = new String[NAME_COLUMN];
        strArr2[KEY_ID_COLUMN] = listItem;
        Cursor result = sQLiteDatabase.query(str, strArr, "item_name=?", strArr2, null, null, null);
        if (result.moveToNext()) {
            return result.getInt(KEY_ID_COLUMN);
        }
        return -1;
    }

    public boolean itemExistsForListName(String listItem, String listName) {
        if (getAllItemsForListNameStr(listName).contains(listItem)) {
            return true;
        }
        return false;
    }
}
