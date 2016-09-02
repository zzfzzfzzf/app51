package com.zzf.app51;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zzf.app51.utils.StringUtils;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

public class AppContext extends Application{
	public static final int nettype_wifi=0x01;
	public static final int nettype_cmwap=0x02;
	public static final int nettype_cmnet=0x03;
	private boolean login=false;
	private int loginUid=0;
	private static String saveImagePath;
	public static int netWorkState=0;
	private Handler loginHander=new Handler(){
		public void handleMessage(Message msg){
			if(msg.what==1){
				
			}
		}
	};
	public void onCreate(){
		init();
		super.onCreate();
	}
	private void init(){
		if (StringUtils.isEmpty(saveImagePath)) {
			setProperty(AppConfig.save_image_path,
					AppConfig.default_save_image_path);
			saveImagePath = AppConfig.default_save_image_path;
		}

		initImageLoader(getApplicationContext());
	}
	public static void initImageLoader(Context context) {
	
		File cacheDir = StorageUtils.getCacheDirectory(context);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				// default
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				// default
				.denyCacheImageMultipleSizesInMemory()
				.diskCache(new UnlimitedDiscCache(cacheDir))
				// 自定义缓存目�?
				.diskCacheSize(20 * 1024 * 1024).diskCacheFileCount(100)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()) // default
				.writeDebugLogs().build();

		ImageLoader.getInstance().init(config);
	}
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = nettype_cmnet;
				} else {
					netType = nettype_cmwap;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = nettype_wifi;
		}
		return netType;
	}
	public void netWorkSettings(Context context) {
		Intent intent = null;
		
		if (android.os.Build.VERSION.SDK_INT > 10) {
			intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
			
		} else {
			intent = new Intent();
			ComponentName component = new ComponentName("com.android.settings",
					"com.android.settings.WirelessSettings");
			intent.setComponent(component);
			intent.setAction("android.intent.action.VIEW");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}
	public static String getAppName(Context context) {
		String verName = context.getResources().getText(R.string.app_name)
				.toString();
		return verName;
	}
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.conf_app_uniqueID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.conf_app_uniqueID, uniqueID);
		}
		return uniqueID;
	}
	public boolean isLogin() {
		return login;
	}


	public int getLoginUid() {
		return this.loginUid;
	}

	public void Logout() {
		this.login = false;
		this.loginUid = 0;
	}

	/**
	 * 未登录或修改密码后的处理
	 */
	public Handler getUnLoginHandler() {
		return this.loginHander;
	}
	public static void clear(Context context) {
		if (null == context) {
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(
				AppConfig.PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}
	public void cleanLoginInfo(String loginType) {
		// this.loginUid = 0;
		// this.login = false;
		removeProperty("login", "loginType", "birthday", "sex", "pictureUrl",
				"screen_name", "profession", "userId", "corporeity",
				"attentionIllness", "maritalStatus", "user_uid",
				"profile_image_url", "gender", "token", "sid", "isAutoLogin",
				"getUserInfoDetails", "qqbdState", "wxbdState", "wbbdState");
		
		if (loginType != null && "weiBo".equals(loginType)) {
			clear(getApplicationContext());
		}

	}
	public boolean setSound() {
		if (!StringUtils.isEmpty(getProperty("isSound"))) {
			if (getProperty("isSound").equals("true")) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}
}
