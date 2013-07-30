package com.example.orthopedicdb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentMenu extends Fragment {
	TextView countOrders;
	EditText quickly_search;
	Button quickly_search_submit;
	DB db;
	Intent intent;
	SharedPreferences sp;
	PackageInfo packageInfo;
	View menu;
	ListView lv;
	static OnLeftMenuItemListener LeftMenuItemListener;
	View view;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_menu, null);
		lv = (ListView)view.findViewById(R.id.left_menu);
		String[] items = {"Новый заказ", "Поиск", "Все заказы", "Галерея"};
		ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(getActivity(), R.layout.menu_simple_list_item, items);
		lv.setAdapter(menuAdapter);
		lv.setOnItemClickListener(myMenuClick);
		
		PackageManager pm = getActivity().getPackageManager();
		try {
			packageInfo = pm.getPackageInfo(getActivity().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = packageInfo.versionName;
		TextView tvversionCode = (TextView)view.findViewById(R.id.versionCode);
		tvversionCode.setText("Version "+version);
		Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.leafanim);
		tvversionCode.startAnimation(anim);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String flag = sp.getString("version", "0");
        
        if (!flag.equals(version)) {
            Intent showNews = new Intent(getActivity(), NewsActivity.class);
            startActivity(showNews);
        }
        
        // подключаемся к БД
        db = new DB(getActivity());
        db.open();

		if(db.countEmployees() == 0){
			intent = new Intent(getActivity(), NewEmployee.class);
			startActivityForResult(intent, 1);
		}
/*		
		quickly_search = (EditText)view.findViewById(R.id.quickly_search);
        quickly_search_submit = (Button)view.findViewById(R.id.quickly_search_submit);
        quickly_search_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String query = quickly_search.getText().toString().trim();
				if(query.length() == 0){
					return;
				}
				Intent quick_search_result = new Intent(getActivity(), AllOrdersActivityShort.class);
				quick_search_result.putExtra("QUICK_SEARCH_QUERY", query);
				startActivity(quick_search_result);
			}
		});
*/		
	    countOrders = (TextView)view.findViewById(R.id.count);
	    countOrders.setText("Заказов в базе: " + db.countOrders());
		
		return view;
	}
	
	public OnItemClickListener myMenuClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
			switch (position) {
				case 0:// НОВЫЙ ЗАКАЗ
					LeftMenuItemListener.callFragmentByPosition(0);
					break;
				case 1: // ПОИСК
					LeftMenuItemListener.callFragmentByPosition(1);
					break;
				case 2:// ВСЕ ЗАПИСИ
					LeftMenuItemListener.callFragmentByPosition(2);
					break;
				case 3:// ГАЛЕРЕЯ
					LeftMenuItemListener.callFragmentByPosition(3);
					break;
				default:
					break;
			}
		}
	};
	
	public interface OnLeftMenuItemListener {
	    public void callFragmentByPosition(int item);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	    	try {
	    		LeftMenuItemListener = (OnLeftMenuItemListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement onLeftMenuItemListener");
	        }
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (data == null) {
	    	Toast.makeText(getActivity(), "Модельер не добавлен! Добавьте модельера", Toast.LENGTH_LONG).show();
	    	return;
	    }

	    if (resultCode == Activity.RESULT_OK) {
	       	String surname 		= data.getStringExtra("surname");
	        String firstname 	= data.getStringExtra("firstname");
	        String patronymic 	= data.getStringExtra("patronymic");
	        db.addNewEmployee(surname, firstname, patronymic);
	        Toast.makeText(getActivity(), "Модельер "+surname +" "+firstname+" "+patronymic+" добавлен!", Toast.LENGTH_SHORT).show();
	    } else {
	    	Toast.makeText(getActivity(), "Модельер не добавлен! Добавьте модельера", Toast.LENGTH_LONG).show();
	    }
	}
}
