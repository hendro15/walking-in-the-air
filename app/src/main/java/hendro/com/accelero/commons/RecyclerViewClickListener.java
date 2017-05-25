package hendro.com.accelero.commons;

import android.view.View;

/**
 * Created by Hendro E. Prabowo on 2/5/2017.
 */

public interface RecyclerViewClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
