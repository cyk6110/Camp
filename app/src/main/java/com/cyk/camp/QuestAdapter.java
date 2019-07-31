package com.cyk.camp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuestAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Quest[] quest_list;
    Context context;

    static class ViewHolder{
        TextView question;
        TextView answer;
        TextView hint;
    }

    public QuestAdapter(Quest[] q, LayoutInflater inflater, Context c){
        this.quest_list = q;
        this.inflater = inflater;
        this.context = c;
    }

    @Override
    public int getCount() {
        return quest_list.length;
    }
    @Override
    public Object getItem(int position){
        return quest_list[position];
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.quest_edit_list_item, container, false);
            holder.question = convertView.findViewById(R.id.tv_quest_edit_question);
            holder.answer = convertView.findViewById(R.id.tv_quest_edit_answer);
            holder.hint = convertView.findViewById(R.id.tv_quest_edit_hint);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        String q = quest_list[position].question;

        if(q.substring(0, 15).equals("multiple_choice")){
            String[] arr = q.split("#");
            String s = context.getResources().getString(R.string.mul_choice, arr[1], arr[2], arr[3], arr[4], arr[5]);
            holder.question.setText(s);
        }
        else {
            holder.question.setText(quest_list[position].question);
        }
        String a = "答案：" + Integer.toString(Integer.valueOf(quest_list[position].answer) + 1);
        holder.answer.setText(a);
        holder.hint.setText(quest_list[position].hint);


        return convertView;
    }
}
