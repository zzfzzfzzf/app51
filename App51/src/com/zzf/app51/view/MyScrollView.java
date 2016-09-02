package com.zzf.app51.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.waps.AppConnect;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zzf.app51.bean.ImageData;
import com.zzf.app51.utils.Constant;
import com.zzf.app51.utils.HttpUtils;
import com.zzf.app51.utils.ImageLoader;
import com.zzf.app51.utils.UIHelper;
import com.zzf.app51.ImageScaleActivity;
import com.zzf.app51.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;

public class MyScrollView extends ScrollView implements OnTouchListener{
	public static final int page_size=15;
	
	private int page;
	private int firstColumnHeight;
	private int secondColumnHeight;
	private int thirdColumnHeight;
	private boolean loadOnce;
	private int columnWidth;
	private ImageLoader imageLoader;
	private LinearLayout firstColumn;
	private LinearLayout secondColumn;
	private LinearLayout thirdColumn;
	private static Set<LoadImageTask>taskCollection;
	private static View scrollLayout;
	private static int scrollViewHeight;
	private static int lastScrollY=-1;
	private List<ImageView> imageViewList=new ArrayList<ImageView>();
	private final static int init_load=1;
	List<ImageData>data=new ArrayList<ImageData>();
	String[]imageUrls=new String[Constant.DATA_SIZE];
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstDisplayListener=new AnimateFirstDisplayListener();
	private static Handler handler=new Handler(){
		public void handleMessage(Message msg){
			MyScrollView myScrollView=(MyScrollView)msg.obj;
			int scrollY=myScrollView.getScrollY();
			if(scrollY==lastScrollY){
				if(scrollViewHeight+scrollY>=scrollLayout.getHeight()&&taskCollection.isEmpty()){
					myScrollView.loadMoreImages();
				}
				myScrollView.checkVisibility();
			}else{
				lastScrollY=scrollY;
				Message message=new Message();
				message.obj=myScrollView;
				handler.sendMessageDelayed(message, 5);
			}
		}
	};	
	public MyScrollView(Context context,AttributeSet attrs) {
		super(context,attrs);
		AppConnect.getInstance(context).setCrashReport(true);
		options=new DisplayImageOptions.Builder()
		.showImageOnFail(R.drawable.empty_photo)
		.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).delayBeforeLoading(10)
		.displayer(new SimpleBitmapDisplayer()).build();
		imageLoader=ImageLoader.getInstance();
		taskCollection=new HashSet<LoadImageTask>();
		setOnTouchListener(this);
		
		
	}
	protected void onLayout(boolean changed,int l,int t,int r,int b){
		super.onLayout(changed, l, t, r, b);
		if(changed&&!loadOnce){
			scrollViewHeight=getHeight();
			scrollLayout=getChildAt(0);
			firstColumn=(LinearLayout)findViewById(R.id.first_column);
			secondColumn=(LinearLayout)findViewById(R.id.second_column);
			thirdColumn=(LinearLayout)findViewById(R.id.third_column);
			columnWidth=firstColumn.getWidth();
			loadOnce=true;
			loadHandler.obtainMessage(init_load).sendToTarget();
		}
		
	}
	private Handler loadHandler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case init_load:
				loadData(Constant.planet,Constant.WidthAndHeight);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	private void loadData(final String planet,final String widthAndHeight){
		final Handler handler=new Handler(){
			public void handleMessage(Message msg){
				Object obj=msg.obj;
				if(msg.what==1){
					for(int i=0;i<data.size()-1;i++){
						imageUrls[i]=data.get(i).getDownload_url();
					}
					loadMoreImages();
					
					
				}
				if(msg.what==-1){
					UIHelper.ToastMessage(getContext(), (String)obj);
				}
				
			}
		};
		new Thread(){
			public void run(){
				Message msg=Message.obtain();
				int what=-1;
				Object obj="获取数据失败";
				String url=planet+"&"+widthAndHeight;
				String content="";
				JSONObject jsonObject;
				try{
					data.clear();
					content=HttpUtils.getContent(url);
					jsonObject=new JSONObject(content);
					JSONArray jsonArray=jsonObject.getJSONArray("data");
					ImageData imageData=null;
					for(int k=0;k<jsonArray.length();k++){
						JSONObject js=jsonArray.getJSONObject(k);
						imageData=JSON.parseObject(js.toString(),ImageData.class);
						data.add(imageData);
					}
					what=1;
					
				}catch(Exception e){
					e.printStackTrace();
					
				}
				msg.what=what;
				msg.obj=obj;
				handler.sendMessage(msg);
			}
			
		}.start();
	}
	public void checkVisibility(){
		for(int i=0;i<imageViewList.size();i++){
			ImageView imageView=imageViewList.get(i);
			int borderTop=(Integer)imageView.getTag(R.string.border_top);
			int borderBottom=(Integer)imageView.getTag(R.string.border_bottom);
			if(borderBottom>getScrollY()&&borderTop<getScrollY()+scrollViewHeight){
				String imageUrl = (String) imageView.getTag(R.string.image_url);
				com.nostra13.universalimageloader.core.ImageLoader
						.getInstance().displayImage(imageUrl, imageView,
								options, animateFirstDisplayListener);
			}else{
				imageView.setImageResource(R.drawable.empty_photo);
			}
		}
	}
	public void loadMoreImages(){
		if(hasSDCard()){
			int startIndex=page*page_size;
			int endIndex=page*page_size+page_size;
			if(startIndex<imageUrls.length){
				UIHelper.ToastMessage(getContext(), "正在加载");
				if(endIndex>imageUrls.length){
					endIndex=imageUrls.length;
				}
				for(int i=startIndex;i<endIndex;i++){
					LoadImageTask task=new LoadImageTask();
					taskCollection.add(task);
					task.execute(imageUrls[i]);
				}
				page++;
			}else{
				UIHelper.ToastMessage(getContext(),"无更多图片");
			}
		}else{
			UIHelper.ToastMessage(getContext(), "未发现SD卡");
		}
	}
	private boolean hasSDCard(){
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	class LoadImageTask extends AsyncTask<String,Void,Bitmap>{
		private String imageUrl;
		private ImageView imageView;
		public LoadImageTask(){}
		public LoadImageTask(ImageView imageView){
			this.imageView=imageView;
		}
		@Override
		protected Bitmap doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			imageUrl=arg0[0];
			return null;
		}
		protected void onPostExecute(Bitmap bitmap){
			addImage(bitmap,columnWidth,Constant.height/3);
			taskCollection.remove(this);
		}
		private void addImage(Bitmap bitmap,int imageWidth,int imageHeight){
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(imageWidth,imageHeight);
			if(imageView!=null){
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imageUrl,imageView,options,animateFirstDisplayListener);
			}else{
				ImageView imageView=new ImageView(getContext());
				
				imageView.setLayoutParams(params);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setPadding(5,5,5,5);
				imageView.setTag(R.string.image_url,imageUrl);
				findColumnToAdd(imageView,imageHeight).addView(imageView);
				imageViewList.add(imageView);
				com.nostra13.universalimageloader.core.ImageLoader
				.getInstance().displayImage(imageUrl, imageView,
						options, animateFirstDisplayListener);
				imageView.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String url =imageUrl;
						Intent intent = new Intent(getContext(),
								ImageScaleActivity.class);
						if ("null".equals(imageUrl)) {
							url = "http://h.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c4859e0ba85d6277f9e2ff84e.jpg";
						}
						intent.putExtra("filePath", url);
						getContext().startActivity(intent);
					}
					
				});
			}
		}
		private LinearLayout findColumnToAdd(ImageView imageView,int imageHeight){
			if(firstColumnHeight<=secondColumnHeight){
				if(firstColumnHeight<=thirdColumnHeight){
					imageView.setTag(R.string.border_top,firstColumnHeight);
					firstColumnHeight+=imageHeight;
					imageView.setTag(R.string.border_bottom,firstColumnHeight);
					return firstColumn;
				}
				imageView.setTag(R.string.border_top,thirdColumnHeight);
				thirdColumnHeight+=imageHeight;
				imageView.setTag(R.string.border_bottom,thirdColumnHeight);
				return thirdColumn;
			}
			else {
				if (secondColumnHeight <= thirdColumnHeight) {
					imageView.setTag(R.string.border_top, secondColumnHeight);
					secondColumnHeight += imageHeight;
					imageView
							.setTag(R.string.border_bottom, secondColumnHeight);
					return secondColumn;
				}
				imageView.setTag(R.string.border_top, thirdColumnHeight);
				thirdColumnHeight += imageHeight;
				imageView.setTag(R.string.border_bottom, thirdColumnHeight);
				return thirdColumn;
			}
		}
		private void downloadImage(String imageUrl){
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				Log.d("tag","mounted sdcard");
				
			}else{
				Log.d("tag","unmounted sdcard");
			}
			
			FileOutputStream fos=null;
			
			File imageFile=null;
			try{
				imageFile=new File(getImagePath(imageUrl));
				URL url=new URL(imageUrl);
				HttpURLConnection conn=(HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.connect();
				if(conn.getResponseCode()==200){
					InputStream is=conn.getInputStream();
					fos=new FileOutputStream(imageFile);
					byte[]buffer=new byte[1024*2];
					int len=0;
					while((len=is.read(buffer))!=0){
						fos.write(buffer,0,len);
					}
					is.close();
					fos.close();
				}
				conn.disconnect();
			}catch(Exception e){
				e.printStackTrace();
			}
			if (imageFile != null) {
				Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
						imageFile.getPath(), columnWidth);
				if (bitmap != null) {
					imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
				}
			}
		}
		private String getImagePath(String imageUrl){
			int lastSlashIndex=imageUrl.lastIndexOf("/");
			String imageName=imageUrl.substring(lastSlashIndex+1);
			String imageDir=Environment.getExternalStorageDirectory().getPath()+"/app51/";
			File file=new File(imageDir);
			if(!file.exists()){
				file.mkdirs();
			}
			String imagePath=imageDir+imageName;
			return imagePath;
		}
		
	}
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		if(arg1.getAction()==MotionEvent.ACTION_UP){
			Message message=new Message();
			message.obj=this;
			handler.sendMessageDelayed(message, 5);
		}
		return false;
	}
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener{
		static final List<String>displayedImages=Collections.synchronizedList(new LinkedList<String>());
		public void onLoadingComplete(String imageUrl,View view,Bitmap loadedImage){
			if(loadedImage!=null){
				ImageView imageView=(ImageView)view;
				boolean firstDisplay=!displayedImages.contains(imageUrl);
				if(firstDisplay){
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUrl);
				}
			}
		}
	}

}
