package com.example.orthopedicdb;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class Receiver extends BroadcastReceiver {
	NotificationManager mNotificationManager;
	Notification notif;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Intent intent1 = new Intent(context, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent1, 0);
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			notif = new Notification(R.drawable.shoes, "Возникла ошибка", System.currentTimeMillis());
			notif.setLatestEventInfo(context, "SD-карту не обнаружена", "Ошибка резервного копирования. Подключите накопитель.", pIntent);
	    }else{
	    	FragmentBackupAndRestore.backup();
			notif = new Notification(R.drawable.shoes, "Создана копия БД", System.currentTimeMillis());
			notif.setLatestEventInfo(context, "Резервная копия БД создана", "Найти ее можно на SD-карте", pIntent);
	    }
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notif.flags |= Notification.FLAG_SHOW_LIGHTS;
		notif.ledARGB = 0xff00ff00;
		notif.ledOnMS = 300;
		notif.ledOffMS = 1000;
		mNotificationManager.notify(1, notif);
	}
}
