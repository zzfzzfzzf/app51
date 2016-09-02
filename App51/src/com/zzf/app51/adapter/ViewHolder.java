package com.zzf.app51.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewHolder {
	private final SparseArray<View>views;
	private int position;
	private View convertView;
	private static DisplayImageOptions options;
	private ImageLoadingListener animateFirstDisplayListener=new AnimateFirstDisplayListener();
	public ViewHolder(Context context,ViewGroup viewGroup,int layoutId,int position,DisplayImageOptions options){
		this.position=position;
		views=new SparseArray<View>();
		convertView=LayoutInflater.from(context).inflate(layoutId, viewGroup,false);
		this.options=options;
		convertView.setTag(this);
	}
	public static ViewHolder get(Context context,View convertView,ViewGroup parent,int layoutId,int position){
		if(convertView==null)
			return new ViewHolder(context,parent,layoutId,position,options);
		return (ViewHolder)convertView.getTag();
	}
	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position,
			DisplayImageOptions options) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position, options);
		}
		return (ViewHolder) convertView.getTag();
	}
	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position,
			DisplayImageOptions options, ProgressBar spinner) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position, options);
		}
		return (ViewHolder) convertView.getTag();
	}
	public ViewHolder getImageBitmap(int viewId,Bitmap bitmap){
		ImageView imageView=getView(viewId);
		imageView.setImageBitmap(bitmap);
		return this;
	}
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);
		return this;
	}
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}
	public <T extends View> T getView(int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (T) view;
	}
	public View getConvertView() {
		return convertView;
	}

	public int getPosition(){
		return position;
	}
	public void displayImages(int viewId,String imageUrl){
		ImageView imageView=getView(viewId);
		if("null".equals(imageUrl)){
			imageUrl="http://h.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c4859e0ba85d6277f9e2ff84e.jpg";
			
		}
		ImageLoader.getInstance().displayImage(imageUrl, imageView,options,animateFirstDisplayListener);
		
	}
	public void stopAndClear(){
		ImageLoader.getInstance().stop();
		AnimateFirstDisplayListener.displayedImages.clear();
	}
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener{
		static final List<String>displayedImages=Collections.synchronizedList(new LinkedList<String>());
		@Override
		public void onLoadingComplete(String imageUrl,View view,Bitmap loadedImage){
			if(loadedImage!=null){
				ImageView imageView=(ImageView)view;
				boolean firstDispaly=!displayedImages.contains(imageUrl);
				if(firstDispaly){
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUrl);
				}
			}
		}
	}
}
