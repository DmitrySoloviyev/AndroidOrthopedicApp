package com.example.orthopedicdb;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DetailOrderActivity extends Activity {

	long ID;
	ListView lv;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;
	DB db;
	final String LOG_TAG = "myLogs";
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_orders);
		
		// подключаемся к БД
	    db = new DB(this);
	    db.open();
	    
	    Intent intent = getIntent();
	    ID = intent.getLongExtra("ID", 1);

	 	cursor = db.getDetailedOrderById(ID);
	 	startManagingCursor(cursor);
//	 	cursor.moveToFirst();

 	    String[] from = new String[] { "OrderID", 
 	    								"Model", 
 	    								"Material", 
 	    								"SizeLEFT",
 	    								"SizeRIGHT",
 	    								"UrkLEFT",
 	    								"UrkRIGHT",
 	    								"HeightLEFT",
 	    								"HeightRIGHT",
 	    								"TopVolumeLEFT",
 	    								"TopVolumeRIGHT",
 	    								"AnkleVolumeLEFT",
 	    								"AnkleVolumeRIGHT",
 	    								"KvVolumeLEFT",
 	    								"KvVolumeRIGHT",
 	    								"CustomerSN",
 	    								"CustomerFN",
 	    								"CustomerP",
 	    								"EmployeeSN",
 	    								"EmployeeFN",
 	    								"EmployeeP",
 	    								"ModelIMG" };
 	    int[] to = new int[] { R.id.detailedOrderID, 
 	    					   R.id.detailedModel, 
 	    					   R.id.detailedMaterial,
 	    					   R.id.detailedSizeLeft,
 	    					   R.id.detailedSizeRight,
 	    					   R.id.detailedUrkLeft,
 	    					   R.id.detailedUrkRight,
 	    					   R.id.detailedHeightLeft,
 	    					   R.id.detailedHeightRight,
 	    					   R.id.detailedTopVolumeLeft,
 	    					   R.id.detailedTopVolumeRight,
 	    					   R.id.detailedAnkleVolumeLeft,
 	    					   R.id.detailedAnkleVolumeRight,
 	    					   R.id.detailedKvVolumeLeft,
 	    					   R.id.detailedKvVolumeRight,
 	    					   R.id.detailedCustomerSN,
 	    					   R.id.detailedCustomerFN,
 	    					   R.id.detailedCustomerP,
 	    					   R.id.detailedEmployeeSN,
 	    					   R.id.detailedEmployeeFN,
 	    					   R.id.detailedEmployeeP,
 	    					   R.id.updateModelIMG };
 		
 	    scAdapter = new SimpleCursorAdapter(this, R.layout.detailed_item, cursor, from, to);
	    
	    
	    
	    lv = (ListView)findViewById(R.id.orders_list);
		// устанавливаем режим выбора пунктов списка 
		lv.setAdapter(scAdapter);
	}
	
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	}
	
	protected void onStop() {
	    super.onStop();
	    db.close();
	}
	
	public void onBackPressed() {
		Intent allOrdersIntent = new Intent(getApplicationContext(), AllOrdersActivityShort.class);
		startActivity(allOrdersIntent);
		finish();
	}
	
	// СОЗДАЕМ МЕНЮ
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0, 1, 0, "Редактировать");
		menu.add(0, 2, 0, "Удалить");
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){

    	switch (item.getItemId()) {
    		case 1:
    			Intent editOrderIntent = new Intent(getApplicationContext(), EditOrderActivity.class);
				editOrderIntent.putExtra("ID", ID);
				startActivity(editOrderIntent);
				finish();
    			break;
    		case 2:
    			db.deleteOrderById(ID);
    		    Toast.makeText(getApplicationContext(), "Запись успешно удалена!", Toast.LENGTH_LONG).show();
    		    Intent allOrdersIntent = new Intent();
				allOrdersIntent.setClass(getApplicationContext(), AllOrdersActivityShort.class);
				startActivity(allOrdersIntent);
				finish();
    			break;
    	}
    	return true;
    }
}