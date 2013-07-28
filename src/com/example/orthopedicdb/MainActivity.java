package com.example.orthopedicdb;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	TextView countOrders;
	final String LOG_TAG = "myLogs";
	DB db;
	Intent intent;
	EditText quickly_search;
	Button quickly_search_submit;
	SharedPreferences sp;
	PackageInfo packageInfo;
	View page1, page2;
	
	@SuppressLint("CommitPrefEdits")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // левое меню
        List<View> pages = new ArrayList<View>();
        page1 = getLayoutInflater().inflate(R.layout.left_menu, null); 
        pages.add(page1);
        
        page2 = getLayoutInflater().inflate(R.layout.activity_main, null); 
        pages.add(page2);
        
        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setCurrentItem(1);   
        setContentView(viewPager);
        
        PackageManager pm = getPackageManager();
		try {
			packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = packageInfo.versionName;
		TextView tvversionCode = (TextView)page2.findViewById(R.id.versionCode);
		tvversionCode.setText("Version "+version);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.leafanim);
		tvversionCode.startAnimation(anim);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String flag = sp.getString("version", "0");
        
        if (!flag.equals(version)) {
            Intent showNews = new Intent(getApplicationContext(), NewsActivity.class);
            startActivity(showNews);
        }
        
        // подключаемся к БД
        db = new DB(this);
        db.open();

		if(db.countEmployees() == 0){
			intent = new Intent(getApplicationContext(), NewEmployee.class);
			startActivityForResult(intent, 1);
		}
        quickly_search = (EditText)page2.findViewById(R.id.quickly_search);
        quickly_search_submit = (Button)page2.findViewById(R.id.quickly_search_submit);
        quickly_search_submit.setOnClickListener(this);
    }//END ONCREATE

	
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
	            float scaleFactor = MIN_SCALE
	                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	}
	
	
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	}
	
	protected void onStart() {
	    super.onStart();
	    db = new DB(this);
        db.open();
        int cnt = db.countOrders();
	    countOrders = (TextView)page2.findViewById(R.id.count);
	    countOrders.setText(" " + cnt);
	}
	
	protected void onStop() {
	    super.onStop();
	    db.close();
	}
	
	public void onBackPressed() {
		finish();
	}
	
	//меню
  	public boolean onCreateOptionsMenu(Menu menu){
  		MenuInflater menuInflater = getMenuInflater();
  		menuInflater.inflate(R.menu.main, menu);
  		return true;
  	}
  	
	// обработка нажатия пункта меню
    public boolean onOptionsItemSelected(MenuItem item){
    	Intent intent = new Intent();
    	switch (item.getItemId()) {
    	
			case R.id.MENU_NEW_ORDER:
				intent.setClass(getApplicationContext(), NewOrderActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				finish();
				break;
	
			case R.id.MENU_SEARCH:
				intent.setClass(getApplicationContext(), SearchActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				finish();
				break;
				
			case R.id.MENU_HISTORY:
				intent.setClass(getApplicationContext(), AllOrdersActivityShort.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				finish();
				break;
				
			case R.id.MENU_GALLERY:
				intent.setClass(getApplicationContext(), GalleryView.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				finish();
				break;
				
			case R.id.MENU_EXIT:
				finish();
				break;
		}
		return true;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (data == null) {
	    	Toast.makeText(this, "Модельер не добавлен! Добавьте модельера", Toast.LENGTH_LONG).show();
	    	finish();
	    	return;
	    }

	    if (resultCode == RESULT_OK) {
	       	String surname 		= data.getStringExtra("surname");
	        String firstname 	= data.getStringExtra("firstname");
	        String patronymic 	= data.getStringExtra("patronymic");
	        db.addNewEmployee(surname, firstname, patronymic);
	        Toast.makeText(this, "Модельер "+surname +" "+firstname+" "+patronymic+" добавлен!", Toast.LENGTH_SHORT).show();
	    } else {
	    	Toast.makeText(this, "Модельер не добавлен! Добавьте модельера", Toast.LENGTH_LONG).show();
	    	finish();
	    }
	}


	@Override
	public void onClick(View v) {
		String query = quickly_search.getText().toString().trim();
		if(query.length() == 0){
			return;
		}
		Intent quick_search_result = new Intent(this, AllOrdersActivityShort.class);
		quick_search_result.putExtra("QUICK_SEARCH_QUERY", query);
		startActivity(quick_search_result);
		finish();
	}
    
  
}// end MainActivity
