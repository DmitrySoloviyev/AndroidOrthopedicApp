package com.example.orthopedicdb;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NewsActivity extends Activity {
	
	SharedPreferences sp;
	PackageInfo packageInfo;
	String versionCode;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        
        PackageManager pm = getPackageManager();
		try {
			packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Button btn = (Button)findViewById(R.id.news_ok);
        btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
                editor.putString("version", packageInfo.versionName);
                editor.commit();
                finish();                
			}
		});
	}
}
