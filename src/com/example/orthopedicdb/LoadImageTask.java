package com.example.orthopedicdb;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class LoadImageTask extends AsyncTask<Integer, Void, Bitmap> {

	Context mContext;
	private final WeakReference<ImageView> imageViewReference;
	ProgressBar mPb;
	String mPath;
	
	LoadImageTask(Context context, String Path, ImageView imageView, ProgressBar pb){
		mContext = context;
		mPath = Path;
		imageViewReference = new WeakReference<ImageView>(imageView);
		mPb = pb;
	}
	
	@Override
	protected void onPreExecute() {
		mPb.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected Bitmap doInBackground(Integer... param) {
		return decodeBitmapFromFile(mPath, param[0], param[1]);
	}

	@Override
    protected void onPostExecute(Bitmap bitmap) {
		final ImageView imageView = imageViewReference.get();
        if (imageView != null) {
        	mPb.setVisibility(View.GONE);
        	imageView.setImageBitmap(bitmap);
        }
    }
	
	// ДЕКОДИРОВАНИЕ ИЗОБРАЖЕНИЯ //	
	public boolean imgExists(String path){
		File file = new File(path);
		if(file.exists()){
	 	  return true;
		}else{
	 	  return false;
		}
	}
	
  	public Bitmap decodeBitmapFromFile(String imagePath, int reqWidth, int reqHeight) {
  		if(!imgExists(imagePath)){
  			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.empty_photo);
  		}
  	    // получаем картинку и определяем ее высоту и ширину
  	    BitmapFactory.Options options = new BitmapFactory.Options();
  	    options.inJustDecodeBounds = true;
  	    BitmapFactory.decodeFile(imagePath, options);
  	    
  	    // Calculate inSampleSize
  	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

  	    // Decode bitmap with inSampleSize set
  	    options.inJustDecodeBounds = false;
  	    return BitmapFactory.decodeFile(imagePath, options);
  	}
   
  	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    return inSampleSize;
  	}
}
