package minor.dokterstas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import minor.dokterstas.DatabaseContract;

/**
 * Created by Hakob on 8-2-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BagDatabase";
    public static final String TABLE_CATEGORIES = "category_table";
    public static final String COLUMN_CATEGORIES_ID = "C_ID";
    public static final String COLUMN_CATEGORIES_NAME = "NAME";
    public static final String TABLE_ITEMS = "items_table";
    public static final String COLUMN_ITEMS_ID = "I_ID";
    public static final String COLUMN_ITEMS_NAME = "NAME";
    public static final String COLUMN_ITEMS_CATEGORIES_ID = "C_ID";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "
                + TABLE_CATEGORIES + " ("
                + COLUMN_CATEGORIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORIES_NAME + " TEXT)");
        db.execSQL("create table "
                + TABLE_ITEMS + " ("
                + COLUMN_ITEMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ITEMS_NAME + " TEXT, "
                + COLUMN_ITEMS_CATEGORIES_ID + " INTEGER, FOREIGN KEY("+ COLUMN_ITEMS_CATEGORIES_ID +") REFERENCES "+ TABLE_CATEGORIES  +"(" + COLUMN_CATEGORIES_ID + ") )");


        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORIES_NAME, "Geneesmiddel");
        db.insert(TABLE_CATEGORIES, null, values);

        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_CATEGORIES_NAME, "Injectiemateriaal");
        db.insert(TABLE_CATEGORIES, null, values2);

        ContentValues itemValues = new ContentValues();
        itemValues.put(COLUMN_ITEMS_NAME, "Injectienaalden");
        itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 2);
        db.insert(TABLE_ITEMS, null, itemValues);

        ContentValues itemValues2 = new ContentValues();
        itemValues2.put(COLUMN_ITEMS_NAME, "Naaldencontainer");
        itemValues2.put(COLUMN_ITEMS_CATEGORIES_ID, 2);
        db.insert(TABLE_ITEMS, null, itemValues2);

        ContentValues itemValues3 = new ContentValues();
        itemValues3.put(COLUMN_ITEMS_NAME, "Acetylsalicylzuur");
        itemValues3.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
        db.insert(TABLE_ITEMS, null, itemValues3);

        ContentValues itemValues4 = new ContentValues();
        itemValues4.put(COLUMN_ITEMS_NAME, "Atropine");
        itemValues4.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
        db.insert(TABLE_ITEMS, null, itemValues4);
    }


    public Cursor getAllDataFromTable(int table) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "";
        if(table == 1) {
            query = "select * from " + TABLE_CATEGORIES;
        }
        else
        {
            query = "select * from " + TABLE_ITEMS;
        }

        Cursor res = db.rawQuery(query, null);
        return res;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS + " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS + " + TABLE_ITEMS);

        onCreate(db);

    }


}
