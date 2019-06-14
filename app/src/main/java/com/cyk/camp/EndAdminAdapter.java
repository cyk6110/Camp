package com.cyk.camp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EndAdminAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Rank[] list;

    static class ViewHolder{
        TextView name;
        TextView time;
    }

    public EndAdminAdapter(Rank[] r, LayoutInflater inflater){
        this.list = r;
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
        EndAdminAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new EndAdminAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.end_admin_list_item, container, false);
            holder.name = convertView.findViewById(R.id.tv_rank_team_name);
            holder.time = convertView.findViewById(R.id.tv_rank_time);
            convertView.setTag(holder);
        }
        else{
            holder = (EndAdminAdapter.ViewHolder) convertView.getTag();
        }

        int seconds = (int) (list[position].time / 1000);
        int minutes = (seconds / 60) % 60;
        int hour = seconds / 3600;
        seconds = seconds % 60;

        holder.name.setText(list[position].name);
        holder.time.setText(String.format("%d:%02d:%02d", hour, minutes, seconds));

        return convertView;
    }
}
