package com.example.orthopedicdb;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewOrderActivity extends Activity implements OnClickListener{
	final String LOG_TAG = "myLogs";
	Button submit_new_order;
	ImageButton get_picture;

    int choosed_material;
    long choosed_employee;
    String model_img_src = "no_image";
    
	EditText new_order_number;
	AutoCompleteTextView new_model;
	String model;
	EditText new_size;
	EditText new_urk;
	EditText new_height;
	EditText new_top_volume;
	EditText new_ankle_volume;
	EditText new_kv_volume;
	EditText new_customer;
	Spinner spinner_new_employee;
	Spinner spinner_materials;
	
	// метки корректности
	ImageView imgID;
	
	DB db;
	
	final int REQUEST_ADD_MATERIAL = 1;
	final int REQUEST_ADD_EMPLOYEE = 2;
	final int REQUEST_ADD_FOTO = 3;
	
	@Override // ON CREATE
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_order_activity);
        
        // подключаемся к БД
        db = new DB(this);
        db.open();
        
        // инициализируем КНОПКИ и прикрепляем к ним слушателя
        submit_new_order = (Button)findViewById(R.id.submit_new_order);
        submit_new_order.setOnClickListener(this);
//        submit_new_order.setEnabled(false);
        
        get_picture = (ImageButton)findViewById(R.id.get_model_image);
        get_picture.setOnClickListener(this);
        
        /********
         **ПОЛЯ**
         ********/
        // Материалы - SPINNER
        spinner_materials = (Spinner)findViewById(R.id.new_materials);
        List<String> materials = db.getMaterialList();
        ArrayAdapter<String> materialAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, materials);
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_materials.setAdapter(materialAdapter);
        spinner_materials.setPrompt("Выберите материал");
        spinner_materials.setOnItemSelectedListener(materialSelected);
        spinner_materials.setSelection(1);

        // Сотрудники - SPINNER
        spinner_new_employee = (Spinner)findViewById(R.id.new_employee);
        List<String> employees = db.getEmployeeList();
        ArrayAdapter<String> employeeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, employees);
        employeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_new_employee.setAdapter(employeeAdapter);
        spinner_new_employee.setPrompt("Выберите модельера");
        spinner_new_employee.setOnItemSelectedListener(employeeSelected);
        spinner_new_employee.setSelection(1);

        // Модель - AutoCompleteTextView
        new_model = (AutoCompleteTextView)findViewById(R.id.new_model);
        List<String> models = db.getModelList();
        ArrayAdapter<String> modelsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, models);
        new_model.setAdapter(modelsAdapter);
        
        new_order_number = (EditText)findViewById(R.id.new_order_number);
        new_size		 = (EditText)findViewById(R.id.new_size);
        new_urk		  	 = (EditText)findViewById(R.id.new_urk);
        new_height		 = (EditText)findViewById(R.id.new_height);
        new_top_volume	 = (EditText)findViewById(R.id.new_TopVolume);
        new_ankle_volume = (EditText)findViewById(R.id.new_AnkleVolume);
        new_kv_volume	 = (EditText)findViewById(R.id.new_KvVolume);
        new_customer	 = (EditText)findViewById(R.id.new_customer);
	}//END ON CREATE


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
        spinner_materials.setAdapter(materialAdapter);
        spinner_materials.setPrompt("Выберите материал");
        spinner_materials.setOnItemSelectedListener(materialSelected);
        spinner_materials.setSelection(1);
        
        // обновить список модельеров, при создании нового
        List<String> employees = db.getEmployeeList();
        ArrayAdapter<String> employeeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, employees);
        employeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_new_employee.setAdapter(employeeAdapter);
        spinner_new_employee.setPrompt("Выберите модельера");
        spinner_new_employee.setOnItemSelectedListener(employeeSelected);
        spinner_new_employee.setSelection(1);
	}

	public OnItemSelectedListener materialSelected = new OnItemSelectedListener() {
		@Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        	if(position == 0){
        		Intent intent = new Intent(getApplicationContext(), NewMaterial.class);
        	    startActivityForResult(intent, REQUEST_ADD_MATERIAL);
        	}else{
        		choosed_material = position;
        	}
        }
        public void onNothingSelected(AdapterView<?> arg0) {}
      };
	
      
    public OnItemSelectedListener employeeSelected = new OnItemSelectedListener() {
    	@Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        	if(position == 0){
          		Intent intent = new Intent(getApplicationContext(), NewEmployee.class);
          	    startActivityForResult(intent, REQUEST_ADD_EMPLOYEE);
          	}else{
          		choosed_employee = position;
          	}
          }
          public void onNothingSelected(AdapterView<?> arg0) {}
        };
	
	
	// СОЗДАЕМ МЕНЮ
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
	}
	
	// ОТКЛИК НА МЕНЮ
    public boolean onOptionsItemSelected(MenuItem item){

    	switch (item.getItemId()) {
    	
			case R.id.MENU_NEW_ORDER:
				Intent newOrderIntent = new Intent();
				newOrderIntent.setClass(getApplicationContext(), NewOrderActivity.class);
				startActivity(newOrderIntent);
				break;
	
			case R.id.MENU_SEARCH:
				Intent searchIntent = new Intent();
				searchIntent.setClass(getApplicationContext(), SearchActivity.class);
				startActivity(searchIntent);
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
			
			case R.id.MENU_DEFAULT_SCREEN:
				
				break;
		}
		return true;
    }


    /* Результаты из активити*/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    /*
		if (data == null) {
	    	return;
	    }
	     */
	    if (resultCode == RESULT_OK) {
	        switch (requestCode) {
	        
	        case REQUEST_ADD_MATERIAL:	
	        	String material = data.getStringExtra("material");
	        	db.addNewMaterial(material);
	        	Toast.makeText(this, "Добавлен новый материал: " + material, Toast.LENGTH_SHORT).show();
	          break;
	          
	        case REQUEST_ADD_EMPLOYEE:
	        	String surname 		= data.getStringExtra("surname");
	    	    String firstname 	= data.getStringExtra("firstname");
	    	    String patronymic 	= data.getStringExtra("patronymic");
	    	    choosed_employee = db.addNewEmployee(surname, firstname, patronymic);
	    	    Toast.makeText(this, "Модельер "+surname +" "+firstname+" "+patronymic+" добавлен!", Toast.LENGTH_SHORT).show();
	          break;
	          
	        case REQUEST_ADD_FOTO:
	                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
	                get_picture.setImageBitmap(thumbnail);
	                model_img_src = data.getStringExtra("srс");
	                Toast.makeText(this, "Фотография закреплена за моделью!", Toast.LENGTH_SHORT).show();
	        	break;
	        }
	      // если вернулось не ОК
	    } else {
	    	Toast.makeText(this, "Ничего не добавлено", Toast.LENGTH_SHORT).show();
	    }
	}
    
	
	// КНОПКА ОТПРАВИТЬ
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit_new_order:
			
			final String order_number = new_order_number.getText().toString().trim();
			String model 		  	  = new_model.getText().toString().trim();
			final String size		  = new_size.getText().toString().trim();
			final String urk 		  = new_urk.getText().toString().trim();
			final String height 	  = new_height.getText().toString().trim();
			final String top_volume   = new_top_volume.getText().toString().trim();
			final String ankle_volume = new_ankle_volume.getText().toString().trim();
			final String kv_volume 	  = new_kv_volume.getText().toString().trim();
			final String customer 	  = new_customer.getText().toString().trim();
	  
			final String size_left;
			final String size_right;
			final String urk_left;
			final String urk_right;
			final String height_left;
			final String height_right;
			final String top_volume_left;
			final String top_volume_right;
			final String ankle_volume_left;
			final String ankle_volume_right;
			final String kv_volume_left;
			final String kv_volume_right;
			
			/*
			Matcher match_new_size 			= Pattern.compile("(\\d\\d)|(\\d\\d \\d\\d)").matcher(size);
	        Matcher match_new_urk 			= Pattern.compile("(\\d\\d\\d)|(\\d\\d \\d\\d\\d)").matcher(urk);
	        Matcher match_new_height 		= Pattern.compile("(\\d\\d\\.\\d)|(\\d\\d\\.\\d)").matcher(height);
	        Matcher match_new_top_volume 	= Pattern.compile("(\\d\\d\\.\\d)|(\\d\\d\\.\\d)").matcher(top_volume);
	        Matcher match_new_ankle_volume  = Pattern.compile("(\\d\\d\\.\\d)|(\\d\\d\\.\\d)").matcher(ankle_volume);
	        Matcher match_new_kv_volume 	= Pattern.compile("(\\d\\d\\.\\d)|(\\d\\d\\.\\d)").matcher(kv_volume);
	       
	        
	        if(order_number.length() == 0){
				new_order_number.setError("Введите номер заказа");
				return;
			}
	        
	        if(!mydb.checkID(order_number)){
				new_order_number.setError("Такой заказ уже есть в базе");
				return;
			}
			
			if(model.length() == 0){
				new_model.setError("Введите номер модели");
				return;
			}
			
			if(size.length() == 0){
				new_size.setError("Введите размер");
				return;
			}
			
			if(urk.length() == 0){
				new_urk.setError("Данные по УРК введены неверно");
				return;
			}
			
			if(!match_new_height.find()){
				new_height.setError("Данные по высоте введены неверно");
				return;
			}
			
			if(!match_new_top_volume.find()){
				new_top_volume.setError("Объем верха введен неверно");
				return;
			}
			
			if(!match_new_ankle_volume.find()){
				new_ankle_volume.setError("Объем лодыжки введен неверно");
				return;
			}
			
			if(!match_new_kv_volume.find()){
				new_kv_volume.setError("Объем КВ введен неверно");
				return;
			}
			
			if(customer.isEmpty()){
				new_customer.setError("Введите Ф.И.О заказчика");
				return;
			}
			
			if(choosed_material == 0){
				Toast.makeText(getApplicationContext(), "Выберите материал!", Toast.LENGTH_LONG).show();
				return;
			}
		    if(choosed_employee == 0){
				Toast.makeText(getApplicationContext(), "Выберите мадельера!", Toast.LENGTH_LONG).show();
				return;
			}
			
			*/
			
			
		  if(size.length() > 2){
		     	String[] arr_size = size.split(" ");
		     	size_left  = arr_size[0];
		     	size_right = arr_size[1];
		  }else{
		     	size_left = size_right = size;
		  }
		
		  // УРК //
		  if(urk.length() > 2){
		     	String[] arr_urk = urk.split(" ");
		     	urk_left  = arr_urk[0];
		     	urk_right = arr_urk[1];
		  }else{
		     	urk_left = urk_right = urk;
		  }
		  
		  // ВЫСОТА //
		  if(height.length() > 2){
		     	String[] arr_height = height.split(" ");
		     	height_left  = arr_height[0];
		     	height_right = arr_height[1];
		  }else{
		  	height_left = height_right = height;
		  }
		  
		  // ОБЪЕМ ВЕРХА //
		  if(top_volume.length() > 4){
		     	String[] arr_top_volume = top_volume.split(" ");
		     	top_volume_left  = arr_top_volume[0];
		     	top_volume_right = arr_top_volume[1];
		  }else{
		  	top_volume_left = top_volume_right = top_volume;
		  }
		  
		  // ОБЪЕМ ЛОДЫЖКИ //
		  if(ankle_volume.length() > 4){
		     	String[] arr_ankle_volume = ankle_volume.split(" ");
		     	ankle_volume_left  = arr_ankle_volume[0];
		     	ankle_volume_right = arr_ankle_volume[1];
		  }else{
		  	ankle_volume_left = ankle_volume_right = ankle_volume;
		  }
		  
		  // ОБЪЕМ КВ //
		  if(kv_volume.length() > 4){
		     	String[] arr_kv_volume = kv_volume.split(" ");
		     	kv_volume_left  = arr_kv_volume[0];
		     	kv_volume_right = arr_kv_volume[1];
		  }else{
		  	kv_volume_left = kv_volume_right = kv_volume;
		  }
		  
		  String[] customerFIO = customer.split(" ");
		  String customerSN = customerFIO[0];
		  String customerFN = customerFIO[1];
		  String customerP  = customerFIO[2];
		  
			//Toast.makeText(getApplicationContext(), "Размер: " + order_number, Toast.LENGTH_LONG).show();
		  
			db.addNewOrder(order_number,
						 model,
						 model_img_src,
						 choosed_material,
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
						 customerSN,
						 customerFN,
						 customerP,
						 choosed_employee);
			
			Toast.makeText(getApplicationContext(), "Запись успешно добавлена!", Toast.LENGTH_SHORT).show();
			
			new_order_number.setText("");
			new_model.setText("");
			new_size.setText("");
			new_urk.setText("");
			new_height.setText("");
			new_top_volume.setText("");
			new_ankle_volume.setText("");
			new_kv_volume.setText("");
			new_customer.setText("");
			
			break;

		case R.id.get_model_image:
			
			Intent intentGetFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intentGetFoto, REQUEST_ADD_FOTO);
			break;
		default:
			break;
		}

	}
}
