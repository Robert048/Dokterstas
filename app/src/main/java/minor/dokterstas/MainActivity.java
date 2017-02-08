package minor.dokterstas;

import android.os.Bundle;
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
import java.util.List;

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
        List<Category> categoryList = new ArrayList<>();

        /* Create a Database. */
        try {
            myDB = this.openOrCreateDatabase("DokterstasTest", MODE_PRIVATE, null);

            myDB.execSQL("DROP TABLE Category;");
            myDB.execSQL("DROP TABLE Item;");

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
            );

            myDB.execSQL("INSERT INTO "
                    + TableName1
                    + " (ID, Name)"
                    + " VALUES ('2', 'category2');"
            );


            myDB.execSQL("INSERT INTO "
                    + TableName2
                    + " (ID, Name, CategoryID)"
                    + " VALUES ('1', 'Product1', 1);"
            );
            myDB.execSQL("INSERT INTO "
                    + TableName2
                    + " (ID, Name, CategoryID)"
                    + " VALUES ('2', 'Product2', 1);"
            );
            myDB.execSQL("INSERT INTO "
                    + TableName2
                    + " (ID, Name, CategoryID)"
                    + " VALUES ('3', 'Product3', 2);"
            );
            myDB.execSQL("INSERT INTO "
                    + TableName2
                    + " (ID, Name, CategoryID)"
                    + " VALUES ('4', 'Product4', 2);"
            );


            /*retrieve data from database */
            {
                Cursor c = myDB.rawQuery("SELECT * FROM " + TableName1, null);

                int Column1 = c.getColumnIndex("ID");
                int Column2 = c.getColumnIndex("Name");
                while (c.moveToNext()) {
                    int ID = c.getInt(Column1);
                    String Name = c.getString(Column2);
                    Category category = new Category(ID, Name);
                    categoryList.add(category);
                }
            }
            {
                Cursor c = myDB.rawQuery("SELECT * FROM " + TableName2, null);

                int Column1 = c.getColumnIndex("ID");
                int Column2 = c.getColumnIndex("Name");
                int Column3 = c.getColumnIndex("CategoryID");
                while (c.moveToNext()) {
                    int ID = c.getInt(Column1);
                    String Name = c.getString(Column2);
                    int CategoryID = c.getInt(Column3);
                    Item item = new Item(ID, Name);
                    Category category = categoryList.get(CategoryID - 1);
                    category.addItem(item);
                }
            }
            createData(categoryList);
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

    public void createData(List<Category> Categories) {
        for (int j = 0; j < Categories.size(); j++) {
            Group group = new Group(Categories.get(j).getName());
            for (int i = 0; i < Categories.get(j).getItems().size(); i++) {
                group.children.add(Categories.get(j).getItems().get(i).getName());
            }
            groups.append(j, group);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar Item clicks here. The action bar will
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
