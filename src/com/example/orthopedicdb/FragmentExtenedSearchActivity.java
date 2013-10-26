package com.example.orthopedicdb;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class FragmentExtenedSearchActivity extends Fragment {

	protected static final String LOG_TAG = "myLogs";
	Button submit_search;
	Button search_employee;
	Button search_material;
	String WHERE = "";
	DB db;
	DialogFragment materialDialog;
	DialogFragment employeeDialog;
	
	public static Cursor materialsCursor;
	public static Cursor employeeCursor;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_activity, null);

		submit_search = (Button) view.findViewById(R.id.submit_search);
		search_employee = (Button) view.findViewById(R.id.search_employee);
		search_material = (Button) view.findViewById(R.id.versionCode);
		submit_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String order_number = edit_order_number.getText().toString().trim();
				String model = edit_model.getText().toString().trim();
				String size = edit_size.getText().toString().trim();
				String urk = edit_urk.getText().toString().trim();
				String height = edit_height.getText().toString().trim();
				String top_volume = edit_top_volume.getText().toString().trim();
				String ankle_volume = edit_ankle_volume.getText().toString().trim();
				String kv_volume = edit_kv_volume.getText().toString().trim();
				String customer = edit_customer.getText().toString().trim();

				if (order_number.length() != 0) {
					WHERE += " OrderID='" + order_number + "' ";
				}

				/***************************************************************
				 * МОДЕЛЬ
				 * */

				if (model.length() != 0) {
					if (WHERE.equals("")) {
						WHERE += " o.ModelID='" + model + "' ";
					} else {
						WHERE += " OR o.ModelID='" + model + "' ";
					}
				}

				/***************************************************************
				 * РАЗМЕРЫ
				 * */
				if (size.length() != 0) {
					if (WHERE.equals("")) {
						// WHERE ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_size = size.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String size_field : arr_size) {
							iteration++;
							if (iteration == arr_size.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (size_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = size_field.split("-");
									WHERE += " (o.SizeLEFT>='" + interval[0] + "' AND o.SizeLEFT<='" + interval[1] + "') OR "
											+ " (o.SizeRIGHT>='" + interval[0]+ "' AND o.SizeRIGHT<='" + interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " o.SizeLEFT='" + size_field + "' AND o.SizeRIGHT='" + size_field + "' ";
								}
							} else {
								// элемент массива не последний
								if (size_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = size_field.split("-");
									WHERE += " (o.SizeLEFT>='" + interval[0]+ "' AND o.SizeLEFT<='"+ interval[1] + "') OR "
											+ " (o.SizeRIGHT>='" + interval[0]+ "' AND o.SizeRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " o.SizeLEFT='" + size_field+ "' AND o.SizeRIGHT='"+ size_field + "' AND ";
								}
							}
						}
					} else {
						// WHERE НЕ ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_size = size.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String size_field : arr_size) {
							iteration++;
							if (iteration == arr_size.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (size_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = size_field.split("-");
									WHERE += " OR (o.SizeLEFT>='" + interval[0]+ "' AND o.SizeLEFT<='"+ interval[1] + "') OR "
											+ " (o.SizeRIGHT>='" + interval[0]+ "' AND o.SizeRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " OR o.SizeLEFT='" + size_field+ "' AND o.SizeRIGHT='"+ size_field + "' ";
								}
							} else {
								// элемент массива не последний
								if (size_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = size_field.split("-");
									WHERE += " OR (o.SizeLEFT>='" + interval[0]+ "' AND o.SizeLEFT<='"+ interval[1] + "') OR "
											+ " (o.SizeRIGHT>='" + interval[0]+ "' AND o.SizeRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " OR o.SizeLEFT='" + size_field+ "' AND o.SizeRIGHT='"+ size_field + "' AND ";
								}
							}
						}
					}
				}

				/***************************************************************
				 * УРК
				 * */
				if (urk.length() != 0) {
					if (WHERE.equals("")) {
						// WHERE ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_urk = urk.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String urk_field : arr_urk) {
							iteration++;
							if (iteration == arr_urk.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (urk_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = urk_field.split("-");
									WHERE += " (o.UrkLEFT>='" + interval[0]+ "' AND o.UrkLEFT<='"+ interval[1] + "') OR "
											+ " (o.UrkRIGHT>='" + interval[0]+ "' AND o.UrkRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " o.UrkLEFT='" + urk_field+ "' AND o.UrkRIGHT='" + urk_field+ "' ";
								}
							} else {
								// элемент массива не последний
								if (urk_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = urk_field.split("-");
									WHERE += " (o.UrkLEFT>='" + interval[0]+ "' AND o.UrkLEFT<='"+ interval[1] + "') OR "
											+ " (o.UrkRIGHT>='" + interval[0]+ "' AND o.UrkRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " o.UrkLEFT='" + urk_field+ "' AND o.UrkRIGHT='" + urk_field+ "' AND ";
								}
							}
						}
					} else {
						// WHERE НЕ ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_urk = urk.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String urk_field : arr_urk) {
							iteration++;
							if (iteration == arr_urk.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (urk_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = urk_field.split("-");
									WHERE += " OR (o.UrkLEFT>='" + interval[0]+ "' AND o.UrkLEFT<='"+ interval[1] + "') OR "
											+ " (o.UrkRIGHT>='" + interval[0]+ "' AND o.UrkRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " OR o.UrkLEFT='" + urk_field+ "' AND o.UrkRIGHT='" + urk_field+ "' ";
								}
							} else {
								// элемент массива не последний
								if (urk_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = urk_field.split("-");
									WHERE += " OR (o.UrkLEFT>='" + interval[0]+ "' AND o.UrkLEFT<='"+ interval[1] + "') OR "+ " (o.UrkRIGHT>='" + interval[0]+ "' AND o.UrkRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " OR o.UrkLEFT='" + urk_field+ "' AND o.UrkRIGHT='" + urk_field+ "' AND ";
								}
							}
						}
					}
				}

				/***************************************************************
				 * ВЫСОТА
				 * */
				if (height.length() != 0) {
					if (WHERE.equals("")) {
						// WHERE ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_height = height.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String height_field : arr_height) {
							iteration++;
							if (iteration == arr_height.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (height_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = height_field.split("-");
									WHERE += " (o.HeightLEFT>='" + interval[0]+ "' AND o.HeightLEFT<='"+ interval[1] + "') OR "
											+ " (o.HeightRIGHT>='"+ interval[0]+ "' AND o.HeightRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " o.HeightLEFT='" + height_field+ "' AND o.HeightRIGHT='"+ height_field + "' ";
								}
							} else {
								// элемент массива не последний
								if (height_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = height_field.split("-");
									WHERE += " (o.HeightLEFT>='" + interval[0]+ "' AND o.HeightLEFT<='"+ interval[1] + "') OR "
											+ " (o.HeightRIGHT>='"+ interval[0]+ "' AND o.HeightRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " o.HeightLEFT='" + height_field+ "' AND o.HeightRIGHT='"+ height_field + "' AND ";
								}
							}
						}
					} else {
						// WHERE НЕ ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_height = height.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String height_field : arr_height) {
							iteration++;
							if (iteration == arr_height.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (height_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = height_field.split("-");
									WHERE += " OR (o.HeightLEFT>='"+ interval[0]+ "' AND o.HeightLEFT<='"+ interval[1] + "') OR "
											+ " (o.HeightRIGHT>='"+ interval[0]+ "' AND o.HeightRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " OR o.HeightLEFT='"+ height_field+ "' AND o.HeightRIGHT='"+ height_field + "' ";
								}
							} else {
								// элемент массива не последний
								if (height_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = height_field.split("-");
									WHERE += " OR (o.HeightLEFT>='"+ interval[0]+ "' AND o.HeightLEFT<='"+ interval[1] + "') OR "
											+ " (HeightRIGHT>='" + interval[0]+ "' AND o.HeightRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " OR o.HeightLEFT='"+ height_field+ "' AND o.HeightRIGHT='"+ height_field + "' AND ";
								}
							}
						}
					}
				}

				/***************************************************************
				 * ОБЪЕМ ВЕРХА
				 * */
				if (top_volume.length() != 0) {
					if (WHERE.equals("")) {
						// WHERE ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_top_volume = top_volume.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String top_volume_field : arr_top_volume) {
							iteration++;
							if (iteration == arr_top_volume.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (top_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = top_volume_field.split("-");
									WHERE += " (o.TopVolumeLEFT>='"+ interval[0]+ "' AND o.TopVolumeLEFT<='"+ interval[1] + "') OR "
											+ " (o.TopVolumeRIGHT>='"+ interval[0]+ "' AND o.TopVolumeRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " o.TopVolumeLEFT='"+ top_volume_field+ "' AND o.TopVolumeRIGHT='"	+ top_volume_field + "' ";
								}
							} else {
								// элемент массива не последний
								if (top_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = top_volume_field.split("-");
									WHERE += " (o.TopVolumeLEFT>='"+ interval[0]+ "' AND o.TopVolumeLEFT<='"+ interval[1] + "') OR "
											+ " (o.TopVolumeRIGHT>='"+ interval[0]+ "' AND o.TopVolumeRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " o.TopVolumeLEFT='"+ top_volume_field+ "' AND o.TopVolumeRIGHT='"	+ top_volume_field + "' AND ";
								}
							}
						}
					} else {
						// WHERE НЕ ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_top_volume = top_volume.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String top_volume_field : arr_top_volume) {
							iteration++;
							if (iteration == arr_top_volume.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (top_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = top_volume_field.split("-");
									WHERE += " OR (o.TopVolumeLEFT>='"+ interval[0]+ "' AND o.TopVolumeLEFT<='"+ interval[1] + "') OR "
											+ " (o.TopVolumeRIGHT>='"+ interval[0]+ "' AND o.TopVolumeRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " OR o.TopVolumeLEFT='"+ top_volume_field+ "' AND o.TopVolumeRIGHT='"+ top_volume_field + "' ";
								}
							} else {
								// элемент массива не последний
								if (top_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = top_volume_field.split("-");
									WHERE += " OR (o.TopVolumeLEFT>='"+ interval[0]+ "' AND o.TopVolumeLEFT<='"+ interval[1] + "') OR "
											+ " (o.TopVolumeRIGHT>='"+ interval[0]	+ "' AND o.TopVolumeRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " OR o.TopVolumeLEFT='"+ top_volume_field	+ "' AND o.TopVolumeRIGHT='"+ top_volume_field + "' AND ";
								}
							}
						}
					}
				}

				/***************************************************************
				 * ОБЪЕМ ЛОДЫЖКИ
				 * */
				if (ankle_volume.length() != 0) {
					if (WHERE.equals("")) {
						// WHERE ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_ankle_volume = ankle_volume.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String ankle_volume_field : arr_ankle_volume) {
							iteration++;
							if (iteration == arr_ankle_volume.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (ankle_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = ankle_volume_field.split("-");
									WHERE += " (AnkleVolumeLEFT>='"+ interval[0]+ "' AND AnkleVolumeLEFT<='"+ interval[1] + "') OR "
											+ " (AnkleVolumeRIGHT>='"+ interval[0]+ "' AND AnkleVolumeRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " AnkleVolumeLEFT='"+ ankle_volume_field+ "' AND AnkleVolumeRIGHT='"+ ankle_volume_field + "' ";
								}
							} else {
								// элемент массива не последний
								if (ankle_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = ankle_volume_field.split("-");
									WHERE += " (AnkleVolumeLEFT>='"+ interval[0]+ "' AND AnkleVolumeLEFT<='"+ interval[1] + "') OR "
											+ " (AnkleVolumeRIGHT>='"+ interval[0]+ "' AND AnkleVolumeRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " AnkleVolumeLEFT='"+ ankle_volume_field+ "' AND AnkleVolumeRIGHT='"+ ankle_volume_field + "' AND ";
								}
							}
						}
					} else {
						// WHERE НЕ ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_ankle_volume = ankle_volume.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String ankle_volume_field : arr_ankle_volume) {
							iteration++;
							if (iteration == arr_ankle_volume.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (ankle_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = ankle_volume_field.split("-");
									WHERE += " OR (AnkleVolumeLEFT>='"+ interval[0]+ "' AND AnkleVolumeLEFT<='"+ interval[1] + "') OR "
											+ " (AnkleVolumeRIGHT>='"+ interval[0]+ "' AND AnkleVolumeRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " OR AnkleVolumeLEFT='"+ ankle_volume_field+ "' AND AnkleVolumeRIGHT='"+ ankle_volume_field + "' ";
								}
							} else {
								// элемент массива не последний
								if (ankle_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = ankle_volume_field.split("-");
									WHERE += " OR (AnkleVolumeLEFT>='"+ interval[0]+ "' AND AnkleVolumeLEFT<='"+ interval[1] + "') OR "
											+ " (AnkleVolumeRIGHT>='"	+ interval[0]+ "' AND AnkleVolumeRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " OR AnkleVolumeLEFT='"+ ankle_volume_field+ "' AND AnkleVolumeRIGHT='"+ ankle_volume_field + "' AND ";
								}
							}
						}
					}
				}

				/***************************************************************
				 * ОБЪЕМ КВ
				 * */
				if (kv_volume.length() != 0) {
					if (WHERE.equals("")) {
						// WHERE ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_kv_volume = kv_volume.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String kv_volume_field : arr_kv_volume) {
							iteration++;
							if (iteration == arr_kv_volume.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (kv_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = kv_volume_field.split("-");
									WHERE += " (KvVolumeLEFT>='" + interval[0]+ "' AND KvVolumeLEFT<='"+ interval[1] + "') OR "
											+ " (KvVolumeRIGHT>='"+ interval[0]+ "' AND KvVolumeRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " KvVolumeLEFT='"+ kv_volume_field+ "' AND KvVolumeRIGHT='"+ kv_volume_field + "' ";
								}
							} else {
								// элемент массива не последний
								if (kv_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = kv_volume_field.split("-");
									WHERE += " (KvVolumeLEFT>='" + interval[0]+ "' AND KvVolumeLEFT<='"	+ interval[1] + "') OR "
											+ " (KvVolumeLEFT>='" + interval[0]+ "' AND KvVolumeRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " KvVolumeLEFT='"+ kv_volume_field+ "' AND KvVolumeRIGHT='"+ kv_volume_field + "' AND ";
								}
							}
						}
					} else {
						// WHERE НЕ ПУСТОЙ
						// разбиваем по пробелу полученные данные
						String[] arr_kv_volume = kv_volume.split(" ");
						// необходимо обойти каждый элемент массива в цикле и
						// проверяем на наличие диапазона
						int iteration = 0;
						for (String kv_volume_field : arr_kv_volume) {
							iteration++;
							if (iteration == arr_kv_volume.length) {
								// значит элемент массива последний
								// если есть дефис, значит это интервал
								if (kv_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = kv_volume_field.split("-");
									WHERE += " OR (KvVolumeLEFT>='"+ interval[0]+ "' AND KvVolumeLEFT<='"+ interval[1] + "') OR "
											+ " (KvVolumeRIGHT>='"+ interval[0]	+ "' AND KvVolumeRIGHT<='"+ interval[1] + "') ";
								} else {
									// интервалов нет
									WHERE += " OR KvVolumeLEFT='"+ kv_volume_field+ "' AND KvVolumeRIGHT='"+ kv_volume_field + "' ";
								}
							} else {
								// элемент массива не последний
								if (kv_volume_field.indexOf("-") != -1) {
									// разбираем интервал
									String[] interval = kv_volume_field.split("-");
									WHERE += " OR (KvVolumeLEFT>='"	+ interval[0]	+ "' AND KvVolumeLEFT<='"	+ interval[1] + "') OR "
											+ " (KvVolumeRIGHT>='"+ interval[0]+ "' AND KvVolumeRIGHT<='"+ interval[1] + "') AND ";
								} else {
									// интервалов нет
									WHERE += " OR KvVolumeLEFT='"+ kv_volume_field+ "' AND KvVolumeRIGHT='"+ kv_volume_field + "' AND ";
								}
							}
						}
					}
				}

				if (materialsCursor != null) {
					if (materialsCursor.moveToFirst()) {
						do {
							int id = materialsCursor.getInt(materialsCursor.getColumnIndex("_id"));
							int checked = materialsCursor.getInt(materialsCursor.getColumnIndex("MaterialChecked"));

							if (checked == 1) {
								if (WHERE.equals("")) {
									WHERE += " o.MaterialID='" + id + "' ";
								} else {
									WHERE += " OR o.MaterialID='" + id + "' ";
								}
							}
						} while (materialsCursor.moveToNext());
					}
					materialsCursor.close();
				}

				if (employeeCursor != null) {
					if (employeeCursor.moveToFirst()) {
						do {
							int id = employeeCursor.getInt(employeeCursor.getColumnIndex("_id"));
							int checked = employeeCursor.getInt(employeeCursor.getColumnIndex("EmployeeChecked"));

							if (checked == 1) {
								if (WHERE.equals("")) {
									WHERE += " o.EmployeeID='" + id + "' ";
								} else {
									WHERE += " OR o.EmployeeID='" + id + "' ";
								}
							}
						} while (employeeCursor.moveToNext());
					}
					employeeCursor.close();
				}
				
				/***************************************************************
				 * ЗАКАЗЧИК
				 * */

				if (customer.length() != 0) {
					if (WHERE.equals("")) {
						WHERE += " o.CustomerSN='" + customer + "' OR o.CustomerFN='"+customer+"' OR o.CustomerP='"+customer+"' ";
					} else {
						WHERE += " OR o.CustomerSN='" + customer + "' OR o.CustomerFN='"+customer+"' OR o.CustomerP='"+customer+"' ";
					}
				}

				db.cleanMaterialChecked();
				db.cleanEmployeeChecked();

				extlistener.getExtWhere(WHERE);
			}
		});////////////***ОТПРАВКА ПОИСКОВОГО ЗАПРОСА***//////////////

		search_material.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				materialDialog = FragmentDialogScreen.newInstance(FragmentDialogScreen.DIALOG_MATERIALS);
				materialDialog.show(getFragmentManager(), "mdlg");
			}
		});
		
		search_employee.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				employeeDialog = FragmentDialogScreen.newInstance(FragmentDialogScreen.DIALOG_EMPLOYEES);
				employeeDialog.show(getFragmentManager(), "edlg");
			}
		});

		// подключаемся к БД
		db = new DB(getActivity());
		db.open();

		edit_order_number = (EditText) view.findViewById(R.id.search_order_number);
		edit_model = (EditText) view.findViewById(R.id.search_model);
		edit_size = (EditText) view.findViewById(R.id.search_size);
		edit_urk = (EditText) view.findViewById(R.id.search_urk);
		edit_height = (EditText) view.findViewById(R.id.search_height);
		edit_top_volume = (EditText) view.findViewById(R.id.search_TopVolume);
		edit_ankle_volume = (EditText) view.findViewById(R.id.search_AnkleVolume);
		edit_kv_volume = (EditText) view.findViewById(R.id.search_KvVolume);
		edit_customer = (EditText) view.findViewById(R.id.search_customer);

		employeeCursor = db.getEmployeeCursor();
		materialsCursor = db.getMaterialCursor();

		return view;
	} // end OnCreate

	public interface OnExtendedSearchClickListener {
	    public void getExtWhere(String where);
	}

	OnExtendedSearchClickListener extlistener;
	
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    extlistener = (OnExtendedSearchClickListener) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActivity().setTitle("Расширенный поиск");
	}
	
	/*
	 * public void createWere(String query_string, String valueLEFT, String
	 * valueRIGHT, String WHERE){ }//end create where
	 */
}// end SearchActivity