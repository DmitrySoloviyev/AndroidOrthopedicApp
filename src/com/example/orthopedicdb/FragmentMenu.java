package com.example.orthopedicdb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentMenu extends Fragment {

	View view;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {




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
		
		return view;
	}
}
