package minor.dokterstas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

public class MainActivity extends AppCompatActivity {

    SparseArray<Group> groups = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createData();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        ExpandableListAdapter adapter = new ExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);
    }

    public void createData() {
        for (int j = 0; j < 5; j++) {
            Group group = new Group("Test " + j);
            for (int i = 0; i < 5; i++) {
                group.children.add("Sub Item" + i);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_Inventory) {
            setContentView(R.layout.content_edit);
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
