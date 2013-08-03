package com.example.orthopedicdb;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentExtenedRESULTSearchActivity extends Fragment {
	
	DB db;
	View view;
	ListView lv;
	Cursor cursor;
	String WHERE;
	DialogFragment progressDialog;
	AllOrdersAdapter scAdapter;
	
	public static FragmentExtenedRESULTSearchActivity newInstance(String where) {
		FragmentExtenedRESULTSearchActivity ext_search = new FragmentExtenedRESULTSearchActivity();
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
	    	protected void onPostExecute(final Cursor cursor) {
	    		super.onPostExecute(cursor);
	    		FragmentExtenedRESULTSearchActivity.this.cursor = cursor;
	    		Toast.makeText(getActivity(), "Найдено записей: "+cursor.getCount(), Toast.LENGTH_SHORT).show();
	    		getActivity().startManagingCursor(cursor);
	            
	    	    String[] from = new String[] { "OrderID", "Model", "Material", "Customer", "Employee" };
	    	    int[] to = new int[] { R.id.shortOrderID, R.id.shortModel, R.id.shortMaterial, R.id.shortCustomer, R.id.shortEmployee };
	    	    
	    	    scAdapter = new AllOrdersAdapter(getActivity(), R.layout.short_item, cursor, from, to);
	    	    
	    		lv = (ListView)view.findViewById(R.id.orders_list);
	    		lv.setAdapter(scAdapter);

	    		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
	    		lv.setMultiChoiceModeListener(new MultiChoiceModeListener() {
	    			private ArrayList<String> checkedIds = new ArrayList<String>();

	    		      public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	    		    	  mode.getMenuInflater().inflate(R.menu.context, menu);
	    		    	  return true;
	    		      }

	    		      public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	    		    	  if (checkedIds.size() == 0) {
	    		    		  mode.finish();
	    		    	  } else if (checkedIds.size() == 1 && mode != null) {
	    		    		  menu.clear();
	    		    		  mode.setTitle(""+ checkedIds.size());
	    		    		  mode.getMenuInflater().inflate(R.menu.context, menu);
	    		    		  return true;
	    		    	  } else if (checkedIds.size() > 1) {
	    		    		  menu.clear();
	    		    		  mode.setTitle(""+ checkedIds.size());
	    		    		  mode.getMenuInflater().inflate(R.menu.context_delete, menu);
	    		    		  return true;
	    		    	  }
	    		    	  return true;
	    		      }

	    		      public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    		    	  switch (item.getItemId()) {
	    		    		case R.id.edit:
	    		    			Intent editOrderIntent = new Intent(getActivity(), EditOrderActivity.class);
	    						editOrderIntent.putExtra("ID", Long.valueOf(checkedIds.get(0)));
	    						startActivity(editOrderIntent);
	    		    			break;
	    		    		case R.id.delete:
	    		    			String[] simpleArray = new String[ checkedIds.size() ];
	    		    			checkedIds.toArray(simpleArray);
	    		    			db.deleteOrderById(simpleArray);
	    		    		    Toast.makeText(getActivity(), "Удалено!", Toast.LENGTH_SHORT).show();
	    		    		    cursor.requery();
	    		    		    getActivity().getActionBar().setSubtitle("Записей в базе: "+db.countOrders());
	    		    			break;
	    		    	  }
	    		    	  checkedIds.clear();
	    		    	  mode.finish();
	    		    	  return false;
	    		      }

	    		      public void onDestroyActionMode(ActionMode mode) {
	    		    	  checkedIds.clear();
	    		      }

	    		      public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
	    		        if(checked){
	                        checkedIds.add(String.valueOf(id));
	                    } else{
	                        Iterator<String> iter = checkedIds.iterator();
	                        while(iter.hasNext()){
	                            String stored = iter.next();
	                            if(stored.contains(String.valueOf(id)))
	                                iter.remove();
	                        }
	                    }
	    		        mode.invalidate();
	    		      }
	    		});
	    		
	    	    lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
						Intent detailedOrderIntent = new Intent(getActivity(), DetailOrderActivity.class);
						detailedOrderIntent.putExtra("ID", id);
						startActivity(detailedOrderIntent);
					}
				});
	    	    progressDialog.dismiss();
	        }
	    }.execute();
	}
}
