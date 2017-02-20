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
    public static final String COLUMN_ITEMS_VOLUME = "VOLUME";
    public static final String COLUMN_ITEMS_TYPE = "TYPE";
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
                + COLUMN_ITEMS_VOLUME + " INTEGER DEFAULT 0, "
                + COLUMN_ITEMS_TYPE + " INTEGER DEFAULT 0, "
                + COLUMN_ITEMS_EXPIRATION + " LONG DEFAULT 10000000000, "
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
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Injectienaalden");
            itemValues.put(COLUMN_ITEMS_STOCK, 10);
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 2);
            itemValues.put(COLUMN_ITEMS_TYPE, 1);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Naaldencontainer");
            itemValues.put(COLUMN_ITEMS_STOCK, 5);
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 2);
            itemValues.put(COLUMN_ITEMS_TYPE, 0);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Acetylsalicylzuur");
            itemValues.put(COLUMN_ITEMS_STOCK, 5);
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Atropine");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Budesonide");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Bumetanide");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Clemastine");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Dexamethason");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Dexamethason drank");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Diazepam");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Diclofenac");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Epinefrine");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Fentanyl");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Furosemide");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Glucagonpoeder");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Glucoseoplossing");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Haloperidol");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Ipratropiumbromide");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Lorazepam");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Midazolam");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Morfine");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "NaCl");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Nitroglycerine");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Prednisolon");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Salbutamol");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 1);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Pen");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 3);
            itemValues.put(COLUMN_ITEMS_TYPE, 1);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Receptenpapier");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 3);
            itemValues.put(COLUMN_ITEMS_TYPE, 1);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Stethoscoop");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 4);
            itemValues.put(COLUMN_ITEMS_TYPE, 0);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
        {
            ContentValues itemValues = new ContentValues();
            itemValues.put(COLUMN_ITEMS_NAME, "Pleisters");
            itemValues.put(COLUMN_ITEMS_CATEGORIES_ID, 5);
            itemValues.put(COLUMN_ITEMS_TYPE, 3);
            db.insert(TABLE_ITEMS, null, itemValues);
        }
    }

    public void updateDate(int item_id,int year, int month, int dayOfMonth)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        DateTime dateTime = new DateTime();
        dateTime = dateTime.withDate(year,month+1,dayOfMonth);
        long milis = dateTime.getMillis();
        db.execSQL("UPDATE " + TABLE_ITEMS + " SET " + COLUMN_ITEMS_EXPIRATION + " = " + milis + " WHERE " + COLUMN_ITEMS_ID + " = " + item_id );
    }

    public void updateStock(int item_id, String stock)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_ITEMS + " SET " + COLUMN_ITEMS_STOCK + " = " + stock + " WHERE " + COLUMN_ITEMS_ID + " = " + item_id );
    }

    public void updateVolume(int item_id, String volume)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_ITEMS + " SET " + COLUMN_ITEMS_VOLUME + " = " + volume + " WHERE " + COLUMN_ITEMS_ID + " = " + item_id );
    }

    public Cursor getItem(int item_id)
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

    public Cursor countAllItems()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select count(*) from " + TABLE_ITEMS;
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

    public void addItemName(String name, int categoryId, int type)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEMS_NAME, name);
        values.put(COLUMN_ITEMS_CATEGORIES_ID, categoryId);
        values.put(COLUMN_ITEMS_TYPE, type);
        db.insert(TABLE_ITEMS, null, values);
    }

    public void addItemDate(String name, int categoryId,long datum, int type)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEMS_NAME, name);
        values.put(COLUMN_ITEMS_CATEGORIES_ID, categoryId);
        values.put(COLUMN_ITEMS_EXPIRATION, datum);
        values.put(COLUMN_ITEMS_TYPE, type);
        db.insert(TABLE_ITEMS, null, values);
    }

    public void addItemVolume(String name, int categoryId, int volume, int type)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEMS_NAME, name);
        values.put(COLUMN_ITEMS_CATEGORIES_ID, categoryId);
        values.put(COLUMN_ITEMS_VOLUME, volume);
        values.put(COLUMN_ITEMS_TYPE, type);
        db.insert(TABLE_ITEMS, null, values);
    }

    public void addItemDateVolume(String name, int categoryId, long datum, int volume, int type)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEMS_NAME, name);
        values.put(COLUMN_ITEMS_CATEGORIES_ID, categoryId);
        values.put(COLUMN_ITEMS_VOLUME, volume);
        values.put(COLUMN_ITEMS_TYPE, type);
        db.insert(TABLE_ITEMS, null, values);
    }

    public void addItemStock(String name, int categoryId, int voorraad, int type)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEMS_NAME, name);
        values.put(COLUMN_ITEMS_CATEGORIES_ID, categoryId);
        values.put(COLUMN_ITEMS_STOCK, voorraad);
        values.put(COLUMN_ITEMS_TYPE, type);
        db.insert(TABLE_ITEMS, null, values);
    }

    public void addItemStockVolume(String name, int categoryId, int voorraad, int volume, int type)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEMS_NAME, name);
        values.put(COLUMN_ITEMS_CATEGORIES_ID, categoryId);
        values.put(COLUMN_ITEMS_STOCK, voorraad);
        values.put(COLUMN_ITEMS_VOLUME, volume);
        values.put(COLUMN_ITEMS_TYPE, type);
        db.insert(TABLE_ITEMS, null, values);
    }

    public void addItemStockDate(String name, int categoryId, int voorraad, long datum, int type)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEMS_NAME, name);
        values.put(COLUMN_ITEMS_CATEGORIES_ID, categoryId);
        values.put(COLUMN_ITEMS_STOCK, voorraad);
        values.put(COLUMN_ITEMS_EXPIRATION, datum);
        values.put(COLUMN_ITEMS_TYPE, type);
        db.insert(TABLE_ITEMS, null, values);
    }

    public void addItemStockDateVolume(String name, int categoryId, int voorraad, long datum, int volume, int type)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEMS_NAME, name);
        values.put(COLUMN_ITEMS_CATEGORIES_ID, categoryId);
        values.put(COLUMN_ITEMS_STOCK, voorraad);
        values.put(COLUMN_ITEMS_VOLUME, volume);
        values.put(COLUMN_ITEMS_EXPIRATION, datum);
        values.put(COLUMN_ITEMS_TYPE, type);
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
