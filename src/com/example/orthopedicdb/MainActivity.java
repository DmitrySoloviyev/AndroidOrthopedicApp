package com.example.orthopedicdb;

import com.example.orthopedicdb.FragmentSearchActivity.OnExtendedSearchClickListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnExtendedSearchClickListener{

	DB db;
	Intent intent;
	SharedPreferences sp;
	String version;
	String WHERE = "";
	int whichSearch;
	private final int EXTRESULT   = 1;
	private final int QUICKRESULT = 2;
	DrawerLayout mDrawerLayout;
	PackageInfo packageInfo;
    private CharSequence mTitle;
	ListView mDrawerList;
	DialogFragment progressDialog;
	String[] items = new String[] {"Новый заказ", "Поиск", "Все заказы", "Галерея"};
	
	private ActionBarDrawerToggle mDrawerToggle;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        db = new DB(this);
        db.open();

        PackageManager pm = getPackageManager();
		try {
			packageInfo = pm.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		version = packageInfo.versionName;
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String flag = sp.getString("version", "0");
        
        if (!flag.equals(version)) {
            Intent showNews = new Intent(this, NewsActivity.class);
            startActivity(showNews);
        }

        if(db.countEmployees() == 0){
			intent = new Intent(this, NewEmployee.class);
			startActivityForResult(intent, 1);
		}

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setSubtitle("Записей в базе: "+db.countOrders());

	
     	mDrawerLayout  = (DrawerLayout)findViewById(R.id.drawer_layout);
     	mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

     	mDrawerList  = (ListView) findViewById(R.id.left_drawer);
     	MenuAdapter menuAdapter = new MenuAdapter(this, items);

     	mDrawerList.setAdapter(menuAdapter);
     	mDrawerList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});

     	mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                getActionBar().setSubtitle("Записей в базе: " + db.countOrders());
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Меню");
                getActionBar().setSubtitle("Version: " + version);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
       
        // TODO тут ориентируемся по настройкам:
        // ОБЯЗАТЕЛЬНО ВЕСТИ ПРОВЕКУ НА НАЛИЧИЕ ХОТЯ БЫ ОДНОГО МОДЕЛЬЕРА ЕСЛИ В selectItem() ПЕРЕДАЕТСЯ 0
        
        if (savedInstanceState != null){
        	WHERE = savedInstanceState.getString("WHERE");
        	whichSearch = savedInstanceState.getInt("SEARCHID");
        	showSearchResults(whichSearch);
        }else
        	selectItem(1);
	}// END ONCREATE
	

/*	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }
	

	/** ЗАГРУЖАЕМ ОСНОВНЫЕ ФРАГМЕНТЫ */
    private void selectItem(final int position) {
    	
    	new AsyncTask<Integer, Void, Fragment>() {
    		@Override
	        protected void onPreExecute() {
    			super.onPreExecute();
    			mTitle = items[position];
	            getActionBar().setTitle(mTitle);
	            mDrawerLayout.closeDrawer(mDrawerList);
	            progressDialog = FragmentDialogScreen.newInstance(FragmentDialogScreen.DIALOG_PROGRESS);
	            progressDialog.show(getSupportFragmentManager(), "pdlg");
	        }
			@Override
			protected Fragment doInBackground(Integer... params) {
				Fragment fragment = new Fragment();
				switch (params[0]) {
					case 0:// НОВЫЙ ЗАКАЗ
						fragment = new FragmentNewOrderActivity();
						break;
					case 1: // ПОИСК
						fragment = new FragmentSearchActivity();
						break;
					case 2:// ВСЕ ЗАПИСИ
						fragment = new FragmentAllOrdersActivity();
						break;
					case 3:// ГАЛЕРЕЯ
						fragment = new FragmentGalleryActivity();
						break;
					default:
						break;
				}
				return fragment;
			}
			@Override
	    	protected void onPostExecute(Fragment result) {
	    		super.onPostExecute(result);
	    		FragmentManager fragmentManager = getSupportFragmentManager();
	    		fragmentManager.beginTransaction().replace(R.id.content_frame, result).commit();
	    		progressDialog.dismiss();
			}
		}.execute(position);
    	
    	
    	/*
        mDrawerList.setItemChecked(position, true);
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	Fragment fragment = new Fragment();
		switch (position) {
			case 0:// НОВЫЙ ЗАКАЗ
				fragment = new FragmentNewOrderActivity();
				break;
			case 1: // ПОИСК
				fragment = new FragmentSearchActivity();
				break;
			case 2:// ВСЕ ЗАПИСИ
				fragment = new FragmentAllOrdersActivity();
				break;
			case 3:// ГАЛЕРЕЯ
				fragment = new FragmentGalleryActivity();
				break;
			default:
				break;
		}
    	fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    	mTitle = items[position];
        getActionBar().setTitle(mTitle);
        mDrawerLayout.closeDrawer(mDrawerList);*/
    }
	
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	}
	
	protected void onStart() {
	    super.onStart();
	    db = new DB(this);
        db.open();
	}
	
	protected void onStop() {
	    super.onStop();
	    db.close();
	}
	// РЕЗУЛЬТАТ ИЗ АКТИВИТИ
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (data == null) {
	    	Toast.makeText(this, "Модельер не добавлен! Добавьте модельера", Toast.LENGTH_SHORT).show();
	    	intent = new Intent(this, NewEmployee.class);
			startActivityForResult(intent, 1);
	    	return;
	    }
	    if (resultCode == Activity.RESULT_OK) {
	       	String surname 		= data.getStringExtra("surname");
	        String firstname 	= data.getStringExtra("firstname");
	        String patronymic 	= data.getStringExtra("patronymic");
	        db.addNewEmployee(surname, firstname, patronymic);
	        Toast.makeText(this, "Модельер "+surname +" "+firstname+" "+patronymic+" добавлен!", Toast.LENGTH_SHORT).show();
	    } else {
	    	Toast.makeText(this, "Модельер не добавлен! Добавьте модельера", Toast.LENGTH_LONG).show();
	    }
	}


	@Override
	public void getExtWhere(String where) {
		this.WHERE = where;
		whichSearch = 1;
		showSearchResults(EXTRESULT);
	}


	private void showSearchResults(int searchID) {
		switch (searchID) {
		case EXTRESULT:
//			FragmentExtenedSearchActivity details = (FragmentExtenedSearchActivity) getSupportFragmentManager().findFragmentById(R.id.content_frame);
//			if (details == null || details.getPosition() != searchID) {
			FragmentExtenedSearchActivity extSearch = FragmentExtenedSearchActivity.newInstance(WHERE);
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, extSearch).commit();
				mTitle = "Результаты поиска";
				getActionBar().setTitle(mTitle);
//	    }
			break;
		case QUICKRESULT:
			
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString("WHERE", WHERE);
	    outState.putInt("SEARCHID", whichSearch);
	}
}// end MainActivity
