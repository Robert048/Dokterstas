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

import minor.dokterstas.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {


    SparseArray<Group> groups = new SparseArray<>();
    DatabaseHelper TasDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        List<Category> categoryList = new ArrayList<>();

        try {

            /*retrieve data from database */
            TasDB = new DatabaseHelper(this);

            {
                Cursor c = TasDB.getAllDataFromTable(1);

                int Column1 = c.getColumnIndex(TasDB.COLUMN_CATEGORIES_ID);
                int Column2 = c.getColumnIndex(TasDB.COLUMN_CATEGORIES_NAME);
                while (c.moveToNext()) {
                    int ID = c.getInt(Column1);
                    String Name = c.getString(Column2);
                    Category category = new Category(ID, Name);
                    categoryList.add(category);
                }
            }
            {
                Cursor c = TasDB.getAllDataFromTable(2);

                int Column1 = c.getColumnIndex(TasDB.COLUMN_ITEMS_ID);
                int Column2 = c.getColumnIndex(TasDB.COLUMN_ITEMS_NAME);
                int Column3 = c.getColumnIndex(TasDB.COLUMN_ITEMS_CATEGORIES_ID);
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

        if (id == R.id.Menu_Settings) {

            Intent intent = new Intent(MainActivity.this, Edit.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.Menu_Category) {

            //open pop-up

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

        return super.onOptionsItemSelected(item);
    }
}
