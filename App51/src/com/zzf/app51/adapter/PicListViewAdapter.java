package com.zzf.app51.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.zzf.app51.AppContext;
import com.zzf.app51.ImageScaleActivity;
import com.zzf.app51.R;
import com.zzf.app51.SecondActivity;
import com.zzf.app51.bean.ImageData;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PicListViewAdapter extends BaseAdapter{
	protected final int itemLayoutId;
	private Context context;
	private LayoutInflater layoutInflater;
	private AppContext appContext;
	private ImageScaleType imageScaleType;
	private DisplayImageOptions displayImageOptions;
	private ViewHolder viewHolder;
	private List<ImageData>data=new ArrayList<ImageData>();
	public PicListViewAdapter(Context context,int itemLayoutId,ImageScaleType imageScaleType){
		this.context=context;
		this.itemLayoutId=itemLayoutId;
		this.layoutInflater=LayoutInflater.from(context);
		this.appContext=(AppContext)context.getApplicationContext();
		this.imageScaleType=imageScaleType;
		displayImageOptions=new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).imageScaleType(imageScaleType!=null?imageScaleType:ImageScaleType.EXACTLY_STRETCHED)
				.delayBeforeLoading(10).displayer(new SimpleBitmapDisplayer()).build();
	}
	public void setData(List<ImageData>lists){
		data=lists;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data!=null?data.size()-1:0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data!=null?data.get(arg0):null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		viewHolder = getViewHolder(arg0, arg1, arg2);

		if (data != null && data.size() > 0) {
			ImageView imageView = (ImageView) viewHolder.getView(R.id.itemImage);

			String imageUrl = String.valueOf(data.get(arg0)
					.getDownload_url());

			viewHolder.displayImages(R.id.itemImage, imageUrl);

			viewHolder.setText(R.id.tag, data.get(arg0).getTag());

			setClick(data, viewHolder.getConvertView(), arg0);
		}
		return viewHolder.getConvertView();
	}
	private ViewHolder getViewHolder(int position, View convertView,
			ViewGroup parent) {
		return ViewHolder.get(context, convertView, parent, itemLayoutId,
				position);
	}
	public void addItem(ImageData data) {
		this.data.add(data);
	}
	public void stopAndclear() {
		if (viewHolder != null) {
			viewHolder.stopAndClear();
		}
	}
	public void setClick(final List<ImageData>data,View convertView,final int position){
		convertView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String imageUrl=data.get(position).getDownload_url();
				Intent intent=new Intent(context,ImageScaleActivity.class);
				if("null".equals(imageUrl)){
					imageUrl="http://h.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c4859e0ba85d6277f9e2ff84e.jpg";
				}
				intent.putExtra("filePath", imageUrl);
				context.startActivity(intent);
			}
			
		});
	}
}
