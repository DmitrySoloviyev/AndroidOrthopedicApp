package com.example.orthopedicdb;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class FragmentPreferencesActivity extends PreferenceFragment implements OnSharedPreferenceChangeListener{
	ListPreference lp;
	AlarmManager am = null;
	PendingIntent pendingIntent = null;
	final String LOG_TAG = "myLogs";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getActivity().setTitle("Настройки");
		lp = (ListPreference) findPreference("backupFrequency");
	}

	@Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	if(key.equals("allow")){
    		if(!sharedPreferences.getBoolean(key, false)){
    			lp.setValue(String.valueOf(-1));
        		if(pendingIntent != null && am != null)
    				am.cancel(pendingIntent);
        	}
    	}else if(key.equals("backupFrequency")){
    		Intent intent = new Intent(getActivity(), Receiver.class);
			pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			am = (AlarmManager) getActivity().getSystemService(Activity.ALARM_SERVICE);
			
    		Calendar timeOff9 = Calendar.getInstance();
			timeOff9.set(Calendar.HOUR_OF_DAY, 23);
			timeOff9.set(Calendar.MINUTE, 0);
			timeOff9.set(Calendar.SECOND, 0);
			
			switch (Integer.parseInt(sharedPreferences.getString(key, "0"))) {
				case 0://каждый день (повтор через 86 400 000 миллисекунд)
					am.setRepeating(AlarmManager.RTC_WAKEUP, timeOff9.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
					break;
					
				case 1://через день (повтор от текущей даты через  172 800 000 миллисекунд = 2 суток)
					am.setRepeating(AlarmManager.RTC_WAKEUP, timeOff9.getTimeInMillis(), 172800000, pendingIntent);
					break;
					
				case 2://раз в неделю (повтор от текущей даты 604 800 000 миллисекунд = 7 суток)
					am.setRepeating(AlarmManager.RTC_WAKEUP, timeOff9.getTimeInMillis(), 604800000, pendingIntent);
					break;
					
				case 3:// раз в месяц
					am.setRepeating(AlarmManager.RTC_WAKEUP, timeOff9.getTimeInMillis(), Long.parseLong("2419200000"), pendingIntent);
					break;
    		}
    	}
    }
}
