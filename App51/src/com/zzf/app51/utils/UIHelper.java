package com.zzf.app51.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zzf.app51.AppContext;
import com.zzf.app51.MainActivity;
import com.zzf.app51.utils.StringUtils;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @author yuanxy
 * 
 */
public class UIHelper {
	private final static String TAG = "UIHelper";
	private OnCheckLoginListener onCheckLoginListener;

	public void setOnCheckLoginListener(OnCheckLoginListener Listener) {
		this.onCheckLoginListener = Listener;
	}

	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public interface OnCheckLoginListener {
		public void onSucceed();
	}

}
