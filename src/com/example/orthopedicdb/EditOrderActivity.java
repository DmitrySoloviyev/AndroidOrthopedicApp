package com.example.orthopedicdb;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class EditOrderActivity extends Activity implements OnClickListener {

	long ID;
	ListView lv;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;
	DB db;
	final String LOG_TAG = "myLogs";
	Button submit_update;
	Button modelIMG;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_orders);

		Intent intent = getIntent();
		ID = intent.getLongExtra("ID", 1);

		// подключаемся к БД
	    db = new DB(this);
	    db.open();
		
	    cursor = db.getDetailedOrderById(ID);
	 	startManagingCursor(cursor);
	    
		String[] from = new String[] { "OrderID", "Model", "Material",
				"SizeLEFT", "SizeRIGHT", "UrkLEFT", "UrkRIGHT", "HeightLEFT",
				"HeightRIGHT", "TopVolumeLEFT", "TopVolumeRIGHT",
				"AnkleVolumeLEFT", "AnkleVolumeRIGHT", "KvVolumeLEFT",
				"KvVolumeRIGHT", "CustomerSN", "CustomerFN", "CustomerP",
				"EmployeeSN", "EmployeeFN", "EmployeeP", "ModelIMG" };
		int[] to = new int[] { R.id.updateOrderID, R.id.updateModel,
				R.id.updateMaterial, R.id.updateSizeLeft,
				R.id.updateSizeRight, R.id.updateUrkLeft,
				R.id.updateUrkRight, R.id.updateHeightLeft,
				R.id.updateHeightRight, R.id.updateTopVolumeLeft,
				R.id.updateTopVolumeRight, R.id.updateAnkleVolumeLeft,
				R.id.updateAnkleVolumeRight, R.id.updateKvVolumeLeft,
				R.id.updateKvVolumeRight, R.id.updateCustomerSN,
				R.id.updateCustomerFN, R.id.updateCustomerP,
				R.id.updateEmployeeSN, R.id.updateEmployeeFN,
				R.id.updateEmployeeP, R.id.updateModelIMG };
		
		scAdapter = new SimpleCursorAdapter(this, R.layout.edit_order, cursor, from, to);
		lv = (ListView)findViewById(R.id.orders_list);
		// устанавливаем режим выбора пунктов списка 
		lv.setAdapter(scAdapter);
		
		submit_update = (Button)findViewById(R.id.submit_update);
		submit_update.setOnClickListener(this);
		modelIMG = (Button)findViewById(R.id.updateModelIMG);
		modelIMG.setOnClickListener(this);
	}
	// вытаскиваем из базы запись с пришедшим ID и вставляем ее значения в
	// EditText'ы
	/*
	 * @Override public void onClick(View v) { Intent intent = new Intent();
	 * String sn = getText().toString().trim(); String fn =
	 * getText().toString().trim(); String p = getText().toString().trim();
	 * 
	 * if(sn.isEmpty() | fn.isEmpty() | p.isEmpty()){
	 * setResult(RESULT_CANCELED); }else{ intent.putExtra("", sn);
	 * intent.putExtra("", fn); intent.putExtra("", p); setResult(RESULT_OK,
	 * intent); } finish(); }
	 */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit_update:
			
			break;
		case R.id.updateModelIMG:
			
			break;
		default:
			break;
		}
	}
}
