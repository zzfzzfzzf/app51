package com.zzf.app51;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.os.Build;
import cn.waps.AppConnect;

import com.zzf.app51.R;
import com.zzf.app51.utils.Constant;
import com.zzf.app51.utils.StringUtils;

public class MainActivity extends Activity {
	private AppContext appContext;
	boolean isFirstIn=false;
	private static final int go_home=1000;
	private static final int go_guide=1001;
	private static final long splash_delay_millis=1000;
	private static final String sharedpreferences_name="first_pref";
	private String tag=MainActivity.class.getSimpleName();
	private Handler handler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case go_home:
				goHome();
				break;
			case go_guide:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start);
        appContext=(AppContext)getApplicationContext();
        AppConnect.getInstance(Constant.APP_ID,Constant.APP_PID,this);
        setAppId(this);
        init();
        
    }
    public static String getDeviceInfo(Context context) throws JSONException{
    	JSONObject jsonObject=new JSONObject();
    	TelephonyManager telephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    	String device_id=telephonyManager.getDeviceId();
    	WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	String mac=wifiManager.getConnectionInfo().getMacAddress();
    	jsonObject.put("mac", mac);
    	if(TextUtils.isEmpty(device_id)){
    		device_id=mac;
    	}
    	if (TextUtils.isEmpty(device_id)) {
			device_id = android.provider.Settings.Secure.getString(
					context.getContentResolver(),
					android.provider.Settings.Secure.ANDROID_ID);
		}
    	jsonObject.put("device_id", device_id);
    	return jsonObject.toString();
    }
    private void setAppId(Context context){
    	String uniqueID=appContext.getProperty(AppConfig.conf_app_uniqueID);
    	if(StringUtils.isEmpty(uniqueID)){
    		TelephonyManager telephony=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    		uniqueID=telephony.getDeviceId();
    		if (TextUtils.isEmpty(uniqueID)) {
				uniqueID = android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}
    		appContext.setProperty(AppConfig.conf_app_uniqueID, uniqueID);
    		
    	}else{
    		appContext.setProperty(AppConfig.conf_app_uniqueID, uniqueID);
    	}
    	String value=AppConnect.getInstance(this).getConfig("SHOWIMG","0");
    	Constant.SHOWIMG=value;
    }
    private void init(){
    	SharedPreferences preferences=getSharedPreferences(sharedpreferences_name,MODE_PRIVATE);
    	isFirstIn=preferences.getBoolean("isFirstIn",false);
    	if(!isFirstIn){
    		handler.sendEmptyMessageDelayed(go_home,splash_delay_millis);
    		
    	}else{
    		handler.sendEmptyMessageDelayed(go_guide, splash_delay_millis);
    	}
    	

    	
    	
    }
    private void goHome(){
    	final View view=View.inflate(this, R.layout.app_start, null);
    	setContentView(view);
    	AlphaAnimation alpha=new AlphaAnimation(0.3f,1.0f);
    	alpha.setDuration(2000);
    	view.startAnimation(alpha);
    	alpha.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				redirect();
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    }
    private void goGuide() {
		//Intent intent = new Intent(AppStart.this, GuideActivity.class);
		//AppStart.this.startActivity(intent);
		//AppStart.this.finish();
	}
    private void redirect()
    {
    	Intent intent=new Intent(MainActivity.this,SecondActivity.class);
    	MainActivity.this.startActivity(intent);
    	finish();
    	
    }
    public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}
    

}
