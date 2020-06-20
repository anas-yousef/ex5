package com.example.ex5;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogHandler extends AppCompatDialogFragment {
    private final static String TITLE = "Add a new phone number";
    private SharedPreferences sharedPreferences;
    private EditText editText;
    Context context;

    public DialogHandler(SharedPreferences sharedPreferences, Context context){
        this.sharedPreferences = sharedPreferences;
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(TITLE);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.popup_dialog_layout,null);

        editText = view.findViewById(R.id.phoneNumber);

        builder.setView(view);

// Set up the buttons
        builder.setPositiveButton("Save Number", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DialogHandler.this.sharedPreferences.edit().putString(LocalSendSmsBroadcastReceiver.PHONE,editText.getText().toString()).apply();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }
}
