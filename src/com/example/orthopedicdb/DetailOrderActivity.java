package com.example.orthopedicdb;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class DetailOrderActivity extends Activity {	
	long ID;
	ListView lv;
	DetailedOrderAdapter scAdapter;
	Cursor cursor;
	DB db;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_orders);
		this.setTitle("Подробная информация");
		// подключаемся к БД
	    db = new DB(this);
	    db.open();

	    Intent intent = getIntent();
	    ID = intent.getLongExtra("ID", 1);

	 	cursor = db.getDetailedOrderById(ID);
	 	startManagingCursor(cursor);

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
 	    								"ModelIMG"};
 	    int[] to = new int[] { R.id.shortOrderID,  
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
 	    					   R.id.detailedModel,
 	    					   R.id.detailedModelIMG }; 
 		
 	    scAdapter = new DetailedOrderAdapter(this, R.layout.detailed_item, cursor, from, to);
	    lv = (ListView)findViewById(R.id.orders_list);
		// устанавливаем режим выбора пунктов списка 
		lv.setAdapter(scAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.context, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()) {
    		case R.id.edit:
    			Intent editOrderIntent = new Intent(this, EditOrderActivity.class);
				editOrderIntent.putExtra("ID", ID);
				startActivity(editOrderIntent);
    			break;
    		case R.id.delete:
    			db.deleteOrderById(new String[]{String.valueOf(ID)});
    		    Toast.makeText(getApplicationContext(), "Запись успешно удалена!", Toast.LENGTH_SHORT).show();
    		    finish();
    			break;
    	}
    	return true;
    }
}
