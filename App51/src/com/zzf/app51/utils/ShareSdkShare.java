package com.zzf.app51.utils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;

import com.zzf.app51.R;
public class ShareSdkShare implements PlatformActionListener,Callback{
	private static Context mContext;
	private final static int SHARE_SUCCESS = 1;
	private final static int SHARE_CANCEL = 2;
	private final static int SHARE_ERROR = 3;

	public ShareSdkShare(Context context) {
		this.mContext = context;
	}

	/**
	 * 分享
	 * 
	 * @param titleUrl
	 *            titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	 * @param title
	 *            分享的标题
	 * @param text
	 *            text是分享文本，所有平台都需要这个字段 （不传 使用 null 或者 "" 默认内容 健康小蜜,您最贴身的健康伴侣!）
	 * @param imageUrl
	 *            imageUrl是图片的网络路径 ，新浪微博、人人网、QQ空间和Linked-In支持此字段
	 */
	public void showShare(final String titleUrl, String title, String text,
			final String imageUrl) {
		ShareSDK.initSDK(mContext);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		// oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.ic_launcher,
				mContext.getString(R.string.app_name));

		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(title);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(titleUrl);
		// text是分享文本，所有平台都需要这个字段
		if (TextUtils.isEmpty(text)) {
			oks.setText("51妹子图手机端,炫丽的瀑布流效果，让妹子尽收你眼底");
		} else {
			oks.setText(text);
		}

		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// oks.setImageUrl("http://f.hiphotos.baidu.com/image/w%3D310/sign=4b49d21db4003af34dbada61052bc619/b8389b504fc2d562f8afe2f9e51190ef76c66c7a.jpg");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(titleUrl);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(mContext.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(titleUrl);

		oks.setCallback(this);

		oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
			// 自定义分享的回调想要函数
			@Override
			public void onShare(Platform platform,
					cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
				if ("SinaWeibo".equals(platform.getName())) {
					// 限制微博内容不超过120个汉字。
					if (paramsToShare.getText().length() > 120) {
						UIHelper.ToastMessage(mContext, "分享长度不能超过120个字");
					}
					paramsToShare.setText(paramsToShare.getText() + " "
							+ titleUrl);
				}
				if (!"SinaWeibo".equals(platform.getName())) {
					paramsToShare.setImageUrl(imageUrl);
				}
				/*
				 * if ("Wechat".equals(platform.getName())) { //
				 * 设置微信的分享方式（不设置默认只能分享文字信息）这里设置图文分享模式也叫网页模式
				 * paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
				 * paramsToShare.setImageUrl(imageUrl); } if
				 * ("WechatMoments".equals(platform.getName())) {
				 * paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
				 * paramsToShare.setImageUrl(imageUrl); } if
				 * ("WechatFavorite".equals(platform.getName())) {
				 * paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
				 * paramsToShare.setImageUrl(imageUrl); }
				 */
			}
		});

		// 启动分享GUI
		oks.show(mContext);
	}

	@Override
	public void onCancel(Platform platf, int arg1) {
		if (arg1 == Platform.ACTION_SHARE) {
			UIHandler.sendEmptyMessage(SHARE_CANCEL, this);
		}
	}

	@Override
	public void onComplete(Platform platf, int arg1,
			HashMap<String, Object> arg2) {
		if (arg1 == Platform.ACTION_SHARE) {
			UIHandler.sendEmptyMessage(SHARE_SUCCESS, this);
		}
		if (arg1 == Platform.SHARE_TEXT) {
			UIHandler.sendEmptyMessage(SHARE_SUCCESS, this);
		}
	}

	@Override
	public void onError(Platform platf, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		if (arg1 == Platform.ACTION_SHARE) {
			UIHandler.sendEmptyMessage(SHARE_ERROR, this);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case SHARE_SUCCESS: {
			UIHelper.ToastMessage(mContext, "分享成功");
		}
			break;
		case SHARE_CANCEL: {
			UIHelper.ToastMessage(mContext, "取消分享");
		}
			break;
		case SHARE_ERROR: {
			UIHelper.ToastMessage(mContext, "分享错误");
		}
			break;
		}
		ShareSDK.stopSDK(mContext);
		return false;
	}

}
