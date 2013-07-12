package com.example.orthopedicdb;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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

	TextView tv;
	final String LOG_TAG = "myLogs";
	DB db;
	Intent intent;
	EditText quickly_search;
	Button quickly_search_submit;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // ВЫПОНЯТЬ В ДРУГОМ ПОТОКЕ!!!!!!!!!!!!!!!!!
        // подключаемся к БД
        db = new DB(this);
        db.open();

		if(db.countEmployees() == 0){
			intent = new Intent(getApplicationContext(), NewEmployee.class);
			startActivityForResult(intent, 1);
		}
		
		AlertDialog dialog = DialogScreen.getDialog(this, DialogScreen.WHATSNEW);
        dialog.show();
        
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
		tv = (TextView)findViewById(R.id.count);
		tv.setText(" " + cnt);
	}
	
	protected void onStop() {
	    super.onStop();
	    db.close();
	}
/*
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

    	switch (item.getItemId()) {
    	
			case R.id.MENU_NEW_ORDER:
				Intent newOrderIntent = new Intent();
				newOrderIntent.setClass(getApplicationContext(), NewOrderActivity.class);
				startActivity(newOrderIntent);
				break;
	
			case R.id.MENU_SEARCH:
				Intent searchIntent = new Intent();
				searchIntent.setClass(getApplicationContext(), SearchActivity.class);
				startActivity(searchIntent);
				break;
				
			case R.id.MENU_HISTORY:
				Intent allOrdersIntent = new Intent();
				allOrdersIntent.setClass(getApplicationContext(), AllOrdersActivityShort.class);
				startActivity(allOrdersIntent);
				break;
				
			case R.id.MENU_GALLERY:
	
				break;
				
			case R.id.MENU_EXIT:
				finish();
				break;
			
			case R.id.MENU_DEFAULT_SCREEN:
				
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
		db.quicklySearch(query);
	}
    
  
}// end MainActivity
