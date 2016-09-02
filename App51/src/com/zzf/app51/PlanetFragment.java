/**
 * 
 */
package com.zzf.app51;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import cn.waps.AppConnect;

import com.alibaba.fastjson.JSON;
import com.zzf.app51.R;
import com.zzf.app51.adapter.PicListViewAdapter;
import com.zzf.app51.bean.ImageData;
import com.zzf.app51.utils.Constant;
import com.zzf.app51.utils.FileUtils;
import com.zzf.app51.utils.HttpUtils;

/**
 * @author Adobe
 * 
 */
@SuppressLint("NewApi")
public class PlanetFragment extends Fragment implements OnScrollListener {
	public static final String ARG_PLANET_NUMBER = "planet_number";
	private String planet = "";
	private ListView listView;
	private boolean isLastRow;
	private int SCROLL_STATE_IDLE = 0;
	private ProgressDialog dialog;
	private int pageNum;
	// 设置一个最大的数据条数，超过即不再加载
	private int MaxDateNum;
	private int lastVisibleIndex;

	HashMap<String, Object> map;
	ArrayList<HashMap<String, Object>> listItem;
	public File cache;
	private FileUtils fileUtils;
	private int displayWidth, displayHeight;

	private PicListViewAdapter picListViewAdapter;

	List<ImageData> data = new ArrayList<ImageData>();

	private final static int INIT_LOAD = 1;
	private final static int OTHER_LOAD = 2;

	private Handler loadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INIT_LOAD:
				loadData(planet, getWidthAndHeight());
				break;
			case OTHER_LOAD:
				loadMoreDate();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 获取全部的图片数据
	 * 
	 * @param account
	 * @param pwd
	 * @param isRememberMe
	 */
	private void loadData(final String planets, final String widthandheight) {

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				Object obj = msg.obj;
				if (msg.what == 1) {
					for (int i = 0; i < 5; i++) {
						picListViewAdapter.addItem(data.get(i));
					}
					// 通知listView刷新数据
					picListViewAdapter.notifyDataSetChanged();
					listView.post(new Runnable() {
						@Override
						public void run() {
							int lastItem = picListViewAdapter.getCount();
							listView.setSelection(lastItem - 5);
						}
					});
					dialog.dismiss();
				}
				if (msg.what == -1) {
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = Message.obtain();
				int what = -1;
				Object obj = "获取数据失败";
				String url = planets + "&" + widthandheight;
				String Content = "";

				JSONObject mJsonObject;
				try {
					data.clear();
					Content = HttpUtils.getContent(url);
					mJsonObject = new JSONObject(Content);
					JSONArray mJsonArray = mJsonObject.getJSONArray("data");
					ImageData imageData = null;
					for (int k = 0; k < mJsonArray.length(); k++) {
						JSONObject objs = mJsonArray.getJSONObject(k);
						imageData = JSON.parseObject(objs.toString(),
								ImageData.class);
						data.add(imageData);
					}
					what = 1;
				} catch (Exception e) {
					e.printStackTrace();
				}
				msg.what = what;
				msg.obj = obj;
				handler.sendMessage(msg);
			}
		}.start();

	}

	private void loadMoreDate() {
		int count = picListViewAdapter.getCount();
		if (count + 5 < MaxDateNum) {
			// 每次加载5条
			for (int i = count; i < count + 5; i++) {
				picListViewAdapter.addItem(data.get(i));
			}
		} else {
			// 数据已经不足5条
			for (int i = count; i < MaxDateNum; i++) {
				picListViewAdapter.addItem(data.get(i));
			}
		}
		dialog.dismiss();
		// 通知listView刷新数据
		picListViewAdapter.notifyDataSetChanged();
	}

	public PlanetFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_planet, container,
				false);
		AppConnect.getInstance(getActivity().getApplication()).setCrashReport(
				true);

		int i = getArguments().getInt(ARG_PLANET_NUMBER);
		/*
		if (Constant.SHOWIMG.equals("0")) {
			planet = getResources().getStringArray(R.array.planets_array2)[i];
		} else {
			planet = getResources().getStringArray(R.array.planets_array)[i];
		}*/
		
		planet = getResources().getStringArray(R.array.planets_array)[i];

		// 设置最大数据条数
		MaxDateNum = 50;
		fileUtils = new FileUtils();
		cache = fileUtils.getFile();
		dialog = new ProgressDialog(getActivity());
		// dialog.setMessage("加载中,请稍后……");
		dialog.setCancelable(false);
		picListViewAdapter = new PicListViewAdapter(getActivity(),
				R.layout.item, null);
		Constant.planet = planet;
		Constant.WidthAndHeight = getWidthAndHeight();
		// listView = (ListView) rootView.findViewById(R.id.lv);
		// listView.setAdapter(picListViewAdapter);
		// loadHandler.obtainMessage(INIT_LOAD).sendToTarget();
		// listView.setOnScrollListener(this);
		if (!Constant.SHOWIMG.equals("0")) {
			// 设置迷你广告背景颜色
			AppConnect.getInstance(getActivity()).setAdBackColor(
					Color.argb(50, 120, 240, 120));
			// 设置迷你广告广告诧颜色
			AppConnect.getInstance(getActivity()).setAdForeColor(Color.YELLOW);
			// 若未设置以上两个颜色，则默认为黑底白字
			LinearLayout miniLayout = (LinearLayout) rootView
					.findViewById(R.id.miniAdLinearLayout);
			AppConnect.getInstance(getActivity()).showMiniAd(getActivity(),
					miniLayout, 10); // 默认10 秒切换一次广告
		}
		
		// dialog.show();

		int imageId = getResources().getIdentifier(
				planet.toLowerCase(Locale.getDefault()), "drawable",
				getActivity().getPackageName());
		getActivity().setTitle(planet);

		return rootView;
	}

	public String getWidthAndHeight() {
		String str = "";
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindow().getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		displayWidth = dm.widthPixels;
		displayHeight = dm.heightPixels;
		Constant.height = displayHeight;
		return "width=" + displayWidth + "&height=" + displayHeight;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		lastVisibleIndex = firstVisibleItem + visibleItemCount;
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount > 0) {
			if (totalItemCount == MaxDateNum
					&& SCROLL_STATE_IDLE == OnScrollListener.SCROLL_STATE_IDLE) {
				isLastRow = false;
			} else {
				isLastRow = true;
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (isLastRow && scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == picListViewAdapter.getCount()) {
			isLastRow = false;
			int count = picListViewAdapter.getCount();
			if (count + 5 < MaxDateNum + 1) {
				pageNum = count + 5;
			} else {
				pageNum = MaxDateNum;
			}
			dialog.show();
			loadHandler.obtainMessage(OTHER_LOAD).sendToTarget();
		}
	}

}
