package com.zzf.app51.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MyImageView extends ImageView {
	private    Matrix mMatrix;
    private float bWidth;
    private float bHeight;
    
    private int dWidth;
    private int  dHeight;
    private float initScale;
    private float mScale;
    private float scale;
    ImageState mapState = new ImageState();
    private float oldDist;
    PointF mStart = new PointF();
    private Bitmap mBitmap;
    float[] values = new float[9];
    
    Matrix initMatrix;
    Matrix mSavedMatrix;
    
	public MyImageView(Context context) {
		super(context);
	     this.setScaleType(ScaleType.MATRIX);
	}
    public void init(MotionEvent event){
        mStart.set(event.getX(), event.getY());
        mSavedMatrix.set(mMatrix);
        
    }
	float rate=1.0f;
	 
    public void setView() {
        this.setImageMatrix(mMatrix);
        
        Rect rect = this.getDrawable().getBounds();
        this.getImageMatrix().getValues(values);
        bWidth = rect.width() * values[0];
        bHeight = rect.height() * values[0];

        mapState.left = values[2];
        mapState.top = values[5];
        mapState.right = mapState.left + bWidth;
        mapState.bottom = mapState.top + bHeight;
    }
    private float s=0.9f;
    public void setScale(){
    
        float sX = dWidth / 2;
        float sY = dHeight / 2;

       mMatrix.postScale(s, s, sX, sY);
       setView();
    }
    public void setScreenSize(Context context, int width, int height,Bitmap bitmap) {
    	 mBitmap =bitmap;
    	dWidth = width;
        dHeight = height;
        setImageBitmap(mBitmap);
//        gd = new GestureDetector(context, new LearnGestureListener());

        bWidth = mBitmap.getWidth();
        bHeight = mBitmap.getHeight();
        // mView = (ImageView) findViewById(R.id.imageView);
        float xScale = (float) dWidth / bWidth;
        float yScale = (float) dHeight / bHeight;
        mScale = xScale <= yScale ? xScale : yScale;
      scale = mScale < 1 ? mScale : 1;
        initScale = scale;
        mMatrix = new Matrix();
        mSavedMatrix = new Matrix();
      
        mMatrix.postTranslate((dWidth - bWidth) / 2, (dHeight - bHeight) / 2);

        float sX = dWidth / 2;
        float sY = dHeight / 2;
        
        mSavedMatrix.set(mMatrix);
       mMatrix.postScale(scale, scale, sX, sY);
        setView();
    }
    
   
	private float spacing(MotionEvent event) {
		float x = 0;
		float y = 0;
		try {
			x = event.getX(0) - event.getX(1);
			y = event.getY(0) - event.getY(1);
		} catch (IllegalArgumentException e) {
		}
		return FloatMath.sqrt(x * x + y * y);
	}
    float backScale;
    
    public void zoom(MotionEvent event) {

        float newDist = spacing(event);
    	if((mapState.right-mapState.left)>4*dWidth&&newDist>oldDist)
    		return;
        if (newDist > 10f&&Math.abs((newDist-oldDist))>10f) {
            scale = newDist / oldDist;
            if (scale < 1) {
                mMatrix.postScale(scale, scale, dWidth / 2, dHeight / 2);
            } else {
                mMatrix.postScale(scale, scale, dWidth / 2, dHeight / 2);
            }
            oldDist=newDist;
        }

        setView();
    }
    
    /**
     * @return the oldDist
     */
    public float getOldDist(MotionEvent event) {
        this.oldDist = this.spacing(event);
        if (oldDist > 10f) {
                mSavedMatrix.set(mMatrix);
        }
        backScale=oldDist;
        return oldDist;
    }
    public void backScale(){
    	 scale = backScale / oldDist;
         if (scale < 1) {
          //   mMatrix.postScale(scale, scale, dWidth / 2, dHeight / 2);
         } else {
        	 if(mapState.right-mapState.left<=dWidth){
        		  scale= dWidth/(mapState.right-mapState.left);
        	
        		 mMatrix.postScale(scale, scale, dWidth / 2, dHeight / 2);
        	      	float h=(dHeight-(mapState.bottom-mapState.top))/2;
        	      	float w=(dWidth-(mapState.right-mapState.left))/2;
        	 		 mMatrix.postTranslate(w-mapState.left,h-mapState.top);
        	 }
         }
 		 setView();
    }
    public void backDrag(){
        if (mapState.left >= 0 || mapState.right <= dWidth
               ||mapState.top >= 0 || mapState.bottom <= dHeight) {
	      	float h=(dHeight-(mapState.bottom-mapState.top))/2;
	      	float w=(dWidth-(mapState.right-mapState.left))/2;
	 		 mMatrix.postTranslate(w-mapState.left,h-mapState.top);
	 		 setView();
        }
    }
    //�϶�
    public void drag(MotionEvent event){

        mMatrix.set(mSavedMatrix);
        if ((mapState.left <= 0 || mapState.right >= dWidth)
                && (mapState.top <= 0 || mapState.bottom >= dHeight)) {
            mMatrix.postTranslate(event.getX() - mStart.x, event.getY()
                    - mStart.y);
        } else if (mapState.top <= 0 || mapState.bottom >= dHeight) {
            mMatrix.postTranslate(0, event.getY() - mStart.y);
        } else if (mapState.left <= 0 || mapState.right >= dWidth) {
            mMatrix.postTranslate(event.getX() - mStart.x, 0);
        }
        else{
        	  mMatrix.postTranslate(event.getX() - mStart.x, event.getY()-mStart.y);
        }
        //mStart.x=event.getX();
       // mStart.y=event.getY();
        setView();
        
    }
    private class ImageState {
        private float left;
        private float top;
        private float right;
        private float bottom;
    }
}