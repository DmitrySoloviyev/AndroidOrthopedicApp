package com.example.orthopedicdb;

import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SearchActivity extends Activity {

	Button   submit_search;
	Boolean isCorrectInput = false;
	
	//переменные для результатов//
	String order_number;
	String model;
	String size_left;
	String size_right;
	String urk_left;
	String urk_right;
	String height_left;
	String height_right;
	String top_volume_left;
	String top_volume_right;
	String ankle_volume_left;
	String ankle_volume_right;
	String kv_volume_left;
	String kv_volume_right;
	Date   now;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        
        // текстовые поля
        EditText edit_order_number  = (EditText)findViewById(R.id.search_order_number);
        EditText edit_model			= (EditText)findViewById(R.id.search_model);
        EditText edit_size		  	= (EditText)findViewById(R.id.search_size);
        EditText edit_urk		  	= (EditText)findViewById(R.id.search_urk);
        EditText edit_height		= (EditText)findViewById(R.id.search_height);
        EditText edit_top_volume	= (EditText)findViewById(R.id.search_TopVolume);
        EditText edit_ankle_volume  = (EditText)findViewById(R.id.search_AnkleVolume);
        EditText edit_kv_volume	    = (EditText)findViewById(R.id.search_KvVolume);
        EditText edit_customer	    = (EditText)findViewById(R.id.search_customer);
        
        // флажки
        CheckBox edit_mk = (CheckBox)findViewById(R.id.mk);
        CheckBox edit_mt = (CheckBox)findViewById(R.id.mt);
        CheckBox edit_mn = (CheckBox)findViewById(R.id.mn);
        CheckBox edit_mw = (CheckBox)findViewById(R.id.mw);
        CheckBox edit_ma = (CheckBox)findViewById(R.id.ma);

        // делаем кнопку неактивной пока не получим все данные в верном формате
        submit_search = (Button)findViewById(R.id.submit_search);
        submit_search.setEnabled(isCorrectInput);
        
        // получаем и обрабатываем данные, если все верно делаем кнопку активной для отправки информации
        final String order_number = edit_order_number.getText().toString();
        final String model		  = edit_model.getText().toString();
        final String size 		  = edit_size.getText().toString();
        final String urk 		  = edit_urk.getText().toString();
        final String height 	  = edit_height.getText().toString();
        final String top_volume   = edit_top_volume.getText().toString();
        final String ankle_volume = edit_ankle_volume.getText().toString();
        final String kv_volume 	  = edit_kv_volume.getText().toString();
        
        /*
         * обрабатываем посыл запроса к БД
         */
        submit_search.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			//...
    		}
    	});
        
        
        if(isCorrectInput){
        	submit_search.setEnabled(isCorrectInput);
        }
	} // end OnCreate
	
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
    
}// end SearchActivity