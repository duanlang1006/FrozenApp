package com.lang.frozenapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import com.lang.frozenapp.util.ImageUtils;
import com.lang.frozenapp.util.SystemUiHider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FrozenActivity extends Activity implements HideAppAdapter.ItemClickCallBack{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private static final String TAG = "Shlyfrozenapp";

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    //private SystemUiHider mSystemUiHider;
    
    private GridView mGridView;
    private HideAppAdapter mHideAppAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setTranslucentFlag();
        setContentView(R.layout.activity_main);
        //final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        //mSystemUiHider = SystemUiHider.getInstance(this, contentView,
        //        HIDER_FLAGS);
        //mSystemUiHider.setup();
       // mSystemUiHider
        //        .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
        //            int mControlsHeight;
         //           int mShortAnimTime;

//                    @Override
//                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//                    public void onVisibilityChange(boolean visible) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//                            // If the ViewPropertyAnimator API is available
//                            // (Honeycomb MR2 and later), use it to animate the
//                            // in-layout UI controls at the bottom of the
//                            // screen.
//                            if (mControlsHeight == 0) {
//                                mControlsHeight = controlsView.getHeight();
//                            }
//                            if (mShortAnimTime == 0) {
//                                mShortAnimTime = getResources().getInteger(
//                                        android.R.integer.config_shortAnimTime);
//                            }
//                            controlsView
//                                    .animate()
//                                    .translationY(visible ? 0 : mControlsHeight)
//                                    .setDuration(mShortAnimTime);
//                        } else {
//                            // If the ViewPropertyAnimator APIs aren't
//                            // available, simply show or hide the in-layout UI
//                            // controls.
//                            controlsView.setVisibility(visible ? View.VISIBLE
//                                    : View.GONE);
//                        }
//
//                        if (visible && AUTO_HIDE) {
//                            // Schedule a hide().
//                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
//                        }
//                    }
//                });

        // Set up the user interaction to manually show or hide the system UI.
//        findViewById(R.id.fullscreen_base).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (TOGGLE_ON_CLICK) {
//                    mSystemUiHider.toggle();
//                } else {
//                    mSystemUiHider.show();
//                }
//            }
//        });
        
        mGridView = (GridView)findViewById(R.id.hideapp_content);
        mHideAppAdapter = new HideAppAdapter(this);
        mHideAppAdapter.setItemClickCallBack(this);
        mGridView.setAdapter(mHideAppAdapter);
        //mGridView.setOnItemClickListener(mOnItemClickListener);
        
        loadApps();
    }
    
    private void setTranslucentFlag () {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.BASE) {
        // TODO(sansid): use the APIs directly when compiling against L sdk.
        // Currently we use reflection to access the flags and the API to set the transparency
        // on the System bars.
            try {
                getWindow().getAttributes().systemUiVisibility |=
                        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                Field drawsSysBackgroundsField = WindowManager.LayoutParams.class.getField(
                        "FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS");
                getWindow().addFlags(drawsSysBackgroundsField.getInt(null));

                Method setStatusBarColorMethod =
                        Window.class.getDeclaredMethod("setStatusBarColor", int.class);
                Method setNavigationBarColorMethod =
                        Window.class.getDeclaredMethod("setNavigationBarColor", int.class);
                setStatusBarColorMethod.invoke(getWindow(), Color.TRANSPARENT);
                setNavigationBarColorMethod.invoke(getWindow(), Color.TRANSPARENT);
            } catch (NoSuchFieldException e) {
                Log.w(TAG, "NoSuchFieldException while setting up transparent bars");
            } catch (NoSuchMethodException ex) {
                Log.w(TAG, "NoSuchMethodException while setting up transparent bars");
            } catch (IllegalAccessException e) {
                Log.w(TAG, "IllegalAccessException while setting up transparent bars");
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "IllegalArgumentException while setting up transparent bars");
            } catch (InvocationTargetException e) {
                Log.w(TAG, "InvocationTargetException while setting up transparent bars");
            } finally {}
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
//    OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position,
//                long id) {
//            // TODO Auto-generated method stub
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            
//            AppInfo info = (AppInfo)view.getTag();
//            android.util.Log.d("taurenlog","position:"+position);
//            if(info.isAddAppTag()){
//                Intent i = new Intent();
//                i.setClassName(MainActivity.this, "com.shly.browserapp.AppInfoActivity");
//                MainActivity.this.startActivityForResult(i,0);
//            }
//        }
//    };

//    Handler mHideHandler = new Handler();
//    Runnable mHideRunnable = new Runnable() {
//        @Override
//        public void run() {
//            mSystemUiHider.hide();
//        }
//    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (20 == resultCode) {
            reloadApps();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void reloadApps(){
        mHideAppAdapter.clearAppItem();
        loadApps();
    }
    private void removeItem(AppInfo info){
        mHideAppAdapter.removeAppItem(info);
        mHideAppAdapter.notifyDataSetChanged();
    }
    private AppInfoLoadTask mTask = null;
    private void loadApps() {
        if (mTask != null && mTask.getStatus() != AppInfoLoadTask.Status.FINISHED) {
            mTask.cancel(true);
        }
        mTask = (AppInfoLoadTask) new AppInfoLoadTask().execute();
    }
    private List<AppInfo> mlistAppInfo = new ArrayList<AppInfo>();
    private class AppInfoLoadTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            queryAppInfo();
            return 0;
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            //BrowseAppInfoAdapter browseAppAdapter = new BrowseAppInfoAdapter(getApplicationContext(), mlistAppInfo);
            //listview.setAdapter(browseAppAdapter);
            for(AppInfo info:mlistAppInfo){
                mHideAppAdapter.addAppItem(info);
            }
            AppInfo addApp = new AppInfo();
            addApp.setIsAddAppTag(true);
            mHideAppAdapter.addAppItem(addApp);
            mHideAppAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        private void queryAppInfo() {
            PackageManager pm = getPackageManager();
            //Intent mainIntent = new Intent(Intent.ACTION_MAIN);
            //mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            //List<ResolveInfo> resolveInfos = pm
            //        .queryIntentActivities(mainIntent, 0);
            List<PackageInfo> packages = pm.getInstalledPackages(0);

            //Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));
            if (mlistAppInfo != null) {
                mlistAppInfo.clear();
                for (PackageInfo pkgInfo : packages) {
                    int flags = pkgInfo.applicationInfo.flags;
                    //系统应用不冷藏
                    if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                        continue;
                    }
                    //自己不能冷藏自己
                    if(pkgInfo.applicationInfo.packageName.equals("com.shly.shlyfrozenapp")){
                        continue;
                    }
                    
                    //没有停用的应用
                    if(pkgInfo.applicationInfo.enabled){
                        continue;
                    }
                    String activityName = pkgInfo.applicationInfo.name; 
                    String pkgName = pkgInfo.applicationInfo.packageName;
                    String appLabel = pkgInfo.applicationInfo.loadLabel(pm).toString().trim();
                    Drawable icon = pkgInfo.applicationInfo.loadIcon(pm);
                    icon = ImageUtils.getFrozenIconBitmap(FrozenActivity.this, icon);

                    //Intent launchIntent = new Intent();
                    //launchIntent.setComponent(new ComponentName(pkgName,
                     //       activityName));

                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppLabel(appLabel);
                    appInfo.setPkgName(pkgName);
                    appInfo.setActivityName(activityName);
                    appInfo.setAppIcon(icon);
                    appInfo.saveTime(FrozenActivity.this);
                    //appInfo.setIntent(launchIntent);
                    mlistAppInfo.add(appInfo);
                }
                Collections.sort(mlistAppInfo,new AppInfo.AppInfoTimeComparator());
            }
        }
    }
    @Override
    public void onItemClick(AppInfo info) {
        if (mTask != null && mTask.getStatus() != AppInfoLoadTask.Status.FINISHED){
            return;
        }
        // TODO Auto-generated method stub
        if(info.isAddAppTag()){
            Intent i = new Intent();
            i.setClassName(FrozenActivity.this, "com.shly.browserapp.AppInfoActivity");
            FrozenActivity.this.startActivityForResult(i,0);
        }
        else{
            getPackageManager().setApplicationEnabledSetting(
                    info.getPkgName(), 
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                    0);
            info.cleanSaveTime(this);
            //reloadApps();
            removeItem(info);
        }
    }
    
    
}
