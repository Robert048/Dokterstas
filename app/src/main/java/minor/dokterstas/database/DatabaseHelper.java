package minor.dokterstas.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hakob on 8-2-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bag.db";
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS + " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS + " + TABLE_ITEMS);

    }
}
