package minor.dokterstas;

import android.app.AlarmManager;
import android.app.Dialog;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import net.danlew.android.joda.JodaTimeAndroid;
import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import minor.dokterstas.database.DatabaseHelper;

import static minor.dokterstas.R.id.checkbox;
import static minor.dokterstas.R.id.spinner;

public class MainActivity extends AppCompatActivity{


    SparseArray<Group> groups = new SparseArray<>();
    DatabaseHelper TasDB;
    List<Category> categoryList = new ArrayList<>();
    NotificationCompat.Builder mBuilder;
    private static int minimumStock = 5;
    private int counter;
    private int counterAmount;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        JodaTimeAndroid.init(this);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        minimumStock = sharedPref.getInt("minimumStock", minimumStock);

        createList();
        setNotifications();
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


        Cursor c = TasDB.countAllItems();
        int column1 = c.getColumnIndex("count(*)");
        while (c.moveToNext()) {
            counterAmount = c.getInt(column1);
        }

        SampleAlarmReceiver alarm = new SampleAlarmReceiver();
        alarm.setAlarm(this);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 37);
        calendar.set(Calendar.SECOND, 00);

        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 20, alarmIntent);

        /*
        Intent intent1 = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        */
    }



    private void setNotifications() {
        try {
            /*retrieve data from database */
            TasDB = new DatabaseHelper(this);
            //Check Stock and expiration date per item
            Cursor c = TasDB.getAllItems();
            int Column1 = c.getColumnIndex(TasDB.COLUMN_ITEMS_NAME);
            int Column2 = c.getColumnIndex(TasDB.COLUMN_ITEMS_STOCK);
            int Column3 = c.getColumnIndex(TasDB.COLUMN_ITEMS_EXPIRATION);
            int Column4 = c.getColumnIndex(TasDB.COLUMN_ITEMS_TYPE);
            String namen = "";
            String voorraden = "";
            String namen2 = "";
            String expirationDateText = "";
            while (c.moveToNext()) {
                String Name = c.getString(Column1);
                int Stock = c.getInt(Column2);
                long date = c.getLong(Column3);
                int type = c.getInt(Column4);

                DateTime dt = new DateTime();
                dt = dt.withMillis(date);
                String dateText = String.valueOf(dt.dayOfMonth().get()) + "/" + String.valueOf(dt.monthOfYear().get()) + "/" +  String.valueOf(dt.year().get());

                Date expirationDate = dt.toDate();
                if (minimumStock >= Stock && (type == 1 || type == 3)) {
                    if(namen.equals(""))
                    {
                        namen = Name + "\n";
                        voorraden = Stock + "\n";
                    }
                    else
                    {
                        namen = namen + Name + "\n";
                        voorraden = voorraden + Stock + "\n";
                    }
                }
                Calendar calendar = new GregorianCalendar();
                if(expirationDate.before(calendar.getTime()) && (type == 2 || type == 3))
                {
                    if(namen2.equals(""))
                    {
                        namen2 = Name + "\n";
                        expirationDateText = dateText + "\n";
                    }
                    else
                    {
                        namen2 = namen2 + Name + "\n";
                        expirationDateText = expirationDateText + dateText + "\n";
                    }
                }

            }
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.stock_message);
            dialog.setTitle("Voorraad meldingen");
            TextView txtName = (TextView) dialog.findViewById(R.id.txtName);
            TextView txtStock = (TextView) dialog.findViewById(R.id.txtStock);
            TextView txtName2 = (TextView) dialog.findViewById(R.id.txtName2);
            TextView txtDate = (TextView) dialog.findViewById(R.id.txtDate);
            txtName.setText(namen);
            txtStock.setText(voorraden);
            txtName2.setText(namen2);
            txtDate.setText(expirationDateText);
            dialog.show();
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        }
    }

    public static int getMinimumStock()
    {
        return minimumStock;
    }

    public void setMinimumStock(int Stock)
    {
        minimumStock = Stock;
    }


    //Method to repopulate the category list including its items.
    //and reloads the checklists
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
                int Column6 = c.getColumnIndex(TasDB.COLUMN_ITEMS_TYPE);
                while (c.moveToNext()) {
                    int ID = c.getInt(Column1);
                    String Name = c.getString(Column2);
                    long tht = c.getLong(Column3);
                    int voorraad = c.getInt(Column4);
                    int CategoryID = c.getInt(Column5);
                    int type = c.getInt(Column6);
                    DateTime dt = new DateTime();
                    dt = dt.withMillis(tht);


                    String dateText = String.valueOf(dt.dayOfMonth().get()) + "/" + String.valueOf(dt.monthOfYear().get()) + "/" +  String.valueOf(dt.year().get());

                    Item item = new Item(ID, Name, dateText, voorraad, type);
                    Category category = categoryList.get(CategoryID - 1);
                    category.addItem(item);
                }
            }

            //Create items in categories
            for (int j = 0; j < categoryList.size(); j++) {
                Group group = new Group(categoryList.get(j).getName());
                for (int i = 0; i < categoryList.get(j).getItems().size(); i++) {
                    int type = categoryList.get(j).getItems().get(i).getType();
                    if(type == 0) group.children.add(categoryList.get(j).getItems().get(i).getName() + "-" + categoryList.get(j).getItems().get(i).getID() + "- ");
                    else if(type == 1) group.children.add(categoryList.get(j).getItems().get(i).getName() + "-" + categoryList.get(j).getItems().get(i).getID() + "-" + "\n" + categoryList.get(j).getItems().get(i).getVoorraad() + " op voorraad");
                    else if(type == 2) group.children.add(categoryList.get(j).getItems().get(i).getName() + "-" + categoryList.get(j).getItems().get(i).getID() + "-" + categoryList.get(j).getItems().get(i).getTht());
                    else if(type == 3) group.children.add(categoryList.get(j).getItems().get(i).getName() + "-" + categoryList.get(j).getItems().get(i).getID() + "-" + categoryList.get(j).getItems().get(i).getTht() + "\n" + categoryList.get(j).getItems().get(i).getVoorraad() + " op voorraad");

                }
                groups.append(j, group);
            }

            ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
            ExpandableListAdapter adapter = new ExpandableListAdapter(this,
                    groups, this, TasDB);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("Error", "Error", e);
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
            final TextView txtMinimumVoorraad = (TextView) dialog.findViewById(R.id.txtMinimumVoorraad) ;
            txtMinimumVoorraad.setText("" + getMinimumStock());
            txtMinimumVoorraad.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        Toast.makeText(MainActivity.this, "Minimum voorraad aangepast naar: " + txtMinimumVoorraad.getText(), Toast.LENGTH_SHORT).show();
                        setMinimumStock(Integer.parseInt(txtMinimumVoorraad.getText().toString()));

                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("minimumStock", minimumStock);
                        editor.apply();
                        return true;
                    }
                    return false;
                }
            });

            CheckBox setting_expiration = (CheckBox) dialog.findViewById(R.id.setting_expiration);
            setting_expiration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {





                    /*
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // notificationID allows you to update the notification later on.
                    mNotificationManager.notify(0, mBuilder.build());
                    */
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
            final CheckBox checkboxVoorraad = (CheckBox) dialog.findViewById(R.id.voorraadBox);
            final CheckBox checkboxTht = (CheckBox) dialog.findViewById(R.id.thtBox);
            final EditText voorraadText = (EditText) dialog.findViewById(R.id.txtVoorraad);
            Button btnSave = (Button) dialog.findViewById(R.id.save);
            Button btnCancel = (Button) dialog.findViewById(R.id.cancel);

            // Creating adapter for spinner
            ArrayAdapter<Category> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            category.setAdapter(dataAdapter);

            checkboxVoorraad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkboxVoorraad.isChecked())
                    {
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.thtLayout);
                        layout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.thtLayout);
                        layout.setVisibility(View.GONE);
                    }
                }
            });

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
                    long itemId = 0;
                    int type; // 0=geen Voorraad en geen datum, 1=Voorraad, 2=houdbaarheidsdatum, 3=Voorraad en houdbaarheidsdatum
                    if(checkboxVoorraad.isChecked())
                    {
                        if (checkboxTht.isChecked())
                        {
                            type = 3;
                        }
                        else
                        {
                            type = 1;
                        }
                        itemId = TasDB.addItem(editText.getText().toString(), cat.getID(), Integer.parseInt(voorraadText.getText().toString()), type);
                    }
                    else
                    {
                        if (checkboxTht.isChecked())
                        {
                            type = 2;
                        }
                        else
                        {
                            type = 0;
                        }
                        itemId = TasDB.addItem(editText.getText().toString(), cat.getID(), type);
                    }

                    Toast.makeText(MainActivity.this, editText.getText().toString() + " toegevoegd aan categorie: " + cat.getName(), Toast.LENGTH_LONG).show();

                    dialog.dismiss();

                    if (checkboxTht.isChecked())
                    {
                        datePicker("" + itemId);
                    }
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

    public void datePicker(String id){

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.itemId = id;
        fragment.activity = this;
        fragment.show(getFragmentManager(), id);
    }

    public void checkbox_checked(View view) {
        if(((CheckBox)view).isChecked())
        {
            counter++;
        }
        else if(!((CheckBox)view).isChecked())
        {
            counter--;
        }
        if(counter == counterAmount)
        {
            Toast.makeText(MainActivity.this, "Lijst klaar", Toast.LENGTH_SHORT).show();
        }
    }
}
