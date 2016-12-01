package de.pixyel.dhbw.pixyel;
import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;


/**
 * Created by Ich on 17.11.2016.
 */
//Klasse um es zu erleichtern ein PopUp zu machen

public class PopUp {
    //dem PopUp wird ein Context und ein Titel und einen Nachricheninhalt (beides in Stringform) übergeben
    public void PopUp(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", //mit einem OK-Button kann das PopUp wieder weggeklickt werden
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}