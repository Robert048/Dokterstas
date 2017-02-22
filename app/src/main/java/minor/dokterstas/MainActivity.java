package minor.dokterstas;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
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
import static minor.dokterstas.R.id.txtVoorraad;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper TasDB;
    private List<Category> categoryList = new ArrayList<>();
    private static int minimumStock = 5;
    private static int days = 1;
    private static Time alarmTime = new Time(8, 30, 0);
    private static boolean alarmUsed = false;
    private static boolean voorraadUsed = false;
    private static boolean thtUsed = false;
    private int counterAmount;
    AlarmReceiver alarm = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        JodaTimeAndroid.init(this);
        //SharedPreferences initialize for local storage
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        minimumStock = sharedPref.getInt("minimumStock", minimumStock);
        days = sharedPref.getInt("days", days);
        String time = sharedPref.getString("alarmTime", "8:30:00");
        String[] timeArray = time.split(":");
        alarmTime = new Time(Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]), Integer.parseInt(timeArray[2]));

        alarmUsed = sharedPref.getBoolean("alarmUsed", alarmUsed);
        if (alarmUsed) {
            alarm.setAlarm(this);
        }

        voorraadUsed = sharedPref.getBoolean("voorraadUsed", voorraadUsed);
        thtUsed = sharedPref.getBoolean("thtUsed", thtUsed);

        //make checklist
        createList();
        //give startup message
        setNotifications();

        Cursor c = TasDB.countAllItems();
        int column1 = c.getColumnIndex("count(*)");
        while (c.moveToNext()) {
            counterAmount = c.getInt(column1);
        }
/*
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9987402400398249/6683581213");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        */
    }

    private void setNotifications() {
        try {
            /*retrieve data from database */
            TasDB = new DatabaseHelper(this);
            //Check Stock and expiration date per item
            Cursor c = TasDB.getAllItems();
            int Column1 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_NAME);
            int Column2 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_STOCK);
            int Column3 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_EXPIRATION);
            int Column4 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_TYPE);
            String namen = "";
            String voorraden = "";
            String namen2 = "";
            String expirationDateText = "";

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.stock_message);
            dialog.setTitle("Voorraad meldingen");
            LinearLayout voorraadLayout = (LinearLayout) dialog.findViewById(R.id.voorraadLayout);
            ViewGroup.LayoutParams params = voorraadLayout.getLayoutParams();
            LinearLayout thtLayout = (LinearLayout) dialog.findViewById(R.id.thtLayout);
            ViewGroup.LayoutParams params2 = voorraadLayout.getLayoutParams();

            int height = 50;
            int height2 = 50;
            //loop thru database cursor
            while (c.moveToNext()) {
                String Name = c.getString(Column1);
                int Stock = c.getInt(Column2);
                long date = c.getLong(Column3);
                int type = c.getInt(Column4);

                DateTime dt = new DateTime();
                dt = dt.withMillis(date);

                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
                String dateText = dt.toString(dateTimeFormatter);

                Date expirationDate = dt.toDate();
                if (minimumStock >= Stock && (type == 1 || type == 3)) {
                    if (namen.equals("")) {
                        namen = Name + "\n";
                        voorraden = Stock + "\n";
                    } else {
                        namen = namen + Name + "\n";
                        voorraden = voorraden + Stock + "\n";
                        if (height <= 300) height = height + 50;
                    }
                }
                Calendar calendar = new GregorianCalendar();

                //get date
                long daysBefore = days * 86400000 + 43200000;
                DateTime dateTime = new DateTime(calendar.getTime());
                long pickedMilis = dateTime.getMillis();
                long correctMilis = pickedMilis + daysBefore;

                DateTime correctDate = new DateTime();
                correctDate = correctDate.withMillis(correctMilis);

                  if (expirationDate.before(correctDate.toDate()) && (type == 2 || type == 3)) {
                    if (namen2.equals("")) {
                        namen2 = Name + "\n";
                        expirationDateText = dateText + "\n";
                    } else {
                        namen2 = namen2 + Name + "\n";
                        expirationDateText = expirationDateText + dateText + "\n";
                        if (height2 <= 300) height2 = height2 + 50;
                    }
                }

            }
            //give layouts the correct height
            params.height = height;
            voorraadLayout.setLayoutParams(params);
            params2.height = height2;
            thtLayout.setLayoutParams(params2);

            //hide layouts that aren't used
            if (voorraadUsed) {
                TextView txtName = (TextView) dialog.findViewById(R.id.txtName);
                TextView txtStock = (TextView) dialog.findViewById(R.id.txtStock);
                txtName.setText(namen);
                txtStock.setText(voorraden);
            } else {
                TextView voorraadTitel = (TextView) dialog.findViewById(R.id.voorraadTitel);
                voorraadTitel.setVisibility(View.GONE);
                voorraadLayout.setVisibility(View.GONE);
            }
            if (thtUsed) {
                TextView txtName2 = (TextView) dialog.findViewById(R.id.txtName2);
                TextView txtDate = (TextView) dialog.findViewById(R.id.txtDate);
                txtName2.setText(namen2);
                txtDate.setText(expirationDateText);
            } else {
                TextView thtTitel = (TextView) dialog.findViewById(R.id.thtTitel);
                thtTitel.setVisibility(View.GONE);
                thtLayout.setVisibility(View.GONE);
            }
            if (voorraadUsed || thtUsed) {
                dialog.show();
            }
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        }
    }

    public int getCounterAmount() {
        return counterAmount;
    }

    public void decreaseCounterAmount() {
        counterAmount--;
    }

    public static int getMinimumStock() {
        return minimumStock;
    }

    public static Time getAlarmTime() {
        return alarmTime;
    }

    public void setMinimumStock(int Stock) {
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

                int Column1 = c.getColumnIndex(DatabaseHelper.COLUMN_CATEGORIES_ID);
                int Column2 = c.getColumnIndex(DatabaseHelper.COLUMN_CATEGORIES_NAME);
                while (c.moveToNext()) {
                    int ID = c.getInt(Column1);
                    String Name = c.getString(Column2);
                    Category category = new Category(ID, Name);
                    categoryList.add(category);
                }
            }
            {
                Cursor c = TasDB.getAllDataFromTable(2);

                int Column1 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_ID);
                int Column2 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_NAME);
                int Column3 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_EXPIRATION);
                int Column4 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_STOCK);
                int Column5 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_CATEGORIES_ID);
                int Column6 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_TYPE);
                int Column7 = c.getColumnIndex(DatabaseHelper.COLUMN_ITEMS_VOLUME);
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

        //'Instellingen' button in menu
        if (id == R.id.Menu_Settings) {
            final Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.settings);
            dialog.setTitle("settings");
            final TextView txtMinimumVoorraad = (TextView) dialog.findViewById(R.id.txtMinimumVoorraad);
            final TextView txtDatum = (TextView) dialog.findViewById(R.id.txtDatum);
            final TextView txtTime = (TextView) dialog.findViewById(R.id.txtTime);
            final Spinner category = (Spinner) dialog.findViewById(spinner);
            final LinearLayout voorraadLayout = (LinearLayout) dialog.findViewById(R.id.voorraadLayout);
            final LinearLayout thtLayout = (LinearLayout) dialog.findViewById(R.id.thtLayout);
            final LinearLayout checklistLayout = (LinearLayout) dialog.findViewById(R.id.checklistLayout);
            Switch switchVoorraad = (Switch) dialog.findViewById(R.id.switchVoorraad);
            Switch switchDatum = (Switch) dialog.findViewById(R.id.switchDatum);
            Switch switchChecklist = (Switch) dialog.findViewById(R.id.switchChecklist);

            if (voorraadUsed) {
                switchVoorraad.setChecked(true);
                voorraadLayout.setVisibility(View.VISIBLE);
            }
            switchVoorraad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        voorraadLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Lage voorraad melding aangezet", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("voorraadUsed", true);
                        editor.apply();
                        voorraadUsed = sharedPref.getBoolean("voorraadUsed", voorraadUsed);
                    } else {
                        voorraadLayout.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Lage voorraad melding uitgezet", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("voorraadUsed", false);
                        editor.apply();
                        voorraadUsed = sharedPref.getBoolean("voorraadUsed", voorraadUsed);
                    }
                }
            });

            if (thtUsed) {
                switchDatum.setChecked(true);
                thtLayout.setVisibility(View.VISIBLE);
            }
            switchDatum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        thtLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Houdbaarheidsdatum melding aangezet", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("thtUsed", true);
                        editor.apply();
                        thtUsed = sharedPref.getBoolean("thtUsed", thtUsed);
                    } else {
                        thtLayout.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Houdbaarheidsdatum melding uitgezet", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("thtUsed", false);
                        editor.apply();
                        thtUsed = sharedPref.getBoolean("thtUsed", thtUsed);
                    }
                }
            });

            if (alarmUsed) {
                switchChecklist.setChecked(true);
                checklistLayout.setVisibility(View.VISIBLE);
                alarm.setAlarm(MainActivity.this);
            }
            switchChecklist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checklistLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Alarm dagelijks om: " + alarmTime, Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("alarmUsed", true);
                        editor.apply();
                        alarmUsed = sharedPref.getBoolean("alarmUsed", thtUsed);
                        alarm.setAlarm(MainActivity.this);
                    } else {
                        checklistLayout.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Dagelijks alarm uitgezet", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("alarmUsed", false);
                        editor.apply();
                        alarmUsed = sharedPref.getBoolean("alarmUsed", thtUsed);
                        alarm.cancelAlarm(MainActivity.this);
                    }
                }
            });

            Button btnDelete = (Button) dialog.findViewById(R.id.delete);
            // Creating adapter for spinner
            ArrayAdapter<Category> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            category.setAdapter(dataAdapter);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(MainActivity.this);

                    dialog.setContentView(R.layout.confirmation);
                    dialog.setTitle("Confirmation");

                    Button btnYes = (Button) dialog.findViewById(R.id.yes);
                    Button btnNo = (Button) dialog.findViewById(R.id.no);

                    dialog.show();

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Category cat = (Category) category.getSelectedItem();
                            TasDB.deleteCategory(String.valueOf(cat.getID()));

                            dialog.dismiss();
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

                            //builder.setMessage(item.getName() + " verwijderd uit lijst.");
                            builder.setTitle("Verwijderd");

                            createList();

                            android.app.AlertDialog dialog4 = builder.create();
                            dialog4.show();

                            dialog.dismiss();
                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });

            txtMinimumVoorraad.setText(String.valueOf(getMinimumStock()));
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

            txtDatum.setText(String.valueOf(days));
            txtDatum.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        Toast.makeText(MainActivity.this, "Aantal dagen aangepast naar: " + txtDatum.getText(), Toast.LENGTH_SHORT).show();
                        days = Integer.parseInt(txtDatum.getText().toString());

                        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("days", days);
                        editor.apply();
                        return true;
                    }
                    return false;
                }
            });

            txtTime.setText(String.valueOf(alarmTime));
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

        //'Herstel gegevens' button in menu
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

        //'Categorie toevoegen' button in menu
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
                    if (!editText.getText().toString().isEmpty()) {
                        TasDB.addCategory(editText.getText().toString());

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setMessage(editText.getText().toString() + " categorie toegevoegd.");
                        builder.setTitle("Nieuwe categorie");

                        AlertDialog dialog2 = builder.create();
                        dialog2.show();
                        dialog.dismiss();

                        createList();
                    } else {
                        editText.setHintTextColor(Color.RED);
                    }
                }
            });

            dialog.show();
            return true;
        }

        //'Item toevoegen' button in menu
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
                    if (checkboxVoorraad.isChecked()) {
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.voorraadLayout);
                        layout.setVisibility(View.VISIBLE);
                    } else {
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.voorraadLayout);
                        layout.setVisibility(View.GONE);
                    }
                }
            });

            checkboxTht.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkboxTht.isChecked()) {
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.thtLayout);
                        layout.setVisibility(View.VISIBLE);
                    } else {
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.thtLayout);
                        layout.setVisibility(View.GONE);
                    }
                }
            });

            checkboxvolume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkboxvolume.isChecked()) {
                        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.volumeLayout);
                        layout.setVisibility(View.VISIBLE);
                    } else {
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
                    if (!editText.getText().toString().isEmpty()) {
                        Category cat = (Category) category.getSelectedItem();
                        int volume = Integer.parseInt(volumeText.getText().toString());

                        if (checkboxVoorraad.isChecked()) {
                            if(Integer.parseInt(voorraadText.getText().toString()) >= 0) {
                                if (checkboxTht.isChecked()) {
                                    DateTime dateTime = new DateTime();
                                    dateTime = dateTime.withDate(dialog.year, dialog.month + 1, dialog.day);
                                    long milis = dateTime.getMillis();
                                    if (checkboxvolume.isChecked()) {
                                        TasDB.addItemStockDateVolume(editText.getText().toString(), cat.getID(), Integer.parseInt(voorraadText.getText().toString()), milis, volume, 7);
                                    } else {
                                        TasDB.addItemStockDate(editText.getText().toString(), cat.getID(), Integer.parseInt(voorraadText.getText().toString()), milis, 3);
                                    }
                                } else {
                                    if (checkboxvolume.isChecked()) {
                                        TasDB.addItemStockVolume(editText.getText().toString(), cat.getID(), Integer.parseInt(voorraadText.getText().toString()), volume, 5);
                                    } else {
                                        TasDB.addItemStock(editText.getText().toString(), cat.getID(), Integer.parseInt(voorraadText.getText().toString()), 1);
                                    }
                                }
                            }
                            else
                            {
                                voorraadText.setHintTextColor(Color.RED);
                            }
                        } else {
                            if (checkboxTht.isChecked()) {
                                DateTime dateTime = new DateTime();
                                dateTime = dateTime.withDate(dialog.year, dialog.month + 1, dialog.day);
                                long milis = dateTime.getMillis();
                                if (checkboxvolume.isChecked()) {
                                    TasDB.addItemDateVolume(editText.getText().toString(), cat.getID(), milis, volume, 6);
                                } else {
                                    TasDB.addItemDate(editText.getText().toString(), cat.getID(), milis, 2);
                                }
                            } else {
                                if (checkboxvolume.isChecked()) {
                                    TasDB.addItemVolume(editText.getText().toString(), cat.getID(), volume, 4);
                                } else {
                                    TasDB.addItemName(editText.getText().toString(), cat.getID(), 0);
                                }
                            }
                        }

                        counterAmount++;
                        Toast.makeText(MainActivity.this, editText.getText().toString() + " toegevoegd aan categorie: " + cat.getName(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        createList();
                    } else {
                        editText.setHintTextColor(Color.RED);
                    }
                }
            });

            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void datePicker(CustomDialog dialog) {

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
