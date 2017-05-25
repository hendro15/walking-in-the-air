package hendro.com.accelero.commons;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Hendro E. Prabowo on 02/04/2017.
 */

public class Dialog {
    Activity activity;

    public Dialog(Activity activity){
        this.activity = activity;
    }

    public void exitDialog(Context mContext){
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("EXIT");
        builder.setMessage("Are you sure ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
