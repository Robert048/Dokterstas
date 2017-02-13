package minor.dokterstas;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import minor.dokterstas.database.DatabaseHelper;

import static minor.dokterstas.R.id.spinner;

public class MainActivity extends AppCompatActivity {


    SparseArray<Group> groups = new SparseArray<>();
    DatabaseHelper TasDB;
    List<Category> categoryList = new ArrayList<>();
    NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createList();

        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);


    }

    public void createList() {
        categoryList.clear();

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
                int Column3 = c.getColumnIndex(TasDB.COLUMN_ITEMS_EXPIRATION);
                int Column4 = c.getColumnIndex(TasDB.COLUMN_ITEMS_STOCK);
                int Column5 = c.getColumnIndex(TasDB.COLUMN_ITEMS_CATEGORIES_ID);
                while (c.moveToNext()) {
                    int ID = c.getInt(Column1);
                    String Name = c.getString(Column2);
                    String tht = c.getString(Column3);
                    int voorraad = c.getInt(Column4);
                    int CategoryID = c.getInt(Column5);

                    Item item = new Item(ID, Name, tht, voorraad );
                    Category category = categoryList.get(CategoryID - 1);
                    category.addItem(item);
                }
            }
            createData(categoryList);

            ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
            ExpandableListAdapter adapter = new ExpandableListAdapter(this,
                    groups, this, TasDB);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        }

    }

    public void createData(List<Category> Categories) {
        for (int j = 0; j < Categories.size(); j++) {
            Group group = new Group(Categories.get(j).getName());
            for (int i = 0; i < Categories.get(j).getItems().size(); i++) {
                group.children.add(Categories.get(j).getItems().get(i).getName() + "/" + Categories.get(j).getItems().get(i).getID() + "/" + Categories.get(j).getItems().get(i).getTht() + "\n" + Categories.get(j).getItems().get(i).getVoorraad() + " op voorraad");
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
            final Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.settings);
            dialog.setTitle("settings");

            CheckBox setting_expiration = (CheckBox) dialog.findViewById(R.id.setting_expiration);
            setting_expiration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.setting_expiration_checked), true);
                    editor.commit();
                    //https://developer.android.com/training/basics/data-storage/shared-preferences.html

                    /*
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // notificationID allows you to update the notification later on.
                    mNotificationManager.notify(0, mBuilder.build());
                    */
                }
            });

            CheckBox setting_stock = (CheckBox) dialog.findViewById(R.id.setting_stock);
            setting_stock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            CheckBox setting_check = (CheckBox) dialog.findViewById(R.id.setting_check);
            setting_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            dialog.show();
            return true;
        }

        if (id == R.id.Menu_Reset) {

            final Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.confirmation);
            dialog.setTitle("Confirmation");

            Button btnYes = (Button) dialog.findViewById(R.id.yes);
            Button btnNo = (Button) dialog.findViewById(R.id.no);

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TasDB.resetAll(MainActivity.this);
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

            return true;
        }

        if (id == R.id.Menu_Category) {

            //open pop-up

            final Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.createcategory);
            dialog.setTitle("Categorie toevoegen");

            final EditText editText = (EditText) dialog.findViewById(R.id.editText);
            Button btnSave = (Button) dialog.findViewById(R.id.save);
            Button btnCancel = (Button) dialog.findViewById(R.id.cancel);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TasDB.addCategory(editText.getText().toString());

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage(editText.getText().toString() + " categorie toegevoegd.");
                    builder.setTitle("Nieuwe categorie");

                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                    dialog.dismiss();

                    createList();
                }
            });

            dialog.show();
            return true;
        }

        if (id == R.id.Menu_Item) {

            //open pop-up
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.create_item);
            dialog.setTitle("Item toevoegen");

            final Spinner category = (Spinner) dialog.findViewById(spinner);
            final EditText editText = (EditText) dialog.findViewById(R.id.editText);
            Button btnSave = (Button) dialog.findViewById(R.id.save);
            Button btnCancel = (Button) dialog.findViewById(R.id.cancel);

            //spinner settings
            List<Category> categories = new ArrayList<>();
            for (Category cat : categoryList) {
                categories.add(new Category(cat.getID(), cat.getName()));
            }

            // Creating adapter for spinner
            ArrayAdapter<Category> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            category.setAdapter(dataAdapter);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Category cat = (Category) category.getSelectedItem();
                    TasDB.addItem(editText.getText().toString(), cat.getID());
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage(editText.getText().toString() + " item toegevoegd aan categorie: " + cat.getName());
                    builder.setTitle("Nieuwe item");

                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                    dialog.dismiss();

                    createList();

                }
            });

            dialog.show();
            return true;
        }

        if (id == R.id.Settings_1) {
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
