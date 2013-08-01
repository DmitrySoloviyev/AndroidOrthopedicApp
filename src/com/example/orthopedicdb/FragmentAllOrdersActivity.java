package com.example.orthopedicdb;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class FragmentAllOrdersActivity extends Fragment{
	final String LOG_TAG = "myLogs";
	ActionMode actionMode;
	ListView lv;
	SimpleCursorAdapter scAdapter;
	DialogFragment progressDialog;
	Cursor cursor;
	DB db;
	View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.all_orders, null);

		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		//cursor = db.getAllShortOrders();
		//Toast.makeText(getActivity(), "Всего записей: "+cursor.getCount(), Toast.LENGTH_LONG).show();
	    return view;
	}// ON CREATE
	
	public void onStart() {
	    super.onStart();
	    db = new DB(getActivity());
        db.open();
        
	    new AsyncTask<Void, Void, Cursor>() {
	    	Cursor taskcursor;
	    	@Override
	        protected void onPreExecute() {
	    		super.onPreExecute();
	    		progressDialog = FragmentDialogScreen.newInstance(FragmentDialogScreen.DIALOG_PROGRESS);
	    		progressDialog.show(getFragmentManager(), "pdlg");
	        }
			@Override
	    	protected Cursor doInBackground(Void... params) {
		    	taskcursor = db.getAllShortOrders();
	    		return taskcursor;
	    	}
			@SuppressWarnings("deprecation")
			@Override
	    	protected void onPostExecute(Cursor cursor) {
	    		super.onPostExecute(cursor);
	    		FragmentAllOrdersActivity.this.cursor = cursor;
	    		Toast.makeText(getActivity(), "Записей в базе: "+cursor.getCount(), Toast.LENGTH_LONG).show();
	    		getActivity().getActionBar().setSubtitle("Записей в базе: "+db.countOrders());
	    		getActivity().startManagingCursor(cursor);
	            
	    	    String[] from = new String[] { "OrderID", "Model", "Material", "Customer", "Employee" };
	    	    int[] to = new int[] { R.id.detailedOrderID, R.id.Model, R.id.Material, R.id.Customer, R.id.Employee };
	    	    
	    	    scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.short_item, cursor, from, to);
	    	    
	    		lv = (ListView)view.findViewById(R.id.orders_list);
	    		lv.setAdapter(scAdapter);

	    	    lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						Intent detailedOrderIntent = new Intent(getActivity(), DetailOrderActivity.class);
						detailedOrderIntent.putExtra("ID", arg3);
						startActivity(detailedOrderIntent);
					}
				});
	    	    
	    	    lv.setLongClickable(true);
	    	    lv.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

						return true;
					}
				});
	    	    progressDialog.dismiss();
	        }
	    }.execute();
	}
	
/*
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
			Intent editOrderIntent = new Intent(getActivity(), EditOrderActivity.class);
				editOrderIntent.putExtra("ID", acmi_edit.id);
			startActivity(editOrderIntent);
			return true;
			//break;
		case 2:			// удаление
			// получаем из пункта контекстного меню данные по пункту списка 
		    AdapterContextMenuInfo acmi_delete = (AdapterContextMenuInfo) item.getMenuInfo();
		    // извлекаем id записи и удаляем соответствующую запись в БД
		    db.deleteOrderById(acmi_delete.id);
		    Toast.makeText(getActivity(), "Запись успешно удалена!", Toast.LENGTH_LONG).show();
		    // обновляем курсор
		    cursor.requery();
		    return true;
			//break;
		default:
			break;
		}
	    return super.onContextItemSelected(item);
	  }*/
}// END ACTIVITY