package com.example.orthopedicdb;

import java.lang.ref.WeakReference;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class ImageDetailActivity extends FragmentActivity implements OnClickListener{
	public static final String EXTRA_IMAGE = "extra_image";
    private ImagePagerAdapter mAdapter;
    private ViewPager mPager;
    DB db;
    static Cursor cursor = null;
    private LruCache<String, Bitmap> mMemoryCache;
    private Bitmap mPlaceHolderBitmap;
    int extraCurrentItem;
    
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
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.image_detail_pager_margin));
        mPager.setOffscreenPageLimit(2);
        mPager.setPageTransformer(true, new DepthPageTransformer());

        extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
        mPager.setCurrentItem(extraCurrentItem);
        
        // Set up activity to go full screen
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);
        
        final ActionBar actionBar = getActionBar();

        // Hide and show the ActionBar as the visibility changes
        mPager.setOnSystemUiVisibilityChangeListener( new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int vis) {
                        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                            actionBar.hide();
                        } else {
                            actionBar.show();
                        }
                    }
                });

        // Start low profile mode and hide ActionBar
        mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        actionBar.hide();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_order, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	cursor.moveToPosition(mPager.getCurrentItem());
        long id = cursor.getLong(cursor.getColumnIndex("_id"));
    	
    	Intent detailedOrderIntent = new Intent(this, DetailOrderActivity.class);
		detailedOrderIntent.putExtra("ID", id);
		startActivity(detailedOrderIntent);
		finish();
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
    
    public void loadBitmap(String resIdPath, ImageView imageView, int w, int h) {
        final String imageKey = resIdPath;
        final Bitmap bitmap = mMemoryCache.get(imageKey);
        if (bitmap != null) {
        	imageView.setImageBitmap(bitmap);
        } else {
//        	imageView.setImageBitmap(mPlaceHolderBitmap);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView, w, h);
            AsyncDrawable asyncDrawable = new AsyncDrawable(getResources(), null, task);
            imageView.setImageDrawable(asyncDrawable);
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
        	cursor.moveToPosition(position);
            return ImageDetailFragment.newInstance(cursor.getString(cursor.getColumnIndex("ModelIMG")), cursor.getString(cursor.getColumnIndex("ModelID")));
        }
    }
    
 /*   @Override
    public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      outState.putInt("SAVE_PAGE_NUMBER", extraCurrentItem);
    }*/
    
    
    // АНИМАЦИЯ ПЕРЕХОДА
    public class DepthPageTransformer implements ViewPager.PageTransformer {
    	private static final float MIN_SCALE = 0.75f;
    	public void transformPage(View view, float position) {
    		int pageWidth = view.getWidth();
    		if (position < -1) { // [-Infinity,-1)
    			// This page is way off-screen to the left.
    	        view.setAlpha(0);
    	    } else if (position <= 0) { // [-1,0]
    	       	// Use the default slide transition when moving to the left page
    	        view.setAlpha(1);
    	        view.setTranslationX(0);
    	        view.setScaleX(1);
    	        view.setScaleY(1);
    	    } else if (position <= 1) { // (0,1]
    	       	// Fade the page out.
    	        view.setAlpha(1 - position);
    	        // Counteract the default slide transition
    	        view.setTranslationX(pageWidth * -position);
    	        // Scale the page down (between MIN_SCALE and 1)
    	        float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
    	        view.setScaleX(scaleFactor);
    	        view.setScaleY(scaleFactor);
    	    } else { // (1,+Infinity]
    	       	// This page is way off-screen to the right.
    	        view.setAlpha(0);
    	    }
    	}
    }
    
    
    
    
    
    
    
    
    
    
    
    /**
     * A custom Drawable that will be attached to the imageView while the work is in progress.
     * Contains a reference to the actual worker task, so that it can be stopped if a new binding is
     * required, and makes sure that only the last started worker process can bind its result,
     * independently of the finish order.
     */
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    /**
     * Returns true if the current work has been canceled or if there was no work in
     * progress on this image view.
     * Returns false if the work in progress deals with the same data. The work is not
     * stopped in that case.
     */
    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            if (!bitmapData.equals(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
   

    // КЭШИРОВАНИЕ
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
    
    
    // РАБОЧИЙ КЛАСС
    public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        String data = "";
        private int width;
        private int height;
        public BitmapWorkerTask(ImageView imageView, int w, int h) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            width = w;
            height = h;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
        	Bitmap bitmap = decodeBitmapFromFile(params[0], width, height);
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
    
    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
       if (imageView != null) {
           final Drawable drawable = imageView.getDrawable();
           if (drawable instanceof AsyncDrawable) {
               final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
               return asyncDrawable.getBitmapWorkerTask();
           }
        }
        return null;
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
   
  	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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