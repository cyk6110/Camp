package com.cyk.camp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PopDialog extends DialogFragment {

    public View view;
    public String question;
    public int answer = 0;
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
        final EditText editText = view.findViewById(R.id.et_quest_dialog_answer);
        Spinner sp = view.findViewById(R.id.dialog_spinner_answer);

        // 選擇題
        if(question.length() > 15 && question.substring(0,15).equals("multiple_choice")){


            sp.setVisibility(View.VISIBLE);
            // 設選項
            // 設listener
            ArrayAdapter<CharSequence> adapter_answer = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_answer,
                    android.R.layout.simple_spinner_item);
            adapter_answer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(adapter_answer);

            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    answer = position;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            String[] q = question.split("#");
            String s = getString(R.string.mul_choice, q[1], q[2], q[3], q[4], q[5]);
            tv.setText(s);
        }
        else{
            tv.setText(question);
            editText.setVisibility(View.VISIBLE);
        }
        builder.setView(view)
            .setPositiveButton("答題", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    if(question.length() < 15 || !question.substring(0,15).equals("multiple_choice")) {

                        if (!editText.getText().toString().matches("")) {
                            //Log.d("tag_dialog_answer", answer);

                            MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                            activity.onReturnValue(editText.getText().toString());
                        } else {
                            Toast toast = Toast.makeText(getActivity(), "請輸入答案", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 400);
                            toast.show();
                        }
                    }
                    else {
                        MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                        activity.onReturnValue("multiple_choice#" + String.valueOf(answer));
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

