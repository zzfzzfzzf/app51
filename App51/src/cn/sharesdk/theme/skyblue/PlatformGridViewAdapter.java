/*
 * 官网地站:http://www.mob.com
 * �?术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第�?时间通过微信将版本更新内容推送给您�?�如果使用过程中有任何问题，也可以�?�过微信与我们取得联系，我们将会�?24小时内给予回复）
 *
 * Copyright (c) 2013�? mob.com. All rights reserved.
 */

package cn.sharesdk.theme.skyblue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.CustomerLogo;
import cn.sharesdk.onekeyshare.ShareCore;

import static cn.sharesdk.framework.utils.R.getBitmapRes;
import static cn.sharesdk.framework.utils.R.getIdRes;
import static cn.sharesdk.framework.utils.R.getLayoutRes;

public class PlatformGridViewAdapter extends BaseAdapter implements View.OnClickListener {

	private final Context context;
	private List<Object> logos = new ArrayList<Object>();
	private List<Integer> checkedPositionList = new ArrayList<Integer>();
	private int directOnlyPosition = -1;

	static class ViewHolder {
		public Integer position;
		public ImageView logoImageView;
		public ImageView checkedImageView;
		public TextView nameTextView;
	}

	public PlatformGridViewAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return logos.size();
	}

	@Override
	public Object getItem(int i) {
		return logos.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder;
		if(view == null) {
			view = LayoutInflater.from(context).inflate(getLayoutRes(context, "skyblue_share_platform_list_item"), null);
			viewHolder = new ViewHolder();
			viewHolder.checkedImageView = (ImageView) view.findViewById(getIdRes(context, "checkedImageView"));
			viewHolder.logoImageView = (ImageView) view.findViewById(getIdRes(context, "logoImageView"));
			viewHolder.nameTextView = (TextView) view.findViewById(getIdRes(context, "nameTextView"));
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		Bitmap logo;
		String label;
		Object item = getItem(position);
		boolean disabled;
		boolean isDirectShare = item instanceof Platform ? ShareCore.isDirectShare((Platform) item) : true;
		if(directOnlyPosition == -1) {
			disabled = !checkedPositionList.isEmpty() && isDirectShare;
		} else {
			disabled = position != directOnlyPosition;
		}

		if (item instanceof Platform) {
			logo = getIcon((Platform) item, disabled ? "" : "_checked");
			label = getName((Platform) item);
			view.setOnClickListener(this);
		} else {
			CustomerLogo customerLogo = (CustomerLogo) item;
			logo = disabled ? customerLogo.disableLogo : customerLogo.enableLogo;
			label = customerLogo.label;
			view.setOnClickListener(this);
			//TODO �?要整�?
		//	view.setOnClickListener(((CustomerLogo) item).listener);
		}
		String checkedResName = directOnlyPosition != -1 && directOnlyPosition != position ? "skyblue_platform_checked_disabled" : "skyblue_platform_checked";
		viewHolder.position = position;
		viewHolder.checkedImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), getBitmapRes(context, checkedResName)));
		viewHolder.checkedImageView.setVisibility(checkedPositionList.contains(viewHolder.position) ? View.VISIBLE : View.GONE);
		viewHolder.nameTextView.setText(label);
		viewHolder.logoImageView.setImageBitmap(logo);

		return view;
	}

	@Override
	public void onClick(View view) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		Integer position = viewHolder.position;
		//直接分享平台选中后，其它的不可用
		if(directOnlyPosition != -1 && position != directOnlyPosition)
			return;

		Object item = getItem(position);
		boolean direct = false;
		//normal platform
		if(item instanceof Platform){
			direct = ShareCore.isDirectShare((Platform) item);
		}else{
			//自定义图�?
			direct = true;
		}
		//EditPage Platforms only
		if(direct && directOnlyPosition == -1 && !checkedPositionList.isEmpty())
			return;

		if(checkedPositionList.contains(position)) {
			checkedPositionList.remove(position);
			if(direct)
				directOnlyPosition = -1;
		} else {
			checkedPositionList.add(position);
			if(direct)
				directOnlyPosition = position;
		}

		notifyDataSetChanged();
	}

	public void setData(Platform[] platforms, HashMap<String, String> hiddenPlatforms) {
		if(platforms == null)
			return;
		if (hiddenPlatforms != null && hiddenPlatforms.size() > 0) {
			ArrayList<Platform> ps = new ArrayList<Platform>();
			for (Platform p : platforms) {
				if (hiddenPlatforms.containsKey(p.getName())) {
					continue;
				}
				ps.add(p);
			}

			logos.addAll(ps);
		} else {
			logos.addAll(Arrays.asList(platforms));
		}
		checkedPositionList.clear();
		notifyDataSetChanged();
	}

	public void setCustomerLogos(ArrayList<CustomerLogo> customers) {
		if(customers == null || customers.size() == 0)
			return;
		logos.addAll(customers);
	}

	public List<Object> getCheckedItems() {
		ArrayList<Object> list = new ArrayList<Object>();

		if(directOnlyPosition != -1) {
			list.add(getItem(directOnlyPosition));
			return list;
		}

		Object item;
		for(Integer position : checkedPositionList) {
			item = getItem(position);
			list.add(item);
		}
		return list;
	}

	private Bitmap getIcon(Platform plat, String subfix) {
		String resName = "skyblue_logo_" + plat.getName() + subfix;
		int resId = getBitmapRes(context, resName);
		return BitmapFactory.decodeResource(context.getResources(), resId);
	}

	private String getName(Platform plat) {
		if (plat == null) {
			return "";
		}

		String name = plat.getName();
		if (name == null) {
			return "";
		}

		int resId = cn.sharesdk.framework.utils.R.getStringRes(context, plat.getName());
		if (resId > 0) {
			return context.getString(resId);
		}
		return null;
	}
}
