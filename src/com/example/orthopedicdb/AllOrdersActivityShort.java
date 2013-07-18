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
	String quick_search_query;
	String ext_search_query;
	final int SHOW_PROGRESS = 0;
	final int CLOSE_PROGRESS = 1;
	AlertDialog dialog;
	MyAsyncTask mt;

	public final int ALLORDERS = 1;
	public final int QUICK_SEARCH = 2;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_orders);

		// подключаемся к БД
	    db = new DB(this);
	    db.open();
	    mt = new MyAsyncTask();
	    
	    dialog = DialogScreen.getDialog(this, DialogScreen.PROGRESS);
        
        if( getIntent().hasExtra("QUICK_SEARCH_QUERY") ){
        	this.setTitle("Результаты поиска");
        	quick_search_query = getIntent().getStringExtra("QUICK_SEARCH_QUERY");
//        	mt.execute(QUICK_SEARCH);
           	cursor = db.quicklySearch(quick_search_query);
        	Toast.makeText(getApplicationContext(), "Найдено записей: "+cursor.getCount(), Toast.LENGTH_LONG).show();
        }else if(getIntent().hasExtra("EXT_SEARCH_WHERE")){
        	this.setTitle("Результаты поиска");
        	ext_search_query = getIntent().getStringExtra("EXT_SEARCH_WHERE");
        	if(ext_search_query.length() == 0){
        		Toast.makeText(getApplicationContext(), "Ничего не найдено. Ваш запрос пуст.", Toast.LENGTH_LONG).show();
        		return;
        	}
        	cursor = db.extendedSearch(ext_search_query);
        	Toast.makeText(getApplicationContext(), "Найдено записей: "+cursor.getCount(), Toast.LENGTH_LONG).show();
        }else{
//        	mt.execute(ALLORDERS);
			cursor = db.getAllShortOrders();
			Toast.makeText(getApplicationContext(), "Всего записей: "+cursor.getCount(), Toast.LENGTH_LONG).show();
//			Log.d(LOG_TAG, "ALL ORDERS = " + cursor);
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
/*	    lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
	    lv.setMultiChoiceModeListener(new MultiChoiceModeListener() {

	        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	          mode.getMenuInflater().inflate(R.menu.context, menu);
	          return true;
	        }

	        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	          return false;
	        }

	        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        	
	        	SparseBooleanArray sbArray = lv.getCheckedItemPositions();
		          for (int i = 0; i < sbArray.size(); i++) {
		            int key = sbArray.keyAt(i);
		            if (sbArray.get(key))
		            	switch (item.getItemId()) {
				    		case R.id.edit:
				    			AdapterContextMenuInfo acmi_edit = (AdapterContextMenuInfo) item.getMenuInfo();
				    			Intent editOrderIntent = new Intent(getApplicationContext(), EditOrderActivity.class);
				    				editOrderIntent.putExtra("ID", acmi_edit.id);
				    			startActivity(editOrderIntent);
				    			finish();
				    			return true;
				    			//break;
				    		case R.id.delete:
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
		          }
	          return false;
	        }

	        public void onDestroyActionMode(ActionMode mode) {
	        }

	        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
	          Log.d(LOG_TAG, "position = " + position + ", checked = " + checked);
	        }
	      });*/
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
					cursor.requery();
					break;
					
				case R.id.MENU_GALLERY:
		
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
		    			taskcursor = db.quicklySearch(quick_search_query);
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
	    		Toast.makeText(getApplicationContext(), "Найдено записей: "+result.getCount(), Toast.LENGTH_LONG).show();
	    		cursor = result;
	        }

	    }
	    
}// END ACTIVITY