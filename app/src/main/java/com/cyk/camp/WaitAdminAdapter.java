package com.cyk.camp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WaitAdminAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private String[] list;

    static class ViewHolder{
        TextView name;
    }
    public WaitAdminAdapter(String[] str, LayoutInflater inflater){
        this.list = str;
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
        WaitAdminAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new WaitAdminAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.wait_admin_list_item, container, false);
            holder.name = convertView.findViewById(R.id.tv_wait_admin_team_name);
            convertView.setTag(holder);
        }
        else {
            holder = (WaitAdminAdapter.ViewHolder) convertView.getTag();
        }

        holder.name.setText(list[position]);
        return convertView;
    }


}
