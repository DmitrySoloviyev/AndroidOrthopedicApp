package com.example.orthopedicdb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SearchActivity extends Activity implements OnClickListener {

	protected static final String LOG_TAG = "myLogs";
	Button submit_search;
	Button search_employee;
	Button search_material;
	String WHERE="";
	DB db;
	
	Cursor materialsCursor;
	Cursor employeeCursor;
	SimpleCursorAdapter scAdapter;

	EditText edit_order_number;
	EditText edit_model;
	EditText edit_size;
	EditText edit_urk;
	EditText edit_height;
	EditText edit_top_volume;
	EditText edit_ankle_volume;
	EditText edit_kv_volume;
	EditText edit_customer;

	final int DIALOG_MATERIALS = 1;
	final int DIALOG_EMPLOYEES = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);

		submit_search   = (Button) findViewById(R.id.submit_search);
		search_employee = (Button)findViewById(R.id.search_employee);
		search_material = (Button)findViewById(R.id.versionCode);
		submit_search.setOnClickListener(this);
		search_employee.setOnClickListener(this);
		search_material.setOnClickListener(this);
		
		// подключаемся к БД
		db = new DB(this);
		db.open();

		edit_order_number = (EditText) findViewById(R.id.search_order_number);
		edit_model = (EditText) findViewById(R.id.search_model);
		edit_size = (EditText) findViewById(R.id.search_size);
		edit_urk = (EditText) findViewById(R.id.search_urk);
		edit_height = (EditText) findViewById(R.id.search_height);
		edit_top_volume = (EditText) findViewById(R.id.search_TopVolume);
		edit_ankle_volume = (EditText) findViewById(R.id.search_AnkleVolume);
		edit_kv_volume = (EditText) findViewById(R.id.search_KvVolume);
		edit_customer = (EditText) findViewById(R.id.search_customer);
		
		employeeCursor   = db.getEmployeeCursor();
		materialsCursor  = db.getMaterialCursor();
		
	} // end OnCreate

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		switch (id) {
			case DIALOG_MATERIALS:
				adb.setTitle("Выберите материал");
				adb.setMultiChoiceItems(materialsCursor, "MaterialChecked", "MaterialValue",
						new DialogInterface.OnMultiChoiceClickListener() {
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							    db.changeMaterialFlag(which, isChecked);
							    materialsCursor.requery();
							}
						});
				break;
	
			case DIALOG_EMPLOYEES:
				adb.setTitle("Выберите модельера");
				adb.setMultiChoiceItems(employeeCursor, "EmployeeChecked", "Employee" ,
						new DialogInterface.OnMultiChoiceClickListener() {
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								db.changeEmployeeFlag(which, isChecked);
							    employeeCursor.requery();
							}
						});
				
				break;
		}
		
		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return adb.create();
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// меню
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// обработка нажатия пункта меню
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.MENU_NEW_ORDER:
			Intent newOrderIntent = new Intent();
			newOrderIntent.setClass(getApplicationContext(), NewOrderActivity.class);
			startActivity(newOrderIntent);
			break;

		case R.id.MENU_SEARCH:
			break;

		case R.id.MENU_HISTORY:
			Intent allOrdersIntent = new Intent();
			allOrdersIntent.setClass(getApplicationContext(), AllOrdersActivityShort.class);
			startActivity(allOrdersIntent);
			break;

		case R.id.MENU_GALLERY:

			break;

		case R.id.MENU_EXIT:
			finish();
			break;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.submit_search:	
				
				String order_number = edit_order_number.getText().toString().trim();
				String model = edit_model.getText().toString().trim();
				String size = edit_size.getText().toString().trim();
				String urk = edit_urk.getText().toString().trim();
				String height = edit_height.getText().toString().trim();
				String top_volume = edit_top_volume.getText().toString().trim();
				String ankle_volume = edit_ankle_volume.getText().toString().trim();
				String kv_volume = edit_kv_volume.getText().toString().trim();
				
				if(order_number.length() != 0){
					WHERE += " OrderID='"+order_number+"' ";
				}
				
				/***************************************************************
				 * МОДЕЛЬ
				 * */	
				
				if(model.length() != 0){
					if(WHERE.equals("")){
						WHERE += " mod.ModelID='"+model+"' ";
					}else{
						WHERE += " OR mod.ModelID='"+model+"' ";
					}
				}
					

				
		/***************************************************************
		 * РАЗМЕРЫ
		 * */if(size.length() != 0){
				if(WHERE.equals("")){
				//WHERE  ПУСТОЙ
					//разбиваем по пробелу полученные данные
					String[] arr_size = size.split(" ");
					//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
					int iteration=0;
					for (String size_field : arr_size) {
						iteration++;
						if(iteration == arr_size.length){
							//значит элемент массива последний
							//если есть дефис, значит это интервал
							if(size_field.indexOf("-") != -1){
								// разбираем интервал
								String[] interval = size_field.split("-");
								WHERE += " (o.SizeLEFT>='"+interval[0]+"' AND o.SizeLEFT<='"+interval[1]+"') OR " +
										 " (o.SizeRIGHT>='"+interval[0]+"' AND o.SizeRIGHT<='"+interval[1]+"') ";
							}else{
								// интервалов нет
									WHERE += " o.SizeLEFT='"+size_field+"' AND o.SizeRIGHT='"+size_field+"' ";	
							}
						}else{
							// элемент массива не последний
							if(size_field.indexOf("-") != -1){
								// разбираем интервал
								String[] interval = size_field.split("-");
								WHERE += " (o.SizeLEFT>='"+interval[0]+"' AND o.SizeLEFT<='"+interval[1]+"') OR " +
										 " (o.SizeRIGHT>='"+interval[0]+"' AND o.SizeRIGHT<='"+interval[1]+"') AND ";
							}else{
								// интервалов нет
								WHERE += " o.SizeLEFT='"+size_field+"' AND o.SizeRIGHT='"+size_field+"' AND ";
							}	
						}
					}
				}else{
				//WHERE НЕ ПУСТОЙ
					//разбиваем по пробелу полученные данные
					String[] arr_size = size.split(" ");
					//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
					int iteration=0;
					for (String size_field : arr_size) {
						iteration++;
						if(iteration == arr_size.length){
							//значит элемент массива последний
							//если есть дефис, значит это интервал
							if(size_field.indexOf("-") != -1){
								// разбираем интервал
								String[] interval = size_field.split("-");
								WHERE += " OR (o.SizeLEFT>='"+interval[0]+"' AND o.SizeLEFT<='"+interval[1]+"') OR " +
										 " (o.SizeRIGHT>='"+interval[0]+"' AND o.SizeRIGHT<='"+interval[1]+"') ";
							}else{
								// интервалов нет
									WHERE += " OR o.SizeLEFT='"+size_field+"' AND o.SizeRIGHT='"+size_field+"' ";	
							}
						}else{
							// элемент массива не последний
							if(size_field.indexOf("-") != -1){
								// разбираем интервал
								String[] interval = size_field.split("-");
								WHERE += " OR (o.SizeLEFT>='"+interval[0]+"' AND o.SizeLEFT<='"+interval[1]+"') OR " +
										 " (o.SizeRIGHT>='"+interval[0]+"' AND o.SizeRIGHT<='"+interval[1]+"') AND ";
							}else{
								// интервалов нет
								WHERE += " OR o.SizeLEFT='"+size_field+"' AND o.SizeRIGHT='"+size_field+"' AND ";
							}
						}
					}
				}
		 }
				
				/***************************************************************
				 * УРК
				 * */if(urk.length() != 0){
						if(WHERE.equals("")){
						//WHERE  ПУСТОЙ
							//разбиваем по пробелу полученные данные
							String[] arr_urk = urk.split(" ");
							//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
							int iteration=0;
							for (String urk_field : arr_urk) {
								iteration++;
								if(iteration == arr_urk.length){
									//значит элемент массива последний
									//если есть дефис, значит это интервал
									if(urk_field.indexOf("-") != -1){
										// разбираем интервал
										String[] interval = urk_field.split("-");
										WHERE += " (o.UrkLEFT>='"+interval[0]+"' AND o.UrkLEFT<='"+interval[1]+"') OR " +
												 " (o.UrkRIGHT>='"+interval[0]+"' AND o.UrkRIGHT<='"+interval[1]+"') ";
									}else{
										// интервалов нет
											WHERE += " o.UrkLEFT='"+urk_field+"' AND o.UrkRIGHT='"+urk_field+"' ";	
									}
								}else{
									// элемент массива не последний
									if(urk_field.indexOf("-") != -1){
										// разбираем интервал
										String[] interval = urk_field.split("-");
										WHERE += " (o.UrkLEFT>='"+interval[0]+"' AND o.UrkLEFT<='"+interval[1]+"') OR " +
												 " (o.UrkRIGHT>='"+interval[0]+"' AND o.UrkRIGHT<='"+interval[1]+"') AND ";
									}else{
										// интервалов нет
										WHERE += " o.UrkLEFT='"+urk_field+"' AND o.UrkRIGHT='"+urk_field+"' AND ";
									}	
								}
							}
						}else{
						//WHERE НЕ ПУСТОЙ
							//разбиваем по пробелу полученные данные
							String[] arr_urk = urk.split(" ");
							//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
							int iteration=0;
							for (String urk_field : arr_urk) {
								iteration++;
								if(iteration == arr_urk.length){
									//значит элемент массива последний
									//если есть дефис, значит это интервал
									if(urk_field.indexOf("-") != -1){
										// разбираем интервал
										String[] interval = urk_field.split("-");
										WHERE += " OR (o.UrkLEFT>='"+interval[0]+"' AND o.UrkLEFT<='"+interval[1]+"') OR " +
												 " (o.UrkRIGHT>='"+interval[0]+"' AND o.UrkRIGHT<='"+interval[1]+"') ";
									}else{
										// интервалов нет
											WHERE += " OR o.UrkLEFT='"+urk_field+"' AND o.UrkRIGHT='"+urk_field+"' ";	
									}
								}else{
									// элемент массива не последний
									if(urk_field.indexOf("-") != -1){
										// разбираем интервал
										String[] interval = urk_field.split("-");
										WHERE += " OR (o.UrkLEFT>='"+interval[0]+"' AND o.UrkLEFT<='"+interval[1]+"') OR " +
												 " (o.UrkRIGHT>='"+interval[0]+"' AND o.UrkRIGHT<='"+interval[1]+"') AND ";
									}else{
										// интервалов нет
										WHERE += " OR o.UrkLEFT='"+urk_field+"' AND o.UrkRIGHT='"+urk_field+"' AND ";
									}
								}
							}
						}	
				 }
				
						/***************************************************************
						 * ВЫСОТА
						 * */if(height.length() != 0){
								if(WHERE.equals("")){
								//WHERE  ПУСТОЙ
									//разбиваем по пробелу полученные данные
									String[] arr_height = height.split(" ");
									//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
									int iteration=0;
									for (String height_field : arr_height) {
										iteration++;
										if(iteration == arr_height.length){
											//значит элемент массива последний
											//если есть дефис, значит это интервал
											if(height_field.indexOf("-") != -1){
												// разбираем интервал
												String[] interval = height_field.split("-");
												WHERE += " (o.HeightLEFT>='"+interval[0]+"' AND o.HeightLEFT<='"+interval[1]+"') OR " +
														 " (o.HeightRIGHT>='"+interval[0]+"' AND o.HeightRIGHT<='"+interval[1]+"') ";
											}else{
												// интервалов нет
													WHERE += " o.HeightLEFT='"+height_field+"' AND o.HeightRIGHT='"+height_field+"' ";	
											}
										}else{
											// элемент массива не последний
											if(height_field.indexOf("-") != -1){
												// разбираем интервал
												String[] interval = height_field.split("-");
												WHERE += " (o.HeightLEFT>='"+interval[0]+"' AND o.HeightLEFT<='"+interval[1]+"') OR " +
														 " (o.HeightRIGHT>='"+interval[0]+"' AND o.HeightRIGHT<='"+interval[1]+"') AND ";
											}else{
												// интервалов нет
												WHERE += " o.HeightLEFT='"+height_field+"' AND o.HeightRIGHT='"+height_field+"' AND ";
											}	
										}
									}
								}else{
								//WHERE НЕ ПУСТОЙ
									//разбиваем по пробелу полученные данные
									String[] arr_height = height.split(" ");
									//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
									int iteration=0;
									for (String height_field : arr_height) {
										iteration++;
										if(iteration == arr_height.length){
											//значит элемент массива последний
											//если есть дефис, значит это интервал
											if(height_field.indexOf("-") != -1){
												// разбираем интервал
												String[] interval = height_field.split("-");
												WHERE += " OR (o.HeightLEFT>='"+interval[0]+"' AND o.HeightLEFT<='"+interval[1]+"') OR " +
														 " (o.HeightRIGHT>='"+interval[0]+"' AND o.HeightRIGHT<='"+interval[1]+"') ";
											}else{
												// интервалов нет
													WHERE += " OR o.HeightLEFT='"+height_field+"' AND o.HeightRIGHT='"+height_field+"' ";	
											}
										}else{
											// элемент массива не последний
											if(height_field.indexOf("-") != -1){
												// разбираем интервал
												String[] interval = height_field.split("-");
												WHERE += " OR (o.HeightLEFT>='"+interval[0]+"' AND o.HeightLEFT<='"+interval[1]+"') OR " +
														 " (HeightRIGHT>='"+interval[0]+"' AND o.HeightRIGHT<='"+interval[1]+"') AND ";
											}else{
												// интервалов нет
												WHERE += " OR o.HeightLEFT='"+height_field+"' AND o.HeightRIGHT='"+height_field+"' AND ";
											}
										}
									}
								}
						 }
						 
						 
								/***************************************************************
								 * ОБЪЕМ ВЕРХА
								 * */if(top_volume.length() != 0){
										if(WHERE.equals("")){
										//WHERE  ПУСТОЙ
											//разбиваем по пробелу полученные данные
											String[] arr_top_volume = top_volume.split(" ");
											//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
											int iteration=0;
											for (String top_volume_field : arr_top_volume) {
												iteration++;
												if(iteration == arr_top_volume.length){
													//значит элемент массива последний
													//если есть дефис, значит это интервал
													if(top_volume_field.indexOf("-") != -1){
														// разбираем интервал
														String[] interval = top_volume_field.split("-");
														WHERE += " (o.TopVolumeLEFT>='"+interval[0]+"' AND o.TopVolumeLEFT<='"+interval[1]+"') OR " +
																 " (o.TopVolumeRIGHT>='"+interval[0]+"' AND o.TopVolumeRIGHT<='"+interval[1]+"') ";
													}else{
														// интервалов нет
															WHERE += " o.TopVolumeLEFT='"+top_volume_field+"' AND o.TopVolumeRIGHT='"+top_volume_field+"' ";	
													}
												}else{
													// элемент массива не последний
													if(top_volume_field.indexOf("-") != -1){
														// разбираем интервал
														String[] interval = top_volume_field.split("-");
														WHERE += " (o.TopVolumeLEFT>='"+interval[0]+"' AND o.TopVolumeLEFT<='"+interval[1]+"') OR " +
																 " (o.TopVolumeRIGHT>='"+interval[0]+"' AND o.TopVolumeRIGHT<='"+interval[1]+"') AND ";
													}else{
														// интервалов нет
														WHERE += " o.TopVolumeLEFT='"+top_volume_field+"' AND o.TopVolumeRIGHT='"+top_volume_field+"' AND ";
													}	
												}
											}
										}else{
										//WHERE НЕ ПУСТОЙ
											//разбиваем по пробелу полученные данные
											String[] arr_top_volume = top_volume.split(" ");
											//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
											int iteration=0;
											for (String top_volume_field : arr_top_volume) {
												iteration++;
												if(iteration == arr_top_volume.length){
													//значит элемент массива последний
													//если есть дефис, значит это интервал
													if(top_volume_field.indexOf("-") != -1){
														// разбираем интервал
														String[] interval = top_volume_field.split("-");
														WHERE += " OR (o.TopVolumeLEFT>='"+interval[0]+"' AND o.TopVolumeLEFT<='"+interval[1]+"') OR " +
																 " (o.TopVolumeRIGHT>='"+interval[0]+"' AND o.TopVolumeRIGHT<='"+interval[1]+"') ";
													}else{
														// интервалов нет
															WHERE += " OR o.TopVolumeLEFT='"+top_volume_field+"' AND o.TopVolumeRIGHT='"+top_volume_field+"' ";	
													}
												}else{
													// элемент массива не последний
													if(top_volume_field.indexOf("-") != -1){
														// разбираем интервал
														String[] interval = top_volume_field.split("-");
														WHERE += " OR (o.TopVolumeLEFT>='"+interval[0]+"' AND o.TopVolumeLEFT<='"+interval[1]+"') OR " +
																 " (o.TopVolumeRIGHT>='"+interval[0]+"' AND o.TopVolumeRIGHT<='"+interval[1]+"') AND ";
													}else{
														// интервалов нет
														WHERE += " OR o.TopVolumeLEFT='"+top_volume_field+"' AND o.TopVolumeRIGHT='"+top_volume_field+"' AND ";
													}
												}
											}
										}
								 }
										
										
										/***************************************************************
										 * ОБЪЕМ ЛОДЫЖКИ
										 * */if(ankle_volume.length() != 0){
												if(WHERE.equals("")){
												//WHERE  ПУСТОЙ
													//разбиваем по пробелу полученные данные
													String[] arr_ankle_volume = ankle_volume.split(" ");
													//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
													int iteration=0;
													for (String ankle_volume_field : arr_ankle_volume) {
														iteration++;
														if(iteration == arr_ankle_volume.length){
															//значит элемент массива последний
															//если есть дефис, значит это интервал
															if(ankle_volume_field.indexOf("-") != -1){
																// разбираем интервал
																String[] interval = ankle_volume_field.split("-");
																WHERE += " (AnkleVolumeLEFT>='"+interval[0]+"' AND AnkleVolumeLEFT<='"+interval[1]+"') OR " +
																		 " (AnkleVolumeRIGHT>='"+interval[0]+"' AND AnkleVolumeRIGHT<='"+interval[1]+"') ";
															}else{
																// интервалов нет
																	WHERE += " AnkleVolumeLEFT='"+ankle_volume_field+"' AND AnkleVolumeRIGHT='"+ankle_volume_field+"' ";	
															}
														}else{
															// элемент массива не последний
															if(ankle_volume_field.indexOf("-") != -1){
																// разбираем интервал
																String[] interval = ankle_volume_field.split("-");
																WHERE += " (AnkleVolumeLEFT>='"+interval[0]+"' AND AnkleVolumeLEFT<='"+interval[1]+"') OR " +
																		 " (AnkleVolumeRIGHT>='"+interval[0]+"' AND AnkleVolumeRIGHT<='"+interval[1]+"') AND ";
															}else{
																// интервалов нет
																WHERE += " AnkleVolumeLEFT='"+ankle_volume_field+"' AND AnkleVolumeRIGHT='"+ankle_volume_field+"' AND ";
															}	
														}
													}
												}else{
												//WHERE НЕ ПУСТОЙ
													//разбиваем по пробелу полученные данные
													String[] arr_ankle_volume = ankle_volume.split(" ");
													//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
													int iteration=0;
													for (String ankle_volume_field : arr_ankle_volume) {
														iteration++;
														if(iteration == arr_ankle_volume.length){
															//значит элемент массива последний
															//если есть дефис, значит это интервал
															if(ankle_volume_field.indexOf("-") != -1){
																// разбираем интервал
																String[] interval = ankle_volume_field.split("-");
																WHERE += " OR (AnkleVolumeLEFT>='"+interval[0]+"' AND AnkleVolumeLEFT<='"+interval[1]+"') OR " +
																		 " (AnkleVolumeRIGHT>='"+interval[0]+"' AND AnkleVolumeRIGHT<='"+interval[1]+"') ";
															}else{
																// интервалов нет
																	WHERE += " OR AnkleVolumeLEFT='"+ankle_volume_field+"' AND AnkleVolumeRIGHT='"+ankle_volume_field+"' ";	
															}
														}else{
															// элемент массива не последний
															if(ankle_volume_field.indexOf("-") != -1){
																// разбираем интервал
																String[] interval = ankle_volume_field.split("-");
																WHERE += " OR (AnkleVolumeLEFT>='"+interval[0]+"' AND AnkleVolumeLEFT<='"+interval[1]+"') OR " +
																		 " (AnkleVolumeRIGHT>='"+interval[0]+"' AND AnkleVolumeRIGHT<='"+interval[1]+"') AND ";
															}else{
																// интервалов нет
																WHERE += " OR AnkleVolumeLEFT='"+ankle_volume_field+"' AND AnkleVolumeRIGHT='"+ankle_volume_field+"' AND ";
															}
														}
													}
												}				
										 }
				
				
												/***************************************************************
												 * ОБЪЕМ КВ
												 * */if(kv_volume.length() != 0){
														if(WHERE.equals("")){
														//WHERE  ПУСТОЙ
															//разбиваем по пробелу полученные данные
															String[] arr_kv_volume = kv_volume.split(" ");
															//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
															int iteration=0;
															for (String kv_volume_field : arr_kv_volume) {
																iteration++;
																if(iteration == arr_kv_volume.length){
																	//значит элемент массива последний
																	//если есть дефис, значит это интервал
																	if(kv_volume_field.indexOf("-") != -1){
																		// разбираем интервал
																		String[] interval = kv_volume_field.split("-");
																		WHERE += " (KvVolumeLEFT>='"+interval[0]+"' AND KvVolumeLEFT<='"+interval[1]+"') OR " +
																				 " (KvVolumeRIGHT>='"+interval[0]+"' AND KvVolumeRIGHT<='"+interval[1]+"') ";
																	}else{
																		// интервалов нет
																			WHERE += " KvVolumeLEFT='"+kv_volume_field+"' AND KvVolumeRIGHT='"+kv_volume_field+"' ";	
																	}
																}else{
																	// элемент массива не последний
																	if(kv_volume_field.indexOf("-") != -1){
																		// разбираем интервал
																		String[] interval = kv_volume_field.split("-");
																		WHERE += " (KvVolumeLEFT>='"+interval[0]+"' AND KvVolumeLEFT<='"+interval[1]+"') OR " +
																				 " (KvVolumeLEFT>='"+interval[0]+"' AND KvVolumeRIGHT<='"+interval[1]+"') AND ";
																	}else{
																		// интервалов нет
																		WHERE += " KvVolumeLEFT='"+kv_volume_field+"' AND KvVolumeRIGHT='"+kv_volume_field+"' AND ";
																	}	
																}
															}
														}else{
														//WHERE НЕ ПУСТОЙ
															//разбиваем по пробелу полученные данные
															String[] arr_kv_volume = kv_volume.split(" ");
															//необходимо обойти каждый элемент массива в цикле и проверяем на наличие диапазона
															int iteration=0;
															for (String kv_volume_field : arr_kv_volume) {
																iteration++;
																if(iteration == arr_kv_volume.length){
																	//значит элемент массива последний
																	//если есть дефис, значит это интервал
																	if(kv_volume_field.indexOf("-") != -1){
																		// разбираем интервал
																		String[] interval = kv_volume_field.split("-");
																		WHERE += " OR (KvVolumeLEFT>='"+interval[0]+"' AND KvVolumeLEFT<='"+interval[1]+"') OR " +
																				 " (KvVolumeRIGHT>='"+interval[0]+"' AND KvVolumeRIGHT<='"+interval[1]+"') ";
																	}else{
																		// интервалов нет
																			WHERE += " OR KvVolumeLEFT='"+kv_volume_field+"' AND KvVolumeRIGHT='"+kv_volume_field+"' ";	
																	}
																}else{
																	// элемент массива не последний
																	if(kv_volume_field.indexOf("-") != -1){
																		// разбираем интервал
																		String[] interval = kv_volume_field.split("-");
																		WHERE += " OR (KvVolumeLEFT>='"+interval[0]+"' AND KvVolumeLEFT<='"+interval[1]+"') OR " +
																				 " (KvVolumeRIGHT>='"+interval[0]+"' AND KvVolumeRIGHT<='"+interval[1]+"') AND ";
																	}else{
																		// интервалов нет
																		WHERE += " OR KvVolumeLEFT='"+kv_volume_field+"' AND KvVolumeRIGHT='"+kv_volume_field+"' AND ";
																	}
																}
															}
														}	
												 }

				if (materialsCursor != null) {
					if (materialsCursor.moveToFirst()) {
						do {
							int id = materialsCursor.getInt( materialsCursor.getColumnIndex("_id") );
							int checked = materialsCursor.getInt( materialsCursor.getColumnIndex("MaterialChecked") );
							
							if(checked==1){
								if(WHERE.equals("")){
									WHERE += " o.MaterialID='"+id+"' ";
								}else{
									WHERE += " OR o.MaterialID='"+id+"' ";
								}
							}
						} while (materialsCursor.moveToNext());
					}
					materialsCursor.close();
				}
				
				if (employeeCursor != null) {
					if (employeeCursor.moveToFirst()) {
						do {
							int id = employeeCursor.getInt( employeeCursor.getColumnIndex("_id") );
							int checked = employeeCursor.getInt( employeeCursor.getColumnIndex("EmployeeChecked") );
							
							if(checked==1){
								if(WHERE.equals("")){
									WHERE += " o.EmployeeID='"+id+"' ";
								}else{
									WHERE += " OR o.EmployeeID='"+id+"' ";
								}
							}
						} while (employeeCursor.moveToNext());
					}
					employeeCursor.close();
				}
				
				db.cleanMaterialChecked();
				db.cleanEmployeeChecked();
				
				Log.d(LOG_TAG, WHERE);
				Toast.makeText(getApplicationContext(), " "+WHERE, Toast.LENGTH_LONG).show();
				
				Intent ext_search_result = new Intent(this, AllOrdersActivityShort.class);
				ext_search_result.putExtra("EXT_SEARCH_WHERE", WHERE);
				startActivity(ext_search_result);
				finish();
				break;
			case R.id.search_employee:
				showDialog(DIALOG_EMPLOYEES);
				break;
			case R.id.versionCode:
				showDialog(DIALOG_MATERIALS);
				break;
			default:
				break;
		}//end switch

	}
/*	
	public void createWere(String query_string, String valueLEFT, String valueRIGHT, String WHERE){
	}//end create where
	*/
}// end SearchActivity