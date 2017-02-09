package minor.dokterstas;

import android.view.View;

/**
 * Created by Hakob on 9-2-2017.
 */

public class LongTappedOnClickListener implements View.OnLongClickListener {

    int itemId;

    public LongTappedOnClickListener(int itemId) {
        this.itemId = itemId;
    }


    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
