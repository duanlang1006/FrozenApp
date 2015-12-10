package com.lang.frozenapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HideAppAdapter extends BaseAdapter {

    Context mContext;
    private List<AppInfo> mlistAppInfo = new ArrayList<AppInfo>();
    private int mIconSize = 96;
    private LayoutInflater mInflater;
    
    public interface ItemClickCallBack{
        public void onItemClick(AppInfo info);
    }
    private ItemClickCallBack mItemClickCallBack;
    public void setItemClickCallBack(ItemClickCallBack back){
        mItemClickCallBack = back;
    }
    
    public void addAppItem(AppInfo info){
        if(!mlistAppInfo.contains(info)){
            mlistAppInfo.add(info);
        }
    }
    public void removeAppItem(AppInfo info){
        mlistAppInfo.remove(info);
    }
    public void clearAppItem(){
        mlistAppInfo.clear();
    }
    
    public HideAppAdapter(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mIconSize = mContext.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mlistAppInfo.size();
    }

    @Override
    public AppInfo getItem(int position) {
        // TODO Auto-generated method stub
        return mlistAppInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        TextView tv = null;
        if(null == convertView){
            tv = (TextView) mInflater.inflate(R.layout.application, parent, false);
            //tv.setClickable(false);
            tv.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AppInfo info = (AppInfo)v.getTag();
                    if(null != mItemClickCallBack){
                        mItemClickCallBack.onItemClick(info);
                    }
                }
            });
        }
        else{
            tv = (TextView)convertView;
        }
        
        AppInfo info = getItem(position);
        
        //最后一个
        if(info.isAddAppTag()){
            Drawable addDrawable = mContext.getResources().getDrawable(R.drawable.icon_add);
            addDrawable.setBounds(0,0,mIconSize,mIconSize);
            tv.setCompoundDrawables(null, addDrawable, null, null);
            tv.setText("");
        }
        else{
            Drawable addDrawable = info.getAppIcon();
            addDrawable.setBounds(0,0,mIconSize,mIconSize);
            tv.setCompoundDrawables(null, addDrawable, null, null);
            tv.setText(info.getAppLabel());
        }
        tv.setTag(getItem(position));
        return tv;
    }

}
