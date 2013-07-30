package com.example.orthopedicdb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
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


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AllOrdersActivityShort extends Activity{
	
	ActionMode actionMode;
	ListView lv;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;
	DB db;
	final String LOG_TAG = "myLogs";
	final int SHOW_PROGRESS = 0;
	final int CLOSE_PROGRESS = 1;
	AlertDialog dialog;
	MyAsyncTask mt;
	String extraType="";
	String extraQuery="";
	public final int ALLORDERS = 1;
	public final int QUICK_SEARCH = 2;
	public final int EXT_SEARCH = 3;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_orders);
		if (savedInstanceState != null){
			extraType = savedInstanceState.getString("extraType");
			extraQuery = savedInstanceState.getString("extraQuery");
		}
		// подключаемся к БД
	    db = new DB(this);
	    db.open();
	    mt = new MyAsyncTask();
	    
	    dialog = DialogScreen.getDialog(this, DialogScreen.PROGRESS);
        
        if( extraType.equals("QUICK_SEARCH_QUERY") || getIntent().hasExtra("QUICK_SEARCH_QUERY") ){
        	extraType = "QUICK_SEARCH_QUERY";
        	this.setTitle("Результаты поиска");
        	extraQuery = getIntent().getStringExtra("QUICK_SEARCH_QUERY");
        	cursor = db.quicklySearch(extraQuery);
        	Toast.makeText(getApplicationContext(), "Найдено записей: "+cursor.getCount(), Toast.LENGTH_LONG).show();
//        	mt.execute(QUICK_SEARCH);
        }else if(getIntent().hasExtra("EXT_SEARCH_WHERE") || extraType.equals("EXT_SEARCH_WHERE") ){
        	extraType = "EXT_SEARCH_WHERE";
        	this.setTitle("Результаты поиска");
        	extraQuery = getIntent().getStringExtra("EXT_SEARCH_WHERE");
        	if(extraQuery.length() == 0){
        		Toast.makeText(getApplicationContext(), "Ничего не найдено. Ваш запрос пуст.", Toast.LENGTH_LONG).show();
        		return;
        	}
        	cursor = db.extendedSearch(extraQuery);
        	Toast.makeText(getApplicationContext(), "Найдено записей: "+cursor.getCount(), Toast.LENGTH_LONG).show();
//        	mt.execute(EXT_SEARCH);
        }else{
//        	mt.execute(ALLORDERS);
        	cursor = db.getAllShortOrders();
        	Toast.makeText(getApplicationContext(), "Всего записей: "+cursor.getCount(), Toast.LENGTH_LONG).show();
        }

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

	@SuppressWarnings("deprecation")
	public void getContent(Cursor c){
		startManagingCursor(c);
	    String[] from = new String[] { "OrderID", "Model", "Material", "Customer", "Employee" };
	    int[] to = new int[] { R.id.detailedOrderID, R.id.Model, R.id.Material, R.id.Customer, R.id.Employee };
	    scAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.short_item, c, from, to);
		lv = (ListView)findViewById(R.id.orders_list); 
		lv.setAdapter(scAdapter);
	    registerForContextMenu(lv);
	    lv.setOnItemClickListener(myOnClick);
	}
	
	public OnItemClickListener myOnClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
			Intent detailedOrderIntent = new Intent(getApplicationContext(), DetailOrderActivity.class);
			detailedOrderIntent.putExtra("ID", arg3);
			startActivity(detailedOrderIntent);
			finish();
		}
	};

	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString("extraType", extraType);
	    outState.putString("extraQuery", extraQuery);
	}
	
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    extraType = savedInstanceState.getString("extraType");
	    extraQuery = savedInstanceState.getString("extraQuery");
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
		Intent main = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(main);
		finish();
	}

	protected void onStart() {
	    super.onStart();
	    db = new DB(this);
        db.open();
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
			finish();
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
	    @SuppressWarnings("deprecation")
		public boolean onOptionsItemSelected(MenuItem item){
	    	Intent intent = new Intent();
	    	switch (item.getItemId()) {
	    	
				case R.id.MENU_NEW_ORDER:
					intent.setClass(getApplicationContext(), NewOrderActivity.class);
					startActivity(intent);
					finish();
					break;
		
				case R.id.MENU_SEARCH:
					intent.setClass(getApplicationContext(), FragmentSearchActivity.class);
					startActivity(intent);
					finish();
					break;
					
				case R.id.MENU_HISTORY:
					cursor.requery();
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
	
	    /************************************************************************/
	    public class MyAsyncTask extends AsyncTask<Integer, Void, Cursor> {
	    	Cursor taskcursor;
   	
	    	@Override
	        protected void onPreExecute() {
	          super.onPreExecute();
	          dialog.show();
	        }
	    	
			@Override
	    	protected Cursor doInBackground(Integer... params) {
	    		switch (params[0]) {
		    		case ALLORDERS:
		    			taskcursor = db.getAllShortOrders();
		    			break;
		    			
		    		case QUICK_SEARCH:
		    			taskcursor = db.quicklySearch(extraQuery);
		    			break;
		    		case EXT_SEARCH:
		    			taskcursor = db.extendedSearch(extraQuery);
		    			break;
					default:
		    			break;
	    		}
	    		
	    		
	    		
	    		return taskcursor;
	    	}

			@Override
	    	protected void onPostExecute(Cursor result) {
	    		super.onPostExecute(cursor);
	    	    dialog.dismiss();
	    	    getContent(result);
//	    		Toast.makeText(getApplicationContext(), "Найдено записей: "+result.getCount(), Toast.LENGTH_LONG).show();
//	    	    scAdapter.changeCursor(result);
//	    	    startManagingCursor(result);
	        }

	    }
	    
}// END ACTIVITY