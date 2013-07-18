package com.example.orthopedicdb;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	TextView countOrders;
	final String LOG_TAG = "myLogs";
	DB db;
	Intent intent;
	EditText quickly_search;
	Button quickly_search_submit;
	SharedPreferences sp;
	PackageInfo packageInfo;
	
	
	@SuppressLint("CommitPrefEdits")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        PackageManager pm = getPackageManager();
		try {
			packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = packageInfo.versionName;
		
		TextView tvversionCode = (TextView)findViewById(R.id.versionCode);
		tvversionCode.setText("Version "+version);

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
		
		//AlertDialog dialog = DialogScreen.getDialog(this, DialogScreen.WHATSNEW);
        //dialog.show();
        
        quickly_search = (EditText)findViewById(R.id.quickly_search);
        quickly_search_submit = (Button)findViewById(R.id.quickly_search_submit);
        quickly_search_submit.setOnClickListener(this);
    }//END ONCREATE
	
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	}
	
	protected void onStart() {
	    super.onStart();
	    db = new DB(this);
        db.open();
	    int cnt = db.countOrders();
	    countOrders = (TextView)findViewById(R.id.count);
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

    	switch (item.getItemId()) {
    	
			case R.id.MENU_NEW_ORDER:
				Intent newOrderIntent = new Intent();
				newOrderIntent.setClass(getApplicationContext(), NewOrderActivity.class);
				startActivity(newOrderIntent);
				finish();
				break;
	
			case R.id.MENU_SEARCH:
				Intent searchIntent = new Intent();
				searchIntent.setClass(getApplicationContext(), SearchActivity.class);
				startActivity(searchIntent);
				finish();
				break;
				
			case R.id.MENU_HISTORY:
				Intent allOrdersIntent = new Intent();
				allOrdersIntent.setClass(getApplicationContext(), AllOrdersActivityShort.class);
				startActivity(allOrdersIntent);
				finish();
				break;
				
			case R.id.MENU_GALLERY:
	
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
