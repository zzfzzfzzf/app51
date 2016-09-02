package com.zzf.app51;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

public class AppConfig {
	private static final String app_config="config";
	public static final String conf_app_uniqueID="app_uniqueID";
	public final static String save_image_path="save_image_path";
	public static final String APP_KEY="1867383794";
	public static final String REDIRECT_URL="https://api.weibo.com/oauth2/default.html";
	public static final String SCOPE="email,follow_app_official_microblog";
	public static final String PREFERENCES_NAME="com.zzf.app51";
	public static final String KEY_UID="uid";
	public static final String KEY_ACCESS_TOKEN="access_token";
	public static final String KEY_EXPIRES_IN="expires_in";
	public final static String default_save_image_path=Environment.getExternalStorageDirectory()+File.separator+
			"com.zzf.app51"+File.separator;
	private Context context;
	private static AppConfig appConfig;
	public static AppConfig getAppConfig(Context context){
		if(appConfig==null){
			appConfig=new AppConfig();
			appConfig.context=context;
		}
		return appConfig;
	}
	public static SharedPreferences getSharedPreferences(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	public String get(String key){
		Properties props=get();
		return (props==null)?null:props.getProperty(key);
	}
	public Properties get(){
		FileInputStream fis=null;
		Properties props=new Properties();
		try{
			File dirConf=context.getDir(app_config, Context.MODE_PRIVATE);
			fis=new FileInputStream(dirConf.getPath()+File.separator+app_config);
			props.load(fis);
		}catch(Exception e){
			
		}finally{
			try{
				fis.close();
			}catch(Exception e){
				
			}
		}
		return props;
	}
	private void setProps(Properties props){
		FileOutputStream fos=null;
		try{
			File dirConf=context.getDir(app_config, Context.MODE_PRIVATE);
			File conf=new File(dirConf,app_config);
			fos=new FileOutputStream(conf);
			props.store(fos, null);
			fos.flush();
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			try{
				fos.close();
			}catch(Exception e){
				
			}
		}
	}
	public void set(Properties ps){
		Properties props=get();
		props.putAll(ps);
		setProps(props);
	}
	public void set(String key,String value){
		Properties props=get();
		props.setProperty(key, value);
		setProps(props);
	}
	public void remove(String... key) {
		Properties props = get();
		for (String k : key)
			props.remove(k);
		setProps(props);
	}
}
