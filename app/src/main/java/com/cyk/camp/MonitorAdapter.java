package com.cyk.camp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MonitorAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Team[] list;

    static class ViewHolder{
        TextView name;
        TextView current_quest;
        TextView quests_completed;
    }

    public MonitorAdapter(Team[] t, LayoutInflater inflater){
        this.list = t;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return list.length;
    }
    @Override
    public Object getItem(int position){
        return list[position];
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup container){
        MonitorAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new MonitorAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.monitor_list_item, container, false);
            holder.name = convertView.findViewById(R.id.tv_monitor_team_name);
            holder.current_quest = convertView.findViewById(R.id.tv_monitor_current_quest);
            holder.quests_completed = convertView.findViewById(R.id.tv_monitor_quests_completed);
            convertView.setTag(holder);
        }
        else{
            holder = (MonitorAdapter.ViewHolder) convertView.getTag();
        }

        String cur = "正在進行第 " + (list[position].current_quest + 1) + " 關";
        String com = "已完成關卡數： " + list[position].quest_number;

        holder.name.setText(list[position].name);
        holder.current_quest.setText(cur);
        holder.quests_completed.setText(com);

        return convertView;
    }
}
