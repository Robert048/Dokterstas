package minor.dokterstas;

import android.app.Dialog;
import android.content.Context;

class CustomDialog extends Dialog {

    int year;
    int month;
    int day;
    int hour;
    int minute;

    CustomDialog(Context context) {
        super(context);
    }
}
