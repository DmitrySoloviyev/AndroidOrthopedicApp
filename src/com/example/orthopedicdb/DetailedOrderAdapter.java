package com.example.orthopedicdb;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailedOrderAdapter extends SimpleCursorAdapter {

	private int mLayout;
	private Context mContext;
	LoadImageTask imageTask;
	
	@SuppressWarnings("deprecation")
	public DetailedOrderAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
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
		String orderID 			= cursor.getString(cursor.getColumnIndex("OrderID"));
		String model 			= cursor.getString(cursor.getColumnIndex("Model"));
		String material 		= cursor.getString(cursor.getColumnIndex("Material"));
		String sizeLeft 		= cursor.getString(cursor.getColumnIndex("SizeLEFT"));
		String sizeRight 		= cursor.getString(cursor.getColumnIndex("SizeRIGHT"));
		String urkLeft 			= cursor.getString(cursor.getColumnIndex("UrkLEFT"));
		String urkRight 		= cursor.getString(cursor.getColumnIndex("UrkRIGHT"));
		String heightLeft 		= cursor.getString(cursor.getColumnIndex("HeightLEFT"));
		String heightRight 		= cursor.getString(cursor.getColumnIndex("HeightRIGHT"));
		String topVolumeLeft 	= cursor.getString(cursor.getColumnIndex("TopVolumeLEFT"));
		String topVolumeRight 	= cursor.getString(cursor.getColumnIndex("TopVolumeRIGHT"));
		String ankleVolumeLeft 	= cursor.getString(cursor.getColumnIndex("AnkleVolumeLEFT"));
		String ankleVolumeRight = cursor.getString(cursor.getColumnIndex("AnkleVolumeRIGHT"));
		String kvVolumeLeft 	= cursor.getString(cursor.getColumnIndex("KvVolumeLEFT"));
		String kvVolumeRight 	= cursor.getString(cursor.getColumnIndex("KvVolumeRIGHT"));
		String customerSN 		= cursor.getString(cursor.getColumnIndex("CustomerSN"));
		String customerFN 		= cursor.getString(cursor.getColumnIndex("CustomerFN"));
		String customerP 		= cursor.getString(cursor.getColumnIndex("CustomerP"));
		String employeeSN 		= cursor.getString(cursor.getColumnIndex("EmployeeSN"));
		String employeeFN 		= cursor.getString(cursor.getColumnIndex("EmployeeFN"));
		String employeeP 		= cursor.getString(cursor.getColumnIndex("EmployeeP"));
		String modelIMG 		= cursor.getString(cursor.getColumnIndex("ModelIMG"));
		
		// НАХОДИМ ВИДЖЕТЫ
		TextView TVorderID 			= (TextView) view.findViewById(R.id.shortOrderID);
		TextView TVmodel 			= (TextView) view.findViewById(R.id.detailedModel);
		TextView TVmaterial 		= (TextView) view.findViewById(R.id.detailedMaterial);
		TextView TVsizeLeft 		= (TextView) view.findViewById(R.id.detailedSizeLeft);
		TextView TVsizeRight 		= (TextView) view.findViewById(R.id.detailedSizeRight);
		TextView TVurkLeft 			= (TextView) view.findViewById(R.id.detailedUrkLeft);
		TextView TVurkRight 		= (TextView) view.findViewById(R.id.detailedUrkRight);
		TextView TVheightLeft 		= (TextView) view.findViewById(R.id.detailedHeightLeft);
		TextView TVheightRight 		= (TextView) view.findViewById(R.id.detailedHeightRight);
		TextView TVtopVolumeLeft 	= (TextView) view.findViewById(R.id.detailedTopVolumeLeft);
		TextView TVtopVolumeRight 	= (TextView) view.findViewById(R.id.detailedTopVolumeRight);
		TextView TVankleVolumeLeft 	= (TextView) view.findViewById(R.id.detailedAnkleVolumeLeft);
		TextView TVankleVolumeRight = (TextView) view.findViewById(R.id.detailedAnkleVolumeRight);
		TextView TVkvVolumeLeft 	= (TextView) view.findViewById(R.id.detailedKvVolumeLeft);
		TextView TVkvVolumeRight 	= (TextView) view.findViewById(R.id.detailedKvVolumeRight);
		TextView TVcustomerSN 		= (TextView) view.findViewById(R.id.detailedCustomerSN);
		TextView TVcustomerFN 		= (TextView) view.findViewById(R.id.detailedCustomerFN);
		TextView TVcustomerP 		= (TextView) view.findViewById(R.id.detailedCustomerP);
		TextView TVemployeeSN 		= (TextView) view.findViewById(R.id.detailedEmployeeSN);
		TextView TVemployeeFN 		= (TextView) view.findViewById(R.id.detailedEmployeeFN);
		TextView TVemployeeP 		= (TextView) view.findViewById(R.id.detailedEmployeeP);
		ImageView IVmodelIMG 		= (ImageView) view.findViewById(R.id.detailedModelIMG);
		ProgressBar pb 				= (ProgressBar)view.findViewById(R.id.progressBar);

		// СВЯЗЫВАЕМ ДАННЫЕ С ВИДЖЕТАМИ
		TVorderID.setText(orderID);
		TVmodel.setText(model);
		TVmaterial.setText(material);
		TVsizeLeft.setText(sizeLeft);
		TVsizeRight.setText(sizeRight);
		TVurkLeft.setText(urkLeft);
		TVurkRight.setText(urkRight);
		TVheightLeft.setText(heightLeft);
		TVheightRight.setText(heightRight);
		TVtopVolumeLeft.setText(topVolumeLeft);
		TVtopVolumeRight.setText(topVolumeRight);
		TVankleVolumeLeft.setText(ankleVolumeLeft);
		TVankleVolumeRight.setText(ankleVolumeRight);
		TVkvVolumeLeft.setText(kvVolumeLeft);
		TVkvVolumeRight.setText(kvVolumeRight);
		TVcustomerSN.setText(customerSN);
		TVcustomerFN.setText(customerFN);
		TVcustomerP.setText(customerP);
		TVemployeeSN.setText(employeeSN);
		TVemployeeFN.setText(employeeFN);
		TVemployeeP.setText(employeeP);
//		IVmodelIMG.setImageBitmap(decodeBitmapFromFile(modelIMG, 300, 300));
		imageTask = new LoadImageTask(mContext, modelIMG, IVmodelIMG, pb);
		imageTask.execute(300, 300);
	}
}