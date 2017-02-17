package minor.dokterstas;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by Hakob on 17-2-2017.
 */

public class CustomDialog extends Dialog {

    public int year;
    public int month;
    public int day;

    public CustomDialog(Context context) {
        super(context);
    }
}
