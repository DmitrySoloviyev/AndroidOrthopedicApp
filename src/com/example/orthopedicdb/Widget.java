package com.example.orthopedicdb;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {
	
	@Override
	  public void onEnabled(Context context) {
	    super.onEnabled(context);
	  }

	  @Override
	  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	    super.onUpdate(context, appWidgetManager, appWidgetIds);
	    for (int i : appWidgetIds) {
	        updateWidget(context, appWidgetManager, i);
	    }
	  }

	@Override
	  public void onDeleted(Context context, int[] appWidgetIds) {
	    super.onDeleted(context, appWidgetIds);
	  }

	  @Override
	  public void onDisabled(Context context) {
	    super.onDisabled(context);
	  }
	  
	  
	  private void updateWidget(Context context, AppWidgetManager appWidgetManager, int i) {
		  RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		  Intent adapter = new Intent(context, MyService.class);
		  adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i);
		  Uri data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME));
		  adapter.setData(data);
		  rv.setRemoteAdapter(R.id.stackView1, adapter);
		  setListClick(rv, context, i);
		  appWidgetManager.updateAppWidget(i, rv);
		  appWidgetManager.notifyAppWidgetViewDataChanged(i, R.id.stackView1);
	  }
	  
	  void setListClick(RemoteViews rv, Context context, int appWidgetId) {
		  Intent listClickIntent = new Intent(context, Widget.class);
		  listClickIntent.setAction("com.example.orthopedicdb.widget.itemonclick");
		  PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, 0);
		  rv.setPendingIntentTemplate(R.id.stackView1, listClickPIntent);
	  }
	  
	  
	  @Override
	  public void onReceive(Context context, Intent intent) {
	    super.onReceive(context, intent);
	    if (intent.getAction().equalsIgnoreCase("com.example.orthopedicdb.widget.itemonclick")) {
	      long itemPos = intent.getLongExtra("OrderID", -1);
	      if (itemPos != -1) {
	    	  Intent i = new Intent(context, MainActivity.class).putExtra("remoteSearchID", " OrderID='"+itemPos+"' ");
	    	  PendingIntent toMain = PendingIntent.getActivity(context, 0, i, 0);
	    	  try {
				toMain.send();
			} catch (CanceledException e) {
				e.printStackTrace();
			}
	      }
	    }
	  }
}
