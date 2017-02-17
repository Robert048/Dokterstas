package minor.dokterstas;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

import minor.dokterstas.database.DatabaseHelper;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    DatabaseHelper TasDB;
    public String itemId;
    public Activity activity;
    public CustomDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        TasDB = new DatabaseHelper(activity);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        DateTime dateTime = new DateTime();
        dateTime = dateTime.withDate(year,month+1,day);
        long milis = dateTime.getMillis();

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        String dateText = dateTime.toString(dateTimeFormatter);

        TextView txtDate = (TextView) dialog.findViewById(R.id.txtDate);
        txtDate.setText(dateText);

        dialog.year = year;
        dialog.month = month;
        dialog.day = day;
    }
}
