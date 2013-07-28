package com.example.orthopedicdb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LeftMenuActivity extends Activity implements OnClickListener {

	Button new_order, search, all, gallery;
	View menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.left_menu);

		menu = getLayoutInflater().inflate(R.layout.left_menu, null); 
		new_order = (Button) menu.findViewById(R.id.menu_button_new_order);
		search = (Button) menu.findViewById(R.id.menu_button_search);
		all = (Button) menu.findViewById(R.id.menu_button_all_orders);
		gallery = (Button) menu.findViewById(R.id.menu_button_gallery);

		new_order.setOnClickListener(this);
		search.setOnClickListener(this);
		all.setOnClickListener(this);
		gallery.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.menu_button_new_order:
				intent.setClass(getApplicationContext(), NewOrderActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				break;
	
			case R.id.menu_button_search:
				intent.setClass(getApplicationContext(), SearchActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				break;
			case R.id.menu_button_all_orders:
				intent.setClass(getApplicationContext(), AllOrdersActivityShort.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				break;
			case R.id.menu_button_gallery:
				intent.setClass(getApplicationContext(), GalleryView.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				break;
			default:
				break;
		}
	}
}
