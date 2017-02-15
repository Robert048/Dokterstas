package minor.dokterstas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hakob on 8-2-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TasDatabase";

    public static final String TABLE_CATEGORIES = "category_table";
    public static final String COLUMN_CATEGORIES_ID = "C_ID";
    public static final String COLUMN_CATEGORIES_NAME = "NAME";

    public static final String TABLE_ITEMS = "items_table";
    public static final String COLUMN_ITEMS_ID = "I_ID";
    public static final String COLUMN_ITEMS_NAME = "NAME";
    public static final String COLUMN_ITEMS_STOCK = "STOCK";
    public static final String COLUMN_ITEMS_EXPIRATION = "EXPIRATION";
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
                + COLUMN_ITEMS_STOCK + " INTEGER DEFAULT 1, "
//                + COLUMN_ITEMS_EXPIRATION + " DATETIME DEFAULT CURRENT_DATE, "
                + COLUMN_ITEMS_EXPIRATION + " LONG DEFAULT 1000000000000, "
                + COLUMN_ITEMS_CATEGORIES_ID + " INTEGER, FOREIGN KEY("+ COLUMN_ITEMS_CATEGORIES_ID +") REFERENCES "+ TABLE_CATEGORIES  +"(" + COLUMN_CATEGORIES_ID + ") )");


        //INSERT CATEGORIES
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORIES_NAME, "Geneesmiddel");
        db.insert(TABLE_CATEGORIES, null, values);

        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_CATEGORIES_NAME, "Injectiemateriaal");
        db.insert(TABLE_CATEGORIES, null, values2);

        ContentValues values3 = new ContentValues();
        values3.put(COLUMN_CATEGORIES_NAME, "Formulieren");
        db.insert(TABLE_CATEGORIES, null, values3);

        ContentValues values4 = new ContentValues();
        values4.put(COLUMN_CATEGORIES_NAME, "Instrumenten");
        db.insert(TABLE_CATEGORIES, null, values4);

        ContentValues values5 = new ContentValues();
        values5.put(COLUMN_CATEGORIES_NAME, "Verbandmateriaal");
        db.insert(TABLE_CATEGORIES, null, values5);


        //INSERT DEFAULT ITEMS IN CATEGORIES
        ContentValues itemValues = new ContentValues();
        itemValues.put(COLUMN_ITEMS_NAME, "Injectienaalden");
        itemValues.put(COLUMN_ITEMS_STOCK, 10);
        itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 2);
        db.insert(TABLE_ITEMS, null, itemValues);

        ContentValues itemValues2 = new ContentValues();
        itemValues2.put(COLUMN_ITEMS_NAME, "Naaldencontainer");
        itemValues.put(COLUMN_ITEMS_STOCK, 5);
        itemValues2.put(COLUMN_ITEMS_CATEGORIES_ID, 2);
        db.insert(TABLE_ITEMS, null, itemValues2);

        ContentValues itemValues3 = new ContentValues();
        itemValues3.put(COLUMN_ITEMS_NAME, "Acetylsalicylzuur");
        itemValues.put(COLUMN_ITEMS_STOCK, 5);
        itemValues3.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
        db.insert(TABLE_ITEMS, null, itemValues3);

        ContentValues itemValues4 = new ContentValues();
        itemValues4.put(COLUMN_ITEMS_NAME, "Atropine");
        itemValues4.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
        db.insert(TABLE_ITEMS, null, itemValues4);

        ContentValues itemValues5 = new ContentValues();
        itemValues5.put(COLUMN_ITEMS_NAME, "Pen");
        itemValues5.put(COLUMN_ITEMS_CATEGORIES_ID, 3);
        db.insert(TABLE_ITEMS, null, itemValues5);

        ContentValues itemValues6 = new ContentValues();
        itemValues6.put(COLUMN_ITEMS_NAME, "Receptenpapier");
        itemValues6.put(COLUMN_ITEMS_CATEGORIES_ID, 3);
        db.insert(TABLE_ITEMS, null, itemValues6);

        ContentValues itemValues7 = new ContentValues();
        itemValues7.put(COLUMN_ITEMS_NAME, "Stethoscoop");
        itemValues7.put(COLUMN_ITEMS_CATEGORIES_ID, 4);
        db.insert(TABLE_ITEMS, null, itemValues7);

        ContentValues itemValues8 = new ContentValues();
        itemValues8.put(COLUMN_ITEMS_NAME, "Pleisters");
        itemValues8.put(COLUMN_ITEMS_CATEGORIES_ID, 5);
        db.insert(TABLE_ITEMS, null, itemValues8);
    }


    public void insertDate(Date date)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(COLUMN_ITEMS_EXPIRATION, date.getTime());
        db.insert(TABLE_ITEMS, null, values);
    }

    public void updateDate(int item_id,int year, int month, int dayOfMonth)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        DateTime dateTime = new DateTime();
        dateTime = dateTime.withDate(year,month+1,dayOfMonth);
        long milis = dateTime.getMillis();
        db.execSQL("UPDATE " + TABLE_ITEMS + " SET " + COLUMN_ITEMS_EXPIRATION + " = " + milis + " WHERE " + COLUMN_ITEMS_ID + " = " + item_id );

        String kabout = "kabouter";
    }




    public void setStock(String item_id, String stock)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_ITEMS + " SET " + COLUMN_ITEMS_STOCK + " = " + stock + " WHERE " + COLUMN_ITEMS_ID + " = " + item_id );
    }

    public Cursor getStock(String item_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_ITEMS + " where " + COLUMN_ITEMS_ID + " = " + item_id;
        Cursor result = db.rawQuery(query, null);
        return result;
    }

    public Cursor getAllItems()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_ITEMS;
        Cursor result = db.rawQuery(query, null);
        return result;
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

    public void resetAll(Context context)
    {
        context.deleteDatabase(DATABASE_NAME);
    }

    public void addCategory(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORIES_NAME, name);

        db.insert(TABLE_CATEGORIES, null, values);
    }

    public void addItem(String name, int categoryId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEMS_NAME, name);
        values.put(COLUMN_ITEMS_CATEGORIES_ID, categoryId);
        db.insert(TABLE_ITEMS, null, values);
    }

    public void deleteItem(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        db.delete(TABLE_ITEMS,"I_ID=?",new String[]{id});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS + " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS + " + TABLE_ITEMS);

        onCreate(db);

    }


}
