package com.example.orthopedicdb;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AllOrdersAdapter extends SimpleCursorAdapter {

	private int mLayout;

	@SuppressWarnings("deprecation")
	public AllOrdersAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		mLayout = layout;
		mContext = context;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(mLayout, parent, false);
		return view;
	}	
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		// ДОСТАЕМ ДАННЫЕ ИЗ КУРСОРА
		String orderID 	  = cursor.getString(cursor.getColumnIndex("OrderID"));
		String model 	  = cursor.getString(cursor.getColumnIndex("Model"));
		String material   = cursor.getString(cursor.getColumnIndex("Material"));
		String customer   = cursor.getString(cursor.getColumnIndex("Customer"));
		String employee   = cursor.getString(cursor.getColumnIndex("Employee"));

		// НАХОДИМ ВИДЖЕТЫ
		TextView TVorderID = (TextView) view.findViewById(R.id.shortOrderID);
		TextView TVmodel = (TextView) view.findViewById(R.id.shortModel);
		TextView TVmaterial = (TextView) view.findViewById(R.id.shortMaterial);
		TextView TVcustomer = (TextView) view.findViewById(R.id.shortCustomer);
		TextView TVemployee = (TextView) view.findViewById(R.id.shortEmployee);
		
		// СВЯЗЫВАЕМ ДАННЫЕ С ВИДЖЕТАМИ
		TVorderID.setText(orderID);
		TVmodel.setText(model);
		TVmaterial.setText(material);
		TVcustomer.setText(customer);
		TVcustomer.setSelected(true);
		TVemployee.setText(employee);
		TVemployee.setSelected(true);
	}
}