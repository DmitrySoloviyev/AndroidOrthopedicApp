package com.example.orthopedicdb;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class AllOrdersActivityShort extends Activity{
	
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
		
		// получаем курсор
		cursor = db.getAllShortOrders();
		startManagingCursor(cursor);

		// формируем столбцы сопоставления
	    String[] from = new String[] { "OrderID", "Model", "Material", "Customer", "Employee" };
	    int[] to = new int[] { R.id.detailedOrderID, R.id.Model, R.id.Material, R.id.Customer, R.id.Employee };
	    
	    // создааем адаптер и настраиваем список
	    scAdapter = new SimpleCursorAdapter(this, R.layout.short_item, cursor, from, to);
	    
		lv = (ListView)findViewById(R.id.orders_list);
		// устанавливаем режим выбора пунктов списка 
		lv.setAdapter(scAdapter);
	    // добавляем контекстное меню к списку
	    registerForContextMenu(lv);
	    lv.setOnItemClickListener(myOnClick);
	}// ON CREATE
	
	public OnItemClickListener myOnClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
			Intent detailedOrderIntent = new Intent(getApplicationContext(), DetailOrderActivity.class);
			detailedOrderIntent.putExtra("ID", arg3);
			startActivity(detailedOrderIntent);
			finish();
		}
	};
	
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	}
	
	protected void onStop() {
	    super.onStop();
	    db.close();
	}

	public void onBackPressed() {
		Intent main = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(main);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		    super.onCreateContextMenu(menu, v, menuInfo);
		    menu.add(0, 1, 0, "Редактировать");
		    menu.add(0, 2, 0, "Удалить");
		  }

	@SuppressWarnings("deprecation")
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:			// редактирование
			AdapterContextMenuInfo acmi_edit = (AdapterContextMenuInfo) item.getMenuInfo();
			Intent editOrderIntent = new Intent(getApplicationContext(), EditOrderActivity.class);
				editOrderIntent.putExtra("ID", acmi_edit.id);
			startActivity(editOrderIntent);
			// обновляем курсор
		    //cursor.requery();
			return true;
			//break;
		case 2:			// удаление
			// получаем из пункта контекстного меню данные по пункту списка 
		    AdapterContextMenuInfo acmi_delete = (AdapterContextMenuInfo) item.getMenuInfo();
		    // извлекаем id записи и удаляем соответствующую запись в БД
		    db.deleteOrderById(acmi_delete.id);
		    Toast.makeText(getApplicationContext(), "Запись успешно удалена!", Toast.LENGTH_LONG).show();
		    // обновляем курсор
		    cursor.requery();
		    return true;
			//break;
		default:
			break;
		}
	    return super.onContextItemSelected(item);
	  }

	// СОЗДАЕМ МЕНЮ
		public boolean onCreateOptionsMenu(Menu menu){
			MenuInflater menuInflater = getMenuInflater();
			menuInflater.inflate(R.menu.main, menu);
			return true;
		}
		
		// ОТКЛИК НА МЕНЮ
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
	
}// END ACTIVITY