package com.example.orthopedicdb;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentExtenedSearchActivity extends Fragment {
	
	DB db;
	View view;
	ListView lv;
	Cursor cursor;
	String WHERE;
	DialogFragment progressDialog;
	SimpleCursorAdapter scAdapter;
	
	public static FragmentExtenedSearchActivity newInstance(String where) {
		FragmentExtenedSearchActivity ext_search = new FragmentExtenedSearchActivity();
	    Bundle arguments = new Bundle();
	    arguments.putString("EXT_SEARCH_WHERE", where);
	    ext_search.setArguments(arguments);
	    return ext_search;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.all_orders, null);
		WHERE = getArguments().getString("EXT_SEARCH_WHERE");
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	    
	    return view;
	}// ON CREATE

	public OnItemClickListener myOnClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
			Intent detailedOrderIntent = new Intent(getActivity(), DetailOrderActivity.class);
			detailedOrderIntent.putExtra("ID", arg3);
			startActivity(detailedOrderIntent);
		}
	};
	
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
		    	taskcursor = db.extendedSearch(WHERE);
	    		return taskcursor;
	    	}
			@SuppressWarnings("deprecation")
			@Override
	    	protected void onPostExecute(Cursor cursor) {
	    		super.onPostExecute(cursor);
	    		FragmentExtenedSearchActivity.this.cursor = cursor;
	    		Toast.makeText(getActivity(), "Найдено записей: "+cursor.getCount(), Toast.LENGTH_LONG).show();
	    		getActivity().startManagingCursor(cursor);
	            
	    	    String[] from = new String[] { "OrderID", "Model", "Material", "Customer", "Employee" };
	    	    int[] to = new int[] { R.id.detailedOrderID, R.id.Model, R.id.Material, R.id.Customer, R.id.Employee };
	    	    
	    	    scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.short_item, cursor, from, to);
	    	    
	    		lv = (ListView)view.findViewById(R.id.orders_list);
	    		lv.setAdapter(scAdapter);

	    	    registerForContextMenu(lv);
	    	    lv.setOnItemClickListener(myOnClick);
	    	    progressDialog.dismiss();
	        }
	    }.execute();
	}
}
