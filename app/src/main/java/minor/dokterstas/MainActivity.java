package minor.dokterstas;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Intent;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    SparseArray<Group> groups = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SQLiteDatabase myDB= null;
        String TableName1 = "Category";
        String TableName2 = "Item";
        Map<Integer, String> Data = new Map<Integer, String>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public String get(Object key) {
                return null;
            }

            @Override
            public String put(Integer key, String value) {
                return null;
            }

            @Override
            public String remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map<? extends Integer, ? extends String> m) {

            }

            @Override
            public void clear() {

            }

            @NonNull
            @Override
            public Set<Integer> keySet() {
                return null;
            }

            @NonNull
            @Override
            public Collection<String> values() {
                return null;
            }

            @NonNull
            @Override
            public Set<Entry<Integer, String>> entrySet() {
                return null;
            }
        };

        /* Create a Database. */
        try {
            myDB = this.openOrCreateDatabase("DokterstasTest", MODE_PRIVATE, null);

            /* Create a Table in the Database. */
            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + TableName1
                    + " (ID INT, Name Varchar);");

            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + TableName2
                    + " (ID INT, Name Varchar, CategoryID int);");

            /* Insert data to a Table*/
            myDB.execSQL("INSERT INTO "
                    + TableName1
                    + " (ID, Name)"
                    + " VALUES ('1', 'category1');"
                    + " VALUES ('2', 'category2');"
            );

            /*retrieve data from database */
            Cursor c = myDB.rawQuery("SELECT * FROM " + TableName1 , null);

            int Column1 = c.getColumnIndex("ID");
            int Column2 = c.getColumnIndex("Name");
            // Check if our result was valid.
            while(c.moveToNext())
            {
                int ID = c.getInt(Column1);
                String Name = c.getString(Column2);
                Data.put(ID, Name);
            }

            createData(Data);
        }
        catch(Exception e) {
            Log.e("Error", "Error", e);
        } finally {
            if (myDB != null)
                myDB.close();
        }

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        ExpandableListAdapter adapter = new ExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);
    }

    public void createData(Map<Integer, String> data) {
        //for (sting value : data.val)

        //for (int j = 0; j < data.size(); j++) {
        //    Group group = new Group(data.);
        //    for (int i = 0; i < 5; i++) {
        //        group.children.add(data + i);
        //    }
        //    groups.append(j, group);
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_Inventory) {

            Intent intent = new Intent(MainActivity.this, Edit.class);
            startActivity(intent);

            //setContentView(R.layout.activity_edit);
            return true;
        }

        if (id == R.id.Settings_1) {
            if(item.isChecked())
            {
                item.setChecked(false);
            }
            else
            {
                item.setChecked(true);
            }
            return true;
        }

        if (id == R.id.Settings_2) {
            if(item.isChecked())
            {
                item.setChecked(false);
            }
            else
            {
                item.setChecked(true);
            }
            return true;
        }

        if (id == R.id.Settings_3) {
            if(item.isChecked())
            {
                item.setChecked(false);
            }
            else
            {
                item.setChecked(true);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
