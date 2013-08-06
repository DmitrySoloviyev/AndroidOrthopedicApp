package com.example.orthopedicdb;

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

import com.example.orthopedicdb.FragmentExtenedSearchActivity.OnExtendedSearchClickListener;

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
	final int REQUEST_ADD_MATERIAL = 1;
	String[] items = new String[] {"Новый заказ", "Расширенный поиск", "Обычный поиск", "Все заказы", "Галерея","Настройки"};
	
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
			startActivityForResult(intent, REQUEST_ADD_MATERIAL);
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
				selectMenuItem(position);
			}
		});

     	mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.drawer_shadow, R.string.drawer_open, R.string.drawer_close) {
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
        
        if (savedInstanceState != null){
        	WHERE = savedInstanceState.getString("WHERE");
        	whichSearch = savedInstanceState.getInt("SEARCHID");
        	showSearchResults(whichSearch);
        }else{
        	if( getIntent().hasExtra("SELECTMENUITEM") )
        		selectMenuItem( getIntent().getIntExtra("SELECTMENUITEM", 3) );
        	else
        		selectMenuItem(Integer.valueOf(sp.getString("activityList", "1")));// вызываем фрагмент по-умолчанию, считывая настройки приложения
        }
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
    
	/** ЗАГРУЖАЕМ ОСНОВНЫЕ ФРАГМЕНТЫ */
    private void selectMenuItem(final int position) {

    	new AsyncTask<Integer, Void, Fragment>() {
    		@Override
	        protected void onPreExecute() {
    			super.onPreExecute();
    			progressDialog = FragmentDialogScreen.newInstance(FragmentDialogScreen.DIALOG_PROGRESS);
	            progressDialog.show(getSupportFragmentManager(), "pdlg");
    			mTitle = items[position];
	            getActionBar().setTitle(mTitle);
	            mDrawerLayout.closeDrawer(mDrawerList);
	        }
			@Override
			protected Fragment doInBackground(Integer... params) {
				Fragment fragment = null;

				switch (params[0]) {
					case 0:// НОВЫЙ ЗАКАЗ
						fragment = (Fragment) new FragmentNewOrderActivity();
						break;
					case 1: // РАСШИРЕННЫЙ ПОИСК
						fragment = new FragmentExtenedSearchActivity();
						break;
					case 2: // ОБЫЧНЫЙ ПОИСК
						fragment = new FragmentQuickSearchActivity();
						break;
					case 3:// ВСЕ ЗАПИСИ
						fragment = new FragmentAllOrdersActivity();
						break;
					case 4:// ГАЛЕРЕЯ
						fragment = new FragmentImageGridActivity();
						break;
					default:
						break;
				}
					return fragment;
			}
			@Override
	    	protected void onPostExecute(Fragment result) {
	    		super.onPostExecute(result);
	    		if(result==null){
	    			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Fragment()).commit();//удаляем фрагмент
			        getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentPreferencesActivity()).commit();//добавляем фрагмент с настройками
	    		}else{
	    			FragmentManager fragmentManager = getSupportFragmentManager();
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, result).commit();
	    		}
	    		progressDialog.dismiss();
			}
		}.execute(position);
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
    	if(requestCode == REQUEST_ADD_MATERIAL){
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
			FragmentExtenedRESULTSearchActivity extSearch = FragmentExtenedRESULTSearchActivity.newInstance(WHERE);
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, extSearch).commit();
			mTitle = "Результаты поиска";
			getActionBar().setTitle(mTitle);
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
