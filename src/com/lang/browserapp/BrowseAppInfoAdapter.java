package com.lang.browserapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lang.frozenapp.AppInfo;
import com.lang.frozenapp.R;

import java.util.ArrayList;
import java.util.List;

public class BrowseAppInfoAdapter extends BaseAdapter {
    
    public static final Integer APP_NOSELECT = Integer.valueOf(0);
    public static final Integer APP_SELECT = Integer.valueOf(1);
    
    private List<AppInfo> mlistAppInfo = null;
    
    LayoutInflater infater = null;
    private List<String> mSelectPackages = new ArrayList<String>();
    public void addSelectPkgName(String name){
    	mSelectPackages.add(name);
    }
    public void removeSelectPkgName(String name){
    	mSelectPackages.remove(name);
    }
    public List<String> getSelectList(){
    	return mSelectPackages;
    }
    
    public BrowseAppInfoAdapter(Context context,  List<AppInfo> apps) {
        infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mlistAppInfo = apps ;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        System.out.println("size" + mlistAppInfo.size());
        return mlistAppInfo.size();
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mlistAppInfo.get(position);
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public View getView(int position, View convertview, ViewGroup arg2) {
        System.out.println("getView at " + position);
        View view = null;
        ViewHolder holder = null;
        if (convertview == null || convertview.getTag() == null) {
            view = infater.inflate(R.layout.browse_app_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } 
        else{
            view = convertview ;
            holder = (ViewHolder) convertview.getTag() ;
        }
        holder.setPosition(position);
        AppInfo appInfo = (AppInfo) getItem(position);
        holder.appIcon.setImageDrawable(appInfo.getAppIcon());
        holder.tvAppLabel.setText(appInfo.getAppLabel());
        ViewGroup vg = (ViewGroup) view;
        ImageView check = (ImageView)vg.findViewById(R.id.check);
        check.setVisibility(View.VISIBLE);
        if(!mSelectPackages.contains(appInfo.getPkgName())){
            //check.setVisibility(View.INVISIBLE);
            check.setImageResource(R.drawable.check_no);
            check.setTag(APP_NOSELECT);
        } else {
            //check.setVisibility(View.VISIBLE);
            check.setImageResource(R.drawable.check_yes);
            check.setTag(APP_SELECT);
        }
        return view;
    }

    class ViewHolder {
        ImageView appIcon;
        TextView tvAppLabel;
        int position;

        public ViewHolder(View view) {
            this.appIcon = (ImageView) view.findViewById(R.id.imgApp);
            this.tvAppLabel = (TextView) view.findViewById(R.id.tvAppLabel);
            view.setOnClickListener(mItemOnClickListener);
        }
        
        public void setPosition(int p){
            position = p;
        }
        public int getsetPosition(){
            return position;
        }
    }
    
    View.OnClickListener mItemOnClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(null != mItemClickCallBack){
                ViewHolder holder = (ViewHolder) v.getTag() ;
                mItemClickCallBack.onItemClick(v, holder.getsetPosition());
            }
        }
    };
    
    public interface ItemClickCallBack{
        public void onItemClick(View view, int position);
    }
    private ItemClickCallBack mItemClickCallBack ;
    public void setItemClickCallBack(ItemClickCallBack back){
        mItemClickCallBack = back;
    }
}