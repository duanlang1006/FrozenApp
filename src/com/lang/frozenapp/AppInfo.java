package com.lang.frozenapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.util.Comparator;

public class AppInfo {
  
    private String appLabel;
    private Drawable appIcon ;
    private Intent intent ;
    private String pkgName ;
    private String activityName;
    private boolean isAddAppTag = false;
    private long setTime = 0;
    
    public AppInfo(){}
    
    public void setIsAddAppTag(boolean is){
        isAddAppTag = is;
    }
    
    public boolean isAddAppTag(){
        return isAddAppTag;
    }
    
    public String getAppLabel() {
        return appLabel;
    }
    public void setAppLabel(String appName) {
        this.appLabel = appName;
    }
    public Drawable getAppIcon() {
        return appIcon;
    }
    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
    public Intent getIntent() {
        return intent;
    }
    public void setIntent(Intent intent) {
        this.intent = intent;
    }
    public String getPkgName(){
        return pkgName ;
    }
    public void setPkgName(String pkgName){
        this.pkgName=pkgName ;
    }
    public String getActivityName(){
        return activityName ;
    }
    public void setActivityName(String activityName){
        this.activityName=activityName ;
    }
    
    public long saveTime(Context context){
        if(TextUtils.isEmpty(pkgName)){
            return 0;
        }
        SharedPreferences mySharedPreferences= context.getSharedPreferences("AppInfo", 
                Activity.MODE_PRIVATE); 
        setTime = mySharedPreferences.getLong(pkgName, 0);
        if(0 == setTime){
            SharedPreferences.Editor editor = mySharedPreferences.edit(); 
            setTime = System.currentTimeMillis();
            editor.putLong(pkgName,setTime); 
            editor.commit();
        }

        return setTime;
    }
    public void cleanSaveTime(Context context){
        if(TextUtils.isEmpty(pkgName)){
            return;
        }
        SharedPreferences mySharedPreferences= context.getSharedPreferences("AppInfo", 
                Activity.MODE_PRIVATE); 
        SharedPreferences.Editor editor = mySharedPreferences.edit(); 
        editor.putLong(pkgName,0); 
        editor.commit();
    }
    
    public long getSetTime(){
        return setTime;
    }
    
    
    public static class AppInfoTimeComparator
        implements Comparator<AppInfo> {
        public AppInfoTimeComparator() {
        }
        
        public final int compare(AppInfo a, AppInfo b) {
            if(a.getSetTime() == 0){
                return 1;
            }
            else if(a.getSetTime() > b.getSetTime()){
                return 1;
            }
            else{
                return -1;
            }
        }
    }
}
