package minor.dokterstas;

/**
 * Created by Hakob on 1-3-2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import java.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import minor.dokterstas.database.DatabaseHelper;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public CustomDialog dialog;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

        String minutes = "" + i1;
        String hours = "" + i;

        if(10 > i1)
        {
            minutes = "0" + i1;
        }

        if(10 > i)
        {
            hours = "0" + i;
        }

        String dateText =  hours + ":" + minutes + ":00";
        TextView txtTime = (TextView) dialog.findViewById(R.id.txtTime);
        txtTime.setText(dateText);

        dialog.hour = i;
        dialog.minute = i1;



    }
}
