package com.zzf.app51;

import cn.waps.AppConnect;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zdp.aseo.content.AseoZdpAseo;
import com.zzf.app51.utils.MyImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

public class ImageScaleActivity extends Activity {
	private Bitmap bitmap;
	private String filepath;
	private static final boolean defaultValue = false;
	/** Called when the activity is first created. */
	private MyImageView image;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppConnect.getInstance(this).setCrashReport(true);
		Intent intent = getIntent();
		filepath = intent.getStringExtra("filePath");
		AseoZdpAseo.init(this, AseoZdpAseo.INSERT_TYPE);
		if(!(filepath != null)){
			filepath = "http://h.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c4859e0ba85d6277f9e2ff84e.jpg";
		}
		// 定义Handler对象
		final Handler handler = new Handler() {
			@Override
			// 当有消息发送出来的时候就执行Handler的这个方法
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				// 处理UI
				image = new MyImageView(getApplication());
				image.setScreenSize(getApplication(), getWindowManager()
						.getDefaultDisplay().getWidth(), getWindowManager()
						.getDefaultDisplay().getHeight(), bitmap);
				setContentView(image);
			}
		};

		new Thread() {
			@Override
			public void run() {
				// 执行完毕后给handler发送一个空消息
				bitmap = ImageLoader.getInstance().loadImageSync(filepath);
				handler.sendEmptyMessage(0);
			}
		}.start();

	}

	private static final int DRAG = 10;
	private static final int NULL = 0;
	private static final int SCALE = 11;
	private int mode;
	private float mStartX;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;

			mStartX = event.getRawX();
			image.init(event);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == SCALE)
				image.zoom(event);
			if (mode == DRAG) {
				image.drag(event);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mode == SCALE) {
				image.backScale();
				mode = NULL;
			} else if (mode == DRAG) {
				image.backDrag();
			}
			break;
		case MotionEvent.ACTION_POINTER_1_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_2_DOWN:
			image.getOldDist(event);
			mode = SCALE;
			break;
		case MotionEvent.ACTION_POINTER_2_UP:

		default:
			break;
		}

		return super.onTouchEvent(event);
	}

	private float calculate(float x1, float x2) {

		float pz = x1 - x2;
		return pz;
	}
}
