package minor.dokterstas;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import java.sql.Time;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import minor.dokterstas.database.DatabaseHelper;
import static minor.dokterstas.R.id.spinner;

public class MainActivity extends AppCompatActivity{

    DatabaseHelper TasDB;
    private List<Category> categoryList = new ArrayList<>();
    private static int minimumStock = 5;
    private static Time alarmTime = new Time(8,30,0);
    private static boolean alarmUsed = false;
    private int counterAmount;
    AlarmReceiver alarm = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        JodaTimeAndroid.init(this);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        minimumStock = sharedPref.getInt("minimumStock", minimumStock);
        String time = sharedPref.getString("alarmTime", "8:30:00");
        String[] timeArray = time.split(":");
        alarmTime = new Time(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]) ,Integer.parseInt(timeArray[2]));
        alarmUsed = sharedPref.getBoolean("alarmUsed", alarmUsed);
        if(alarmUsed)
        {
            alarm.setAlarm(this);
        }
        createList();
        setNotifications();

        Cursor c = TasDB.countAllItems();
        int column1 = c.getColumnIndex("count(*)");
        while (c.moveToNext()) {
            counterAmount = c.getInt(column1);
        }
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

                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");

                //String dateText = String.valueOf(dt.dayOfMonth().get()) + "/" + String.valueOf(dt.monthOfYear().get()) + "/" +  String.valueOf(dt.year().get());
                String dateText = dt.toString(dateTimeFormatter);

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

    public int getCounterAmount()
    {
        return counterAmount;
    }

    public void decreaseCounterAmount()
    {
        counterAmount--;
    }

    public static int getMinimumStock()
    {
        return minimumStock;
    }

    public static Time getAlarmTime()
    {
        return alarmTime;
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
                int Column7 = c.getColumnIndex(TasDB.COLUMN_ITEMS_VOLUME);
                while (c.moveToNext()) {
                    int ID = c.getInt(Column1);
                    String Name = c.getString(Column2);
                    long tht = c.getLong(Column3);
                    int voorraad = c.getInt(Column4);
                    int CategoryID = c.getInt(Column5);
                    int type = c.getInt(Column6);
                    int volume = c.getInt(Column7);
                    DateTime dt = new DateTime();
                    dt = dt.withMillis(tht);

                    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
                    String dateText = dt.toString(dateTimeFormatter);

                    Item item = new Item(ID, Name, dateText, voorraad, volume, type);
                    Category category = categoryList.get(CategoryID - 1);
                    category.addItem(item);
                }
            }



            ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
            ExpandableListAdapter adapter = new ExpandableListAdapter(this,
                    categoryList, this, TasDB);
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

            CheckBox setting_check = (CheckBox) dialog.findViewById(R.id.setting_check);
            if(alarmUsed)
            {
                setting_check.setChecked(true);
                alarm.setAlarm(MainActivity.this);
            }
            setting_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(((CheckBox)view).isChecked()) {
                        Toast.makeText(MainActivity.this, "Alarm dagelijks om: " + alarmTime, Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("alarmUsed", true);
                        editor.apply();
                        alarm.setAlarm(MainActivity.this);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Dagelijks alarm uitgezet", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("alarmUsed", false);
                        editor.apply();
                        alarm.cancelAlarm(MainActivity.this);
                    }
                }
            });

            final TextView txtTime = (TextView) dialog.findViewById(R.id.txtTime);
            txtTime.setText("" + alarmTime);
            txtTime.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        Toast.makeText(MainActivity.this, "Notificatie tijdstip aangepast naar: " + txtTime.getText(), Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("AlarmTime", txtTime.getText().toString());
                        editor.apply();
                        //cancel old alarm
                        alarm.cancelAlarm(MainActivity.this);
                        alarm.setAlarm(MainActivity.this);
                        return true;
                    }
                    return false;
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
            final CustomDialog dialog = new CustomDialog(this);
            dialog.setContentView(R.layout.create_item);
            dialog.setTitle("Item toevoegen");

            final Spinner category = (Spinner) dialog.findViewById(spinner);
            final EditText editText = (EditText) dialog.findViewById(R.id.editText);
            final CheckBox checkboxVoorraad = (CheckBox) dialog.findViewById(R.id.voorraadBox);
            final CheckBox checkboxTht = (CheckBox) dialog.findViewById(R.id.thtBox);
            final CheckBox checkboxvolume = (CheckBox) dialog.findViewById(R.id.volumeBox);
            final EditText voorraadText = (EditText) dialog.findViewById(R.id.txtVoorraad);
            final EditText volumeText = (EditText) dialog.findViewById(R.id.txtVolume);
            Button btnDate = (Button) dialog.findViewById(R.id.createDate);
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
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.voorraadLayout);
                        layout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.voorraadLayout);
                        layout.setVisibility(View.GONE);
                    }
                }
            });

            checkboxTht.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkboxTht.isChecked())
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

            checkboxvolume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkboxvolume.isChecked())
                    {
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.volumeLayout);
                        layout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.volumeLayout);
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

            btnDate.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               (MainActivity.this).datePicker(dialog);
                                           }
                                       });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Category cat = (Category) category.getSelectedItem();
                    int volume = Integer.parseInt(volumeText.getText().toString());

                    if(checkboxVoorraad.isChecked())
                    {
                        if (checkboxTht.isChecked())
                        {
                            DateTime dateTime = new DateTime();
                            dateTime = dateTime.withDate(dialog.year, dialog.month + 1, dialog.day);
                            long milis = dateTime.getMillis();
                            if(checkboxvolume.isChecked())
                            {
                                TasDB.addItemStockDateVolume(editText.getText().toString(), cat.getID(), Integer.parseInt(voorraadText.getText().toString()), milis, volume, 7);
                            }
                            else
                            {
                                TasDB.addItemStockDate(editText.getText().toString(), cat.getID(), Integer.parseInt(voorraadText.getText().toString()), milis, 3);
                            }
                        }
                        else
                        {
                            if(checkboxvolume.isChecked())
                            {
                                TasDB.addItemStockVolume(editText.getText().toString(), cat.getID(), Integer.parseInt(voorraadText.getText().toString()), volume, 5);
                            }
                            else
                            {
                                TasDB.addItemStock(editText.getText().toString(), cat.getID(), Integer.parseInt(voorraadText.getText().toString()), 1);
                            }
                        }
                    }
                    else
                    {
                        if (checkboxTht.isChecked())
                        {
                            DateTime dateTime = new DateTime();
                            dateTime = dateTime.withDate(dialog.year, dialog.month + 1, dialog.day);
                            long milis = dateTime.getMillis();
                            if (checkboxvolume.isChecked())
                            {
                                TasDB.addItemDateVolume(editText.getText().toString(), cat.getID(), milis, volume, 6);
                            }
                            else
                            {
                                TasDB.addItemDate(editText.getText().toString(), cat.getID(), milis, 2);
                            }
                        }
                        else
                        {
                            if(checkboxvolume.isChecked())
                            {
                                TasDB.addItemVolume(editText.getText().toString(), cat.getID(), volume, 4);
                            }
                            else
                            {
                                TasDB.addItemName(editText.getText().toString(), cat.getID(), 0);
                            }
                        }
                    }

                    counterAmount++;
                    Toast.makeText(MainActivity.this, editText.getText().toString() + " toegevoegd aan categorie: " + cat.getName(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    createList();
                }
            });

            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void datePicker(CustomDialog dialog){

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.activity = this;
        fragment.dialog = dialog;
        fragment.show(getFragmentManager(), "");
    }

    public void datePicker(String id, CustomDialog dialog) {

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.itemId = id;
        fragment.activity = this;
        fragment.dialog = dialog;
        fragment.show(getFragmentManager(), id);
    }
}
