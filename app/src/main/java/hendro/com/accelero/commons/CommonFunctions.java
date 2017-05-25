package hendro.com.accelero.commons;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Hendro E. Prabowo on 2/5/2017.
 */

public class CommonFunctions {
    Activity activity;
    public CommonFunctions(Activity activity){
        this.activity = activity;
    }

    public int dpToPx(int dp) {
        Resources r = activity.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
