package com.zzf.app51;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;
import cn.waps.AppConnect;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zdp.aseo.content.AseoZdpAseo;
import com.zzf.app51.bean.ImageData;
import com.zzf.app51.utils.Constant;
import com.zzf.app51.utils.HttpUtils;
import com.zzf.app51.utils.ShareSdkShare;
import com.zzf.app51.utils.UIHelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class SecondActivity extends Activity{
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private CharSequence drawerTitle;
	private AppContext appContext;
	private CharSequence title;
	private String[]planetTitles;
	private ShareSdkShare shareSDKShare;
	private FrameLayout content_frame;
	private long exitTime;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		content_frame=(FrameLayout)findViewById(R.id.content_frame);
		AppConnect.getInstance(this).setCrashReport(true);
		appContext = (AppContext) getApplication();
		AseoZdpAseo.init(this, AseoZdpAseo.INSERT_TYPE);
		title = drawerTitle = getTitle();
		/*
		if (Constant.SHOWIMG.equals("0")) {
			mPlanetTitles = getResources().getStringArray(
					R.array.planets_array2);
		} else {
			mPlanetTitles = getResources()
					.getStringArray(R.array.planets_array);
		}*/
		planetTitles = getResources()
				.getStringArray(R.array.planets_array);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		drawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, planetTitles));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		drawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		drawerLayout, /* DrawerLayout object */
		R.drawable.jietu ,/* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(title);
				invalidateOptionsMenu(); // creates call to
										// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(drawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
		
		if (savedInstanceState == null) {
			selectItem(0);
		}
	}
	private class DrawerItemClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			selectItem(position);
		}
	}
	private void selectItem(int position){
		if(position==11){
			Intent intent=new Intent(appContext,SettingActivity.class);
			startActivity(intent);
		}else{
			
			PlanetFragment fragment = new PlanetFragment();
			Bundle args = new Bundle();
			args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
			fragment.setArguments(args);
			
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
			//content_frame.setBackground(drawable);
			// update selected item and title, then close the drawer
			drawerList.setItemChecked(position, true);
			setTitle(planetTitles[position]);
			drawerLayout.closeDrawer(drawerList);
		}
	}
	
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 MenuInflater inflater = getMenuInflater();
		 inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	 }
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.action_websearch:
			// create intent to perform web search for this planet
			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
			intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
			// catch event that there's no activity to handle intent
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			} else {
				Toast.makeText(this, R.string.app_not_available,
						Toast.LENGTH_LONG).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public void setTitle(CharSequence charSequence){
		title=charSequence;
		getActionBar().setTitle(title);
	}
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}
	public void onConfigurationChanged(Configuration configuration){
		super.onConfigurationChanged(configuration);
		drawerToggle.onConfigurationChanged(configuration);
	}
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if((System.currentTimeMillis()-exitTime)>2000){
				UIHelper.ToastMessage(getApplicationContext(),"再按一次退出");
				exitTime=System.currentTimeMillis();
			}else{
				ShareSDK.stopSDK(getApplicationContext());
				AppConnect.getInstance(this).close();
				ImageLoader.getInstance().stop();
				AppManager.getAppManager().AppExit(getApplicationContext());
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode,event);
	}
}
