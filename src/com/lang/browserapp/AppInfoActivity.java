package com.lang.browserapp;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;

import com.lang.frozenapp.AppInfo;
import com.lang.frozenapp.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppInfoActivity extends Activity implements BrowseAppInfoAdapter.ItemClickCallBack {

    private static final String TAG = "AppInfoActivity";

    private GridView listview = null;

    private List<AppInfo> mlistAppInfo = null;

    BrowseAppInfoAdapter mBrowseAppAdapter;
    private AppInfoLoadTask mTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getActionBar().setDisplayHomeAsUpEnabled(true);
//		findViewById(android.R.id.content).setSystemUiVisibility(
//				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setTranslucentFlag();
        setContentView(R.layout.browse_app_list);

        listview = (GridView) findViewById(R.id.listviewApp);
        mlistAppInfo = new ArrayList<AppInfo>();
        loadApps();

        // listview.setOnItemClickListener(this);

        // findViewById(R.id.cancle).setOnClickListener(new
        // View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        //
        // AppInfoActivity.this.finish();
        // }
        // });
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                for (String pkgName : mBrowseAppAdapter.getSelectList()) {
                    getPackageManager()
                            .setApplicationEnabledSetting(
                                    pkgName,
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER,
                                    0);

                }
                Intent data = new Intent();
                setResult(20, data);
                AppInfoActivity.this.finish();
            }
        });
    }

    private void setTranslucentFlag() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.BASE) {
            // TODO(sansid): use the APIs directly when compiling against L sdk.
            // Currently we use reflection to access the flags and the API to
            // set the transparency
            // on the System bars.
            try {
                getWindow().getAttributes().systemUiVisibility |= (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                getWindow()
                        .clearFlags(
                                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                Field drawsSysBackgroundsField = WindowManager.LayoutParams.class
                        .getField("FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS");
                getWindow().addFlags(drawsSysBackgroundsField.getInt(null));

                Method setStatusBarColorMethod = Window.class
                        .getDeclaredMethod("setStatusBarColor", int.class);
                Method setNavigationBarColorMethod = Window.class
                        .getDeclaredMethod("setNavigationBarColor", int.class);
                setStatusBarColorMethod.invoke(getWindow(), Color.TRANSPARENT);
                setNavigationBarColorMethod.invoke(getWindow(),
                        Color.TRANSPARENT);
            } catch (NoSuchFieldException e) {
                Log.w(TAG,
                        "NoSuchFieldException while setting up transparent bars");
            } catch (NoSuchMethodException ex) {
                Log.w(TAG,
                        "NoSuchMethodException while setting up transparent bars");
            } catch (IllegalAccessException e) {
                Log.w(TAG,
                        "IllegalAccessException while setting up transparent bars");
            } catch (IllegalArgumentException e) {
                Log.w(TAG,
                        "IllegalArgumentException while setting up transparent bars");
            } catch (InvocationTargetException e) {
                Log.w(TAG,
                        "InvocationTargetException while setting up transparent bars");
            } finally {
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTask != null
                && mTask.getStatus() != AppInfoLoadTask.Status.FINISHED) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    public void onItemClick(View view, int position) {
        // TODO Auto-generated method stub
        // Intent intent = mlistAppInfo.get(position).getIntent();
        // startActivity(intent);
        // Intent data = new Intent();
        // data.putExtra("packageName",
        // mlistAppInfo.get(position).getPkgName());
        // data.putExtra("activityName",
        // mlistAppInfo.get(position).getActivityName());
        // setResult(20, data);
        ViewGroup vg = (ViewGroup) view;
        ImageView check = (ImageView) vg.findViewById(R.id.check);
        Integer selectTag = (Integer) check.getTag();
        if (selectTag == BrowseAppInfoAdapter.APP_SELECT) {
            mBrowseAppAdapter.removeSelectPkgName(mlistAppInfo.get(position).getPkgName());
            //check.setVisibility(View.INVISIBLE);
            check.setImageResource(R.drawable.check_no);
            check.setTag(BrowseAppInfoAdapter.APP_NOSELECT);
        } else {
            mBrowseAppAdapter.addSelectPkgName(mlistAppInfo.get(position).getPkgName());
            //check.setVisibility(View.VISIBLE);
            check.setImageResource(R.drawable.check_yes);
            check.setTag(BrowseAppInfoAdapter.APP_SELECT);
        }
        // getPackageManager().setApplicationEnabledSetting(
        // mlistAppInfo.get(position).getPkgName(),
        // PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER,
        // 0);
        // finish();
    }

    private void loadApps() {
        if (mTask != null
                && mTask.getStatus() != AppInfoLoadTask.Status.FINISHED) {
            mTask.cancel(true);
        }
        mTask = (AppInfoLoadTask) new AppInfoLoadTask().execute();
    }

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
            mBrowseAppAdapter = new BrowseAppInfoAdapter(
                    getApplicationContext(), mlistAppInfo);
            mBrowseAppAdapter.setItemClickCallBack(AppInfoActivity.this);
            listview.setAdapter(mBrowseAppAdapter);
            listview.setEmptyView(findViewById(R.id.empty));
            super.onPostExecute(result);
        }

        private void queryAppInfo() {
            PackageManager pm = getPackageManager();
            Intent mainIntent = new Intent(Intent.ACTION_MAIN);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            // List<ResolveInfo> resolveInfos = pm
            // .queryIntentActivities(mainIntent, 0);
            List<PackageInfo> packages = pm.getInstalledPackages(0);

            if (mlistAppInfo != null) {
                mlistAppInfo.clear();
                for (PackageInfo pkgInfo : packages) {
                    int flags = pkgInfo.applicationInfo.flags;
                    // 系统应用不冷藏
                    if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        continue;
                    }
                    // 自己不能冷藏自己
                    if (pkgInfo.applicationInfo.packageName
                            .equals("com.lang.frozenapp")) {
                        continue;
                    }
                    // 不冷藏主题
                    if (pkgInfo.applicationInfo.packageName.contains("com.lang.theme.")) {
                        continue;
                    }
                    // 已经停用的应用
                    if (!pkgInfo.applicationInfo.enabled) {
                        continue;
                    }

                    String activityName = pkgInfo.applicationInfo.name;
                    String pkgName = pkgInfo.applicationInfo.packageName;
                    String appLabel = pkgInfo.applicationInfo.loadLabel(pm)
                            .toString().trim();
                    Drawable icon = pkgInfo.applicationInfo.loadIcon(pm);

                    // Intent launchIntent = new Intent();
                    // launchIntent.setComponent(new ComponentName(pkgName,
                    // activityName));

                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppLabel(appLabel);
                    appInfo.setPkgName(pkgName);
                    appInfo.setActivityName(activityName);
                    appInfo.setAppIcon(icon);
                    // appInfo.setIntent(launchIntent);
                    mlistAppInfo.add(appInfo);
                    Log.i("appzzj", "appInfo=" + pkgName);
                }
                Collections.sort(mlistAppInfo, new DisplayNameComparator());
            }
        }
    }

    public static class DisplayNameComparator implements Comparator<AppInfo> {
        public DisplayNameComparator() {
        }

        public final int compare(AppInfo a, AppInfo b) {
            CharSequence sa = a.getAppLabel();
            CharSequence sb = b.getAppLabel();
            return Collator.getInstance().compare(sa.toString(), sb.toString());
        }
    }
}
