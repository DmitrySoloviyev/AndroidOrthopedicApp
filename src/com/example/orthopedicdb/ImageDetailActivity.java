package com.example.orthopedicdb;

import java.lang.ref.WeakReference;

import com.example.orthopedicdb.FragmentImageGridActivity.AsyncDrawable;
import com.example.orthopedicdb.FragmentImageGridActivity.BitmapWorkerTask;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

public class ImageDetailActivity extends FragmentActivity {/*
	public static final String EXTRA_IMAGE = "extra_image";

    private ImagePagerAdapter mAdapter;
    private ViewPager mPager;
    DB db;
    Cursor cursor;
    private LruCache<String, Bitmap> mMemoryCache;
    private Bitmap mPlaceHolderBitmap;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager); // Contains just a ViewPager

        db = new DB(this);
        db.open();
        cursor = db.getModelsGallery();
        
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        
        mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty_photo);
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), cursor.getCount());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
    }
    
    public void loadBitmap(String resIdPath, ImageView imageView) {
        final String imageKey = resIdPath;
        final Bitmap bitmap = mMemoryCache.get(imageKey);
        if (bitmap != null) {
        	imageView.setImageBitmap(bitmap);
        } else {
        	imageView.setImageBitmap(mPlaceHolderBitmap);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(resIdPath);
        }
    }


    public static class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageDetailFragment.newInstance(position);
        }
    }
    
    
    
    
    
    
    // include BitmapWorkerTask class
 // РАБОЧИЙ КЛАСС
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        String data = "";
        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
        	Bitmap bitmap = decodeBitmapFromFile(params[0], 128, 128);
        	if(bitmap == null)
        		bitmap = mPlaceHolderBitmap;
        	else{
        		addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
        	}
            return bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    

    

    // МЕТОДЫ ПО ДЕКОДИРОВАНИЮ ИЗОБРАЖЕНИЯ
    public Bitmap decodeBitmapFromFile(String imagePath, int reqWidth, int reqHeight) {
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
  	}*/
}





































