package com.cyk.camp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PopDialog extends DialogFragment {

    public View view;
    public String question;

    public interface MyDialogFragmentListener {
        public void onReturnValue(String foo);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.quest_dialog, null);

        Bundle mArgs = getArguments();
        question = mArgs.getString("question");

        Log.d("tag_question", question);

        TextView tv = view.findViewById(R.id.tv_quest_dialog_question);
        tv.setText(question);


        builder.setView(view)
            .setPositiveButton("答題", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    EditText editText = (EditText) getDialog().findViewById(R.id.et_quest_dialog_answer);
                    if (!editText.getText().toString().matches("")) {
                        //Log.d("tag_dialog_answer", answer);

                        MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                        activity.onReturnValue(editText.getText().toString());
                    }
                    else{
                        Toast toast = Toast.makeText(getActivity(), "請輸入答案", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 400);
                        toast.show();
                    }


                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    PopDialog.this.getDialog().cancel();
                }
            });

        // 3. Get the AlertDialog from create()
        return builder.create();
    }
}

