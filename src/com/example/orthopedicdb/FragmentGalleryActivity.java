package com.example.orthopedicdb;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class FragmentGalleryActivity extends Fragment {
	
	DB db;
	Cursor cursor;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gallery, null);
		db = new DB(getActivity());
	    db.open();
	    
	    String[] from = new String[] { "ModelIMG" };
	    int[] to = new int[] {R.id.modelImage};
	    
	    cursor = db.getModelsGallery();
		SimpleCursorAdapter sa = new SimpleCursorAdapter(getActivity(), R.layout.image, cursor, from, to);
		Gallery gv = (Gallery)view.findViewById(R.id.gallery1);
		// устанавливаем режим выбора пунктов списка 
		gv.setAdapter(sa);
		
		return view;
	}
}
