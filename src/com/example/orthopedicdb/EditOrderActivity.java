package com.example.orthopedicdb;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditOrderActivity extends Activity implements OnClickListener {

	long ID;
	ListView lv;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;
	DB db;
	final String LOG_TAG = "myLogs";
	
	EditText update_order_number;
	AutoCompleteTextView update_model;
	Spinner spinner_update_material;
	EditText update_size_left;
	EditText update_size_right;
	EditText update_urk_left;
	EditText update_urk_right;
	EditText update_height_LEFT;
	EditText update_height_RIGHT;
	EditText update_top_volume_LEFT;
	EditText update_top_volume_RIGHT;
	EditText update_ankle_volume_LEFT;
	EditText update_ankle_volume_RIGHT;
	EditText update_kv_volume_LEFT;
	EditText update_kv_volume_RIGHT;
	EditText update_customerSN;
	EditText update_customerFN;
	EditText update_customerP;
	Spinner spinner_update_employee;
	Button submit_update;
	ImageView modelIMG;
	
	String order_number_before;
	String model;
	String size_left;
	String size_right;
	String urk_left;
	String urk_right;
	String height_left;
	String height_right;
	String top_volume_left;
	String top_volume_right;
	String ankle_volume_left;
	String ankle_volume_right;
	String kv_volume_left;
	String kv_volume_right;
	String customersn;
	String customerfn;
	String customerp;
	String model_img_src;
	
	int old_employee;
	int old_material;
	
	long changed_material;
	long changed_employee;
	
	final int REQUEST_ADD_MATERIAL = 1;
	final int REQUEST_ADD_EMPLOYEE = 2;
	final int REQUEST_ADD_FOTO 	   = 3;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_order);

		Intent intent = getIntent();
		ID = intent.getLongExtra("ID", 1);

		// подключаемся к БД
		db = new DB(this);
		db.open();

		cursor = db.getDetailedOrderById(ID);
		cursor.moveToFirst();
		
		submit_update = (Button)findViewById(R.id.submit_update);
		submit_update.setOnClickListener(this);
		modelIMG = (ImageView)findViewById(R.id.updateModelIMG);
		modelIMG.setOnClickListener(this);
		
		update_order_number 		= (EditText)findViewById(R.id.updateOrderID);
		update_size_left 			= (EditText)findViewById(R.id.updateSizeLeft);
		update_size_right 			= (EditText)findViewById(R.id.updateSizeRight);
		update_urk_left 			= (EditText)findViewById(R.id.updateUrkLeft);
		update_urk_right 			= (EditText)findViewById(R.id.updateUrkRight);
		update_height_LEFT 			= (EditText)findViewById(R.id.updateHeightLeft);
		update_height_RIGHT 		= (EditText)findViewById(R.id.updateHeightRight);
		update_top_volume_LEFT 		= (EditText)findViewById(R.id.updateTopVolumeLeft);
		update_top_volume_RIGHT 	= (EditText)findViewById(R.id.updateTopVolumeRight);
		update_ankle_volume_LEFT 	= (EditText)findViewById(R.id.updateAnkleVolumeLeft);
		update_ankle_volume_RIGHT 	= (EditText)findViewById(R.id.updateAnkleVolumeRight);
		update_kv_volume_LEFT 		= (EditText)findViewById(R.id.updateKvVolumeLeft);
		update_kv_volume_RIGHT 		= (EditText)findViewById(R.id.updateKvVolumeRight);
		update_customerSN 			= (EditText)findViewById(R.id.updateCustomerSN);
		update_customerFN 			= (EditText)findViewById(R.id.updateCustomerFN);
		update_customerP 			= (EditText)findViewById(R.id.updateCustomerP);
		
		order_number_before	= cursor.getString( cursor.getColumnIndex("OrderID") );
		size_left 			= cursor.getString( cursor.getColumnIndex("SizeLEFT") );
		size_right 			= cursor.getString( cursor.getColumnIndex("SizeRIGHT") );
		urk_left 			= cursor.getString( cursor.getColumnIndex("UrkLEFT") );
		urk_right 			= cursor.getString( cursor.getColumnIndex("UrkRIGHT") );
		height_left 		= cursor.getString( cursor.getColumnIndex("HeightLEFT") );
		height_right 		= cursor.getString( cursor.getColumnIndex("HeightRIGHT") );
		top_volume_left 	= cursor.getString( cursor.getColumnIndex("TopVolumeLEFT") );
		top_volume_right 	= cursor.getString( cursor.getColumnIndex("TopVolumeRIGHT") );
		ankle_volume_left 	= cursor.getString( cursor.getColumnIndex("AnkleVolumeLEFT") );
		ankle_volume_right 	= cursor.getString( cursor.getColumnIndex("AnkleVolumeRIGHT") );
		kv_volume_left 		= cursor.getString( cursor.getColumnIndex("KvVolumeLEFT") );
		kv_volume_right 	= cursor.getString( cursor.getColumnIndex("KvVolumeRIGHT") );
		customersn 			= cursor.getString( cursor.getColumnIndex("CustomerSN") );
		customerfn 			= cursor.getString( cursor.getColumnIndex("CustomerFN") );
		customerp 			= cursor.getString( cursor.getColumnIndex("CustomerP") );
		model_img_src 		= cursor.getString( cursor.getColumnIndex("ModelIMG") );
		
		
		update_order_number.setText(order_number_before);
		update_size_left.setText(size_left);	
		update_size_right.setText(size_right);	
		update_urk_left.setText(urk_left);	
		update_urk_right.setText(urk_right);	
		update_height_LEFT.setText(height_left);	
		update_height_RIGHT.setText(height_right);	
		update_top_volume_LEFT.setText(top_volume_left);	
		update_top_volume_RIGHT.setText(top_volume_right);	
		update_ankle_volume_LEFT.setText(ankle_volume_left);	
		update_ankle_volume_RIGHT.setText(ankle_volume_right);	
		update_kv_volume_LEFT.setText(kv_volume_left);	
		update_kv_volume_RIGHT.setText(kv_volume_right);	
		update_customerSN.setText(customersn);	
		update_customerFN.setText(customerfn);	
		update_customerP.setText(customerp);
//		if(model_img_src.equals(""))
//			modelIMG.setImageResource(R.drawable.ic_launcher);
//		else
//			modelIMG.setImageURI(Uri.parse(model_img_src));
			setPic(model_img_src);

		// узнаем каков у заказа МАТЕРИАЛ для вызова диалогового окна для его выбора
		old_material = cursor.getInt( cursor.getColumnIndex("MaterialID") )-1;
			spinner_update_material = (Spinner)findViewById(R.id.updateMaterial);
	        List<String> materials = db.getMaterialList();
	        ArrayAdapter<String> uddateMaterialAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, materials);
	        uddateMaterialAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
	        spinner_update_material.setAdapter(uddateMaterialAdapter);
	        spinner_update_material.setPrompt("Выберите материал");
	        spinner_update_material.setOnItemSelectedListener(materialUpdated);
	        spinner_update_material.setSelection(old_material);
	        
		// узнаем МОДЕЛЬЕР заказа для вызова диалогового окна для его выбора
		old_employee = cursor.getInt( cursor.getColumnIndex("EmployeeID") )-1;
			spinner_update_employee = (Spinner)findViewById(R.id.updateEmployee);
	        List<String> employees = db.getEmployeeList();
	        ArrayAdapter<String> employeeAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, employees);
	        employeeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
	        spinner_update_employee.setAdapter(employeeAdapter);
	        spinner_update_employee.setPrompt("Выберите модельера");
	        spinner_update_employee.setOnItemSelectedListener(employeeUpdated);
	        spinner_update_employee.setSelection(old_employee);
        
        // МОДЕЛЬ - AutoCompleteTextView
        update_model = (AutoCompleteTextView)findViewById(R.id.updateModel);
	        List<String> models = db.getModelList();
	        ArrayAdapter<String> modelsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, models);
	        update_model.setAdapter(modelsAdapter);
	        model = cursor.getString( cursor.getColumnIndex("Model") );
	        update_model.setText(model);
	}


	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	}
	
	protected void onStop() {
	    super.onStop();
	    db.close();
	}
	
	protected void onResume() {
	    super.onResume();
	    db = new DB(this);
        db.open();
        
        // обновить списов материалов, при добавлении нового материала
        List<String> materials = db.getMaterialList();
        ArrayAdapter<String> materialAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, materials);
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_update_material.setAdapter(materialAdapter);
        spinner_update_material.setPrompt("Выберите материал");
        spinner_update_material.setOnItemSelectedListener(materialUpdated);
        spinner_update_material.setSelection(old_material);
        
        // обновить список модельеров, при создании нового
        List<String> employees = db.getEmployeeList();
        ArrayAdapter<String> employeeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, employees);
        employeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_update_employee.setAdapter(employeeAdapter);
        spinner_update_employee.setPrompt("Выберите модельера");
        spinner_update_employee.setOnItemSelectedListener(employeeUpdated);
        spinner_update_employee.setSelection(old_employee);
	}
	
	public void onBackPressed() {
		Intent intent = new Intent(getApplicationContext(), DetailOrderActivity.class);
		intent.putExtra("ID", ID);
		startActivity(intent);
		finish();
	}
	
	public OnItemSelectedListener employeeUpdated = new OnItemSelectedListener() {
		@Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(position == 0){
          		Intent intent = new Intent(getApplicationContext(), NewEmployee.class);
          	    startActivityForResult(intent, REQUEST_ADD_EMPLOYEE);
          	}else{
          		changed_employee = id + 1;
          	}
        }
        public void onNothingSelected(AdapterView<?> arg0) {}
      };
      
      public OnItemSelectedListener materialUpdated = new OnItemSelectedListener() {
  		@Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
  			if(position == 0){
  					Intent intent = new Intent(getApplicationContext(), NewMaterial.class);
  					startActivityForResult(intent, REQUEST_ADD_MATERIAL);
        	}else{
        		changed_material = id + 1;
        	}
          }
          public void onNothingSelected(AdapterView<?> arg0) {}
      };
      
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  	    if (resultCode == RESULT_OK) {
  	        switch (requestCode) {
  	        
  	        case REQUEST_ADD_MATERIAL:	
  	        	String material = data.getStringExtra("material");
  	        	changed_material = (int) db.addNewMaterial(material);
  	        	Toast.makeText(this, "Добавлен новый материал: " + material, Toast.LENGTH_SHORT).show();
  	          break;
  	          
  	        case REQUEST_ADD_EMPLOYEE:
  	        	String surname 		= data.getStringExtra("surname");
  	    	    String firstname 	= data.getStringExtra("firstname");
  	    	    String patronymic 	= data.getStringExtra("patronymic");
  	    	    changed_employee = (int) db.addNewEmployee(surname, firstname, patronymic);
  	    	    Toast.makeText(this, "Модельер "+surname +" "+firstname+" "+patronymic+" добавлен!", Toast.LENGTH_SHORT).show();
  	          break;
  	          
  	        case REQUEST_ADD_FOTO:
  	                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
  	                modelIMG.setImageBitmap(thumbnail);
  	                model_img_src = data.getStringExtra("srс");
  	                Toast.makeText(this, "Фотография закреплена за моделью!", Toast.LENGTH_SHORT).show();
  	        	break;
  	        }
  	      // если вернулось не ОК
  	    } else {
  	    	Toast.makeText(this, "Ничего не добавлено", Toast.LENGTH_SHORT).show();
  	    }
  	}

   // Декодирование масштабированого изображения
  	private void setPic(String photoPath) {
  	    // Get the dimensions of the View
  	    int targetW = modelIMG.getWidth();
  	    int targetH = modelIMG.getHeight();
  	 
  	    // Get the dimensions of the bitmap
  	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
  	    bmOptions.inJustDecodeBounds = true;
  	    BitmapFactory.decodeFile(photoPath, bmOptions);
  	    int photoW = bmOptions.outWidth;
  	    int photoH = bmOptions.outHeight;
  	 
  	    // Determine how much to scale down the image
  	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
  	 
  	    // Decode the image file into a Bitmap sized to fill the View
  	    bmOptions.inJustDecodeBounds = true;
  	    bmOptions.inSampleSize = scaleFactor;
  	    bmOptions.inPurgeable = true;
  	 
  	    Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
  	    modelIMG.setImageBitmap(bitmap);
  	}
      
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit_update:
			
			String order_number_after = update_order_number.getText().toString().trim();
			model 				= update_model.getText().toString().trim();
			size_left 			= update_size_left.getText().toString().trim();
			size_right 			= update_size_right.getText().toString().trim();
			urk_left 			= update_urk_left.getText().toString().trim();
			urk_right 			= update_urk_right.getText().toString().trim();
			height_left 		= update_height_LEFT.getText().toString().trim();
			height_right 		= update_height_RIGHT.getText().toString().trim();
			top_volume_left 	= update_top_volume_LEFT.getText().toString().trim();
			top_volume_right 	= update_top_volume_RIGHT.getText().toString().trim();
			ankle_volume_left 	= update_ankle_volume_LEFT.getText().toString().trim();
			ankle_volume_right 	= update_ankle_volume_RIGHT.getText().toString().trim();
			kv_volume_left 		= update_kv_volume_LEFT.getText().toString().trim();
			kv_volume_right 	= update_kv_volume_RIGHT.getText().toString().trim();
			customersn 			= update_customerSN.getText().toString().trim();
			customerfn 			= update_customerFN.getText().toString().trim();
			customerp 			= update_customerP.getText().toString().trim();
			
			if(!order_number_before.equals(order_number_after)){
				if(!db.checkID(order_number_after)){
					update_order_number.setError("Такой заказ уже есть в базе");
					return;
				}
			}
			
			
			db.updateOrderById(ID, 
							order_number_after, 
							model,
							model_img_src, 
							changed_material,
							size_left,
							size_right, 
							urk_left, 
							urk_right,
							height_left, 
							height_right, 
							top_volume_left,
							top_volume_right, 
							ankle_volume_left,
							ankle_volume_right, 
							kv_volume_left,
							kv_volume_right, 
							customersn, 
							customerfn,
							customerp, 
							changed_employee);
			
			Toast.makeText(this, "Изменения сохранены!", Toast.LENGTH_LONG).show();
			onBackPressed();
			break;
		case R.id.updateModelIMG:
			
			Toast.makeText(this, "Обновляем фотку", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		
	}
}
