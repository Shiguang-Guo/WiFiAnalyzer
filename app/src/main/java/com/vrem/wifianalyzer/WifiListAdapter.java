package com.vrem.wifianalyzer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class WifiListAdapter extends BaseAdapter {
    private Context context;//上下文对象
    private List<String> dataList;//ListView显示的数据
    public WifiListAdapter(Context context, List<String> dataList){
        this.context=context;
        this.dataList=dataList;
    }
    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //判断是否有缓存
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.wifi_item_listview, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置内容
        viewHolder.contentTv.setText(dataList.get(position));
        return convertView;
    }
    private final class ViewHolder {
        TextView contentTv;//内容
        /**
         * 构造器
         *
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {
            contentTv = (TextView) view.findViewById(R.id.content_tv);
        }
    }
}
