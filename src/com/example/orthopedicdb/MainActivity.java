package com.example.orthopedicdb;

import java.util.List;
import java.util.Vector;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.orthopedicdb.FragmentMenu.OnLeftMenuItemListener;

public class MainActivity extends FragmentActivity implements OnLeftMenuItemListener {

	TextView countOrders;
	final String LOG_TAG = "myLogs";
	DB db;
	Intent intent;
	EditText quickly_search;
	Button quickly_search_submit;
	SharedPreferences sp;
	PackageInfo packageInfo;
	View page1, page2;
	
	
	ViewPager pager;
	PagerAdapter pagerAdapter;
	Vector<Fragment> fragments;
	
	@SuppressLint("CommitPrefEdits")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
/*        
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
        quickly_search_submit.setOnClickListener(this);*/
        
/*        
        Fragment frag = new FragmentMenu();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frg, frag);
        ft.commit();
*/        
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (isLarge()) {
        	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        	
        	Fragment frag1 = new FragmentMenu();
            ft.add(R.id.fragment1, frag1);
            
            Fragment frag2 = new FragmentNewOrderActivity();
            ft.add(R.id.fragment2, frag2);
            
            ft.commit();
        }else{
        	fragments = new Vector<Fragment>();
            fragments.add(Fragment.instantiate(this, FragmentMenu.class.getName()));
            fragments.add(Fragment.instantiate(this, FragmentNewOrderActivity.class.getName()));
            
            pager = (ViewPager) findViewById(R.id.pager);
            pagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
            pager.setAdapter(pagerAdapter);
            pager.setCurrentItem(1);
            pager.setOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
					switch (position) {
						case 0:
							getActionBar().setTitle("Меню");
							break;
						case 1:
							getActionBar().setTitle("Новая запись");
							break;
						default:
							break;
					}
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
        
        
        
        
    }//END ONCREATE
	
	boolean isLarge() {
	    return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	@Override
	public void callFragmentByPosition(int item) {
		if (isLarge()) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			switch (item) {
				case 0:// НОВЫЙ ЗАКАЗ
					Fragment fragmentNewOrder = new FragmentNewOrderActivity();
		            ft.add(R.id.fragment2, fragmentNewOrder);
		            ft.commit();
					break;
				case 1: // ПОИСК
					Fragment fragmentSearch = new FragmentSearchActivity();
					ft.replace(R.id.fragment2, fragmentSearch);
					ft.commit();
					break;
				case 2:// ВСЕ ЗАПИСИ
//					Fragment fragmentAllOrders = new FragmentAllOrdersActivity();
//					ft.replace(R.id.fragment2, fragmentSearch);
					break;
				case 3:// ГАЛЕРЕЯ
//					Fragment fragmentGallery = new FragmentGalleryActivity();
//					ft.replace(R.id.fragment2, fragmentSearch);
					break;
				default:
					break;
			}
		}else{
			switch (item) {
				case 0:// НОВЫЙ ЗАКАЗ
					fragments.remove(1);
					fragments.add(Fragment.instantiate(this, FragmentNewOrderActivity.class.getName()));
					pagerAdapter.notifyDataSetChanged();
					pager.setCurrentItem(1);
					break;
				case 1: // ПОИСК
					fragments.add(Fragment.instantiate(this, FragmentSearchActivity.class.getName()));
					
//					fragments.remove(1);
					pagerAdapter.notifyDataSetChanged();
					pager.setCurrentItem(2);
					break;
				case 2:// ВСЕ ЗАПИСИ
//					fragments.remove(1);
//					fragments.add(Fragment.instantiate(this, FragmentAllOrdersActivity.class.getName()));
//					pager.setCurrentItem(1);
					break;
				case 3:// ГАЛЕРЕЯ
//					fragments.remove(1);
//					fragments.add(Fragment.instantiate(this, FragmentGalleryActivity.class.getName()));
//					pager.setCurrentItem(1);
					break;
				default:
					break;
			}
		}
	}
	
	class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

    }
/*	
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
*/	
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
				intent.setClass(getApplicationContext(), FragmentSearchActivity.class);
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
    
/*    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
*/    
  
}// end MainActivity
