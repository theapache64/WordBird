package com.shifz.wordbird.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.shifz.wordbird.R;

/**
 * Created by Shifar Shifz on 10/24/2015 10:05 AM.
 */
public class DialogHelper {

    private final Context context;

    public DialogHelper(final Context context) {
        this.context = context;
    }

    public void showInputDialog(@StringRes int title, @StringRes int message, final DialogInputCallback dialogInputCallback) {

        final AlertDialog dialog = new AlertDialog.Builder(this.context)
                .setTitle(title)
                .setMessage(message)
                .setView(R.layout.dialog_input_layout)
                .setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final EditText etDialogInput = ((EditText) ((AlertDialog) dialog).findViewById(R.id.etDialogInput));
                        dialogInputCallback.onInput(etDialogInput.getText().toString());
                    }
                })
                .create();

        dialog.show();
    }


    public interface DialogInputCallback {
        void onInput(String input);
    }
}
