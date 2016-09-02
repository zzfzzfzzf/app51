package com.zzf.app51;

import cn.sharesdk.framework.ShareSDK;
import cn.waps.AppConnect;

import com.zdp.aseo.content.AseoZdpAseo;
import com.zzf.app51.utils.ShareSdkShare;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class SettingActivity extends Activity {
	private String TAG = SettingActivity.class.getSimpleName();

	private AppContext appContext;

	private Activity mActivity;

	/** 推荐（积分墙） **/
	private LinearLayout offer_LinearLayout;
	/** 版本更新 **/
	private LinearLayout versions_LinearLayout;
	/** 分享 **/
	private LinearLayout share_LinearLayout;
	/** 评论 **/
	private LinearLayout comments_LinearLayout;
	/** 反馈 **/
	private LinearLayout feedback_LinearLayout;
	private ShareSdkShare shareSDKShare;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		// TODO Auto-generated method stub
		appContext = (AppContext) getApplication();
		mActivity = this;
		AppConnect.getInstance(this).setCrashReport(true);

		// 初始化分享组件
		shareSDKShare = new ShareSdkShare(appContext);
		// 初始化ShareSDK
		ShareSDK.initSDK(appContext);
		AseoZdpAseo.initType(this, AseoZdpAseo.INSERT_TYPE);
		offer_LinearLayout = (LinearLayout) findViewById(R.id.offer_lay);
		versions_LinearLayout = (LinearLayout) findViewById(R.id.versions_lay);
		share_LinearLayout = (LinearLayout) findViewById(R.id.share_lay);
		comments_LinearLayout = (LinearLayout) findViewById(R.id.comments_lay);
		feedback_LinearLayout = (LinearLayout) findViewById(R.id.feedback_lay);

		/**
		 * 推荐
		 */
		offer_LinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppConnect.getInstance(mActivity).showOffers(mActivity);
			}
		});

		// 版本信息
		versions_LinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AppConnect.getInstance(mActivity).checkUpdate(mActivity);
			}
		});

		// 分享
		share_LinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				shareSDKShare
						.showShare(
								"http://yxyun.apps.cn/details?app=925511fe7f5de9b2084324cebef603f7",
								"51妹子图手机端,炫丽的瀑布流效果,让妹子尽收你眼底",
								"  51妹子图手机端，涵盖不同风格、不同类型的优质高清的美女图片，\n客户端对图片进行了压缩缓存处理，省流量更流畅，\n无需登录即可免费浏览各种类型的高清妹子图片，\n炫丽的瀑布流效果，让妹子尽收你眼底，\n赶快来发现只属于你的专属美女吧。",
								"http://bcs.91.com/pcsuite-dev/img/0/512_512/fcbcc6df4697d35154ec3c871a569455.jpeg");
			}
		});
		// 应用市场评论
		comments_LinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("market://details?id="
						+ appContext.getPackageInfo().packageName);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		// 反馈
		feedback_LinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AppConnect.getInstance(mActivity).showFeedback(mActivity);
			}
		});

	}

	public void go_back(View view) {
		this.finish();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		ShareSDK.stopSDK(appContext);
		super.onDestroy();
	}
}
