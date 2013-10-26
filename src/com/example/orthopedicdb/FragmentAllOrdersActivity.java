package com.example.orthopedicdb;

import java.util.ArrayList;
import java.util.Iterator;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentAllOrdersActivity extends Fragment implements SearchView.OnQueryTextListener{

	AllOrdersAdapter scAdapter;
	DialogFragment progressDialog;
	ListView lv;
	DB db;
	View view;
	SearchView quickSearchView;
	static int loadedItems = 6;
	volatile Cursor allOrdersCursor;
	static int mScrollState;
	static int orderCounts;

	@Override
	public void onStart() {
	    super.onStart();
	    
	    getActivity().setTitle("Все заказы");
	    setHasOptionsMenu(true);
	    
	    db = new DB(getActivity());
        db.open();
        
        orderCounts = db.countOrders();
        getActivity().getActionBar().setSubtitle("Записей в базе: "+orderCounts);
        
        if(orderCounts < 20)
        	loadedItems = orderCounts;
        else
        	loadedItems = 20;
        
        if(MainActivity.quickSearchWhere != null){
	    	loadSearchResultList(MainActivity.quickSearchWhere);
	    }else{
	    	loadAllOrders();
	    }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.all_orders, null);
	    return view;
	}// ON CREATE
	

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		inflater.inflate(R.menu.action_search, menu);
		
		MenuItem quickSearch = menu.findItem(R.id.action_search);
	    quickSearchView = (SearchView) quickSearch.getActionView();
	    quickSearchView.setOnQueryTextListener(this);
	    
	    MenuItem back = menu.findItem(R.id.action_back_to_all_orders);
	    back.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				MainActivity.quickSearchWhere = null;
				loadAllOrders();
				return false;
			}
		});
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onQueryTextChange(String arg0) { 
		return true; 
	}
	
	/** РЕЗУЛЬТАТЫ ПОИСКА */
	@Override
	public boolean onQueryTextSubmit(String where) {
		MainActivity.quickSearchWhere = where;
		loadSearchResultList(MainActivity.quickSearchWhere);
		return true;
	}
	
	
	
	
	// ФУНКЦИЯ ПОИСКА //
	public void loadSearchResultList(final String where){
		new AsyncTask<Void, Void, Cursor>() {

	    	@Override
	        protected void onPreExecute() {
	    		super.onPreExecute();
	    		progressDialog = FragmentDialogScreen.newInstance(FragmentDialogScreen.DIALOG_PROGRESS);
	    		progressDialog.show(getFragmentManager(), "pdlg");
	        }
			@Override
	    	protected Cursor doInBackground(Void... params) {
		    	return db.quicklySearch(where);
	    	}
			@SuppressWarnings("deprecation")
			@Override
	    	protected void onPostExecute(final Cursor searchResult) {
	    		super.onPostExecute(searchResult);
	    		Toast.makeText(getActivity(), "Найдено записей: "+searchResult.getCount(), Toast.LENGTH_SHORT).show();
	    		getActivity().startManagingCursor(searchResult);
	            
	    	    String[] from = new String[] { "OrderID", "Model", "Material", "Customer", "Employee" };
	    	    int[] to = new int[] { R.id.shortOrderID, R.id.shortModel, R.id.shortMaterial, R.id.shortCustomer, R.id.shortEmployee };
	    	    
	    	    AllOrdersAdapter scAdapter = new AllOrdersAdapter(getActivity(), R.layout.short_item, searchResult, from, to);
	    	    
	    	    ListView lv = (ListView)view.findViewById(R.id.orders_list);
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
	    		    		    searchResult.requery();
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
	    	    
	    	    lv.setOnScrollListener(emptyListener);
	    	    
	    	    progressDialog.dismiss();
	        }
	    }.execute();
	}
	
	// ФУНКЦИЯ ВЫВОДА ВСЕХ ЗАКАЗОВ //
	public void loadAllOrders(){
		new AsyncTask<Void, Void, Cursor>() {
	    	
	    	@Override
	        protected void onPreExecute() {
	    		super.onPreExecute();
	    		progressDialog = FragmentDialogScreen.newInstance(FragmentDialogScreen.DIALOG_PROGRESS);
	    		progressDialog.show(getFragmentManager(), "pdlg");
	        }
			@Override
	    	protected Cursor doInBackground(Void... params) {
				return db.getAllShortOrders(loadedItems);
	    	}
			@SuppressWarnings("deprecation")
			@Override
	    	protected void onPostExecute(Cursor searchResult) {
	    		super.onPostExecute(searchResult);
	    		allOrdersCursor = searchResult;
	    		getActivity().startManagingCursor(allOrdersCursor);
	            
	    	    String[] from = new String[] { "OrderID", "Model", "Material", "Customer", "Employee" };
	    	    int[] to = new int[] { R.id.shortOrderID, R.id.shortModel, R.id.shortMaterial, R.id.shortCustomer, R.id.shortEmployee };
	    	    
	    	    scAdapter = new AllOrdersAdapter(getActivity(), R.layout.short_item, allOrdersCursor, from, to);
	    	    
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
	    		    		    allOrdersCursor.requery();
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
	    		
	    		lv.setOnScrollListener(new OnScrollListener() {
	    			@Override
	    			public void onScrollStateChanged(AbsListView view, int scrollState) {
	    				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE)
	    					mScrollState = scrollState;
	    			}
	    			
	    			@Override
	    			public void onScroll(AbsListView v, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	    		        if ( (mScrollState == OnScrollListener.SCROLL_STATE_IDLE) && ((firstVisibleItem + visibleItemCount) >= totalItemCount) ) {
	    		        	final int oldTotal = totalItemCount;
	    		        	if( orderCounts > loadedItems ){
	    		        		// если количество всех заказов больше чем загружено на данный момент, то грузим еще
	    		        		// но сначала узнаем сколько грузить: будем грузить по 20 штук, НО если в базе осталось, скажем, 10 заказов
	    		        		// необходимо загрузить только 10, если осталось 7, значит 7, и т.д
	    		        		int itemsToLoad = orderCounts - loadedItems;
	    		        		if(itemsToLoad < 20)
	    		        			loadedItems = loadedItems + itemsToLoad;
	    		        		else
	    		        			loadedItems+=20;

	    		        		
	    		        		new AsyncTask<Void, Void, Cursor>() {
	    		        			
	    		        			@Override
	    		        			protected void onPreExecute(){
	    		        				view.findViewById(R.id.loadingData).setVisibility(View.VISIBLE);
	    		        			}
	    		        			
	    							@Override
	    							protected Cursor doInBackground(Void... params) {
	    								return db.getAllShortOrders(loadedItems);
	    							}
	    							
	    							@Override
	    					    	protected void onPostExecute(Cursor searchResult) {
	    					    		super.onPostExecute(searchResult);
	    					    		scAdapter.changeCursor(searchResult);
	    					    		scAdapter.notifyDataSetChanged();
	    					    		view.findViewById(R.id.loadingData).setVisibility(View.INVISIBLE);
	    					    		view.invalidate();
	    					    		lv.setSelectionFromTop(oldTotal + 1, 0);
	    					    	}
	    		        		}.execute();
	    		        	}
	    		        }
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
	

	OnScrollListener emptyListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
	};
	
	public void onDestroy() {
	    super.onDestroy();
	    
	}
}// END ACTIVITY