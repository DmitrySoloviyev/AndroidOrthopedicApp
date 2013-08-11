package com.example.orthopedicdb;

import java.io.File;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class StackViewFactoryAdapter implements RemoteViewsFactory{

	Context context;
	int widgetID;
	LoadImageTask imageTask;
	DB db;
	Cursor cursor;
	
	StackViewFactoryAdapter(Context ctx, Intent intent) {
	    context = ctx;
	    widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	}
	
	@Override
	public void onCreate() {
		db = new DB(context);
        db.open();
        cursor = db.getModelsGallery();
	}
	
	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews rView = new RemoteViews(context.getPackageName(), R.layout.widget_item);
		cursor.moveToPosition(position);
		rView.setImageViewBitmap(R.id.widgetIMG, decodeBitmapFromFile(cursor.getString(cursor.getColumnIndex("ModelIMG")), 120, 120));
		rView.setTextViewText(R.id.widgetModel, cursor.getString(cursor.getColumnIndex("ModelID")));
		
		Intent clickIntent = new Intent();
	    clickIntent.putExtra("OrderID", cursor.getLong(cursor.getColumnIndex("OrderID")));
	    rView.setOnClickFillInIntent(R.id.widgetIMG, clickIntent);
	    
		return rView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onDataSetChanged() {
		cursor.requery();
	}

	@Override
	public void onDestroy() {
	}

	
	
	
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
  			return BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_photo);
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
