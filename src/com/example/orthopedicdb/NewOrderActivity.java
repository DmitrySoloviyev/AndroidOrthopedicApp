package com.example.orthopedicdb;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Spinner;
import android.widget.Toast;

public class NewOrderActivity extends Activity implements OnClickListener{
	final String LOG_TAG = "myLogs";
	Button submit_new_order;
	ImageButton get_picture;

    long choosed_material;
    long choosed_employee;
    String model_img_src = "";
    
	EditText new_order_number;
	AutoCompleteTextView new_model;
	String model;
	EditText new_size;
	EditText new_urk;
	EditText new_height;
	EditText new_top_volume;
	EditText new_ankle_volume;
	EditText new_kv_volume;
	EditText new_customerSN;
	EditText new_customerFN;
	EditText new_customerP;
	Spinner spinner_new_employee;
	Spinner spinner_materials;
	
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
        get_picture = (ImageButton)findViewById(R.id.get_model_image);
        get_picture.setOnClickListener(this);

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
        new_customerSN	 = (EditText)findViewById(R.id.new_customerSN);
        new_customerFN	 = (EditText)findViewById(R.id.new_customerFN);
        new_customerP	 = (EditText)findViewById(R.id.new_customerP);
        
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
        materialAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner_materials.setAdapter(materialAdapter);
        spinner_materials.setPrompt("Выберите материал");
        spinner_materials.setOnItemSelectedListener(materialSelected);
        spinner_materials.setSelection(1);
        
        // обновить список модельеров, при создании нового
        List<String> employees = db.getEmployeeList();
        ArrayAdapter<String> employeeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, employees);
        employeeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
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
        		choosed_material = id + 1;
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
          		choosed_employee = id + 1;
          	}
          }
          public void onNothingSelected(AdapterView<?> arg0) {}
        };
	
    public void onBackPressed() {
    	Intent main = new Intent(getApplicationContext(), MainActivity.class);
    	startActivity(main);
    	finish();
    }
        
        
	// СОЗДАЕМ МЕНЮ
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
	}
	
	// ОТКЛИК НА МЕНЮ
    public boolean onOptionsItemSelected(MenuItem item){
    	Intent intent = new Intent();
    	switch (item.getItemId()) {
    	
			case R.id.MENU_NEW_ORDER:
				break;
	
			case R.id.MENU_SEARCH:
				intent.setClass(getApplicationContext(), FragmentSearchActivity.class);
				startActivity(intent);
				break;
				
			case R.id.MENU_HISTORY:
				intent.setClass(getApplicationContext(), AllOrdersActivityShort.class);
				startActivity(intent);
				break;
				
			case R.id.MENU_GALLERY:
				intent.setClass(getApplicationContext(), GalleryView.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
				finish();
				break;
				
			case R.id.MENU_EXIT:
				finish();
				break;
		}
		return true;
    }


    /* Результаты из активити*/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	        switch (requestCode) {
		        case REQUEST_ADD_MATERIAL:	
		        	if (resultCode == RESULT_OK) {
			        	String material = data.getStringExtra("material");
			        	db.addNewMaterial(material);
			        	Toast.makeText(this, "Добавлен новый материал: " + material, Toast.LENGTH_SHORT).show();
		        	}else{
		        		Toast.makeText(this, "Новый материал не был добавлен", Toast.LENGTH_SHORT).show();
		        	}
		          break;
		          
		        case REQUEST_ADD_EMPLOYEE:
		        	if (resultCode == RESULT_OK) {
			        	String surname 		= data.getStringExtra("surname");
			    	    String firstname 	= data.getStringExtra("firstname");
			    	    String patronymic 	= data.getStringExtra("patronymic");
			    	    choosed_employee = db.addNewEmployee(surname, firstname, patronymic);
			    	    Toast.makeText(this, "Модельер "+surname +" "+firstname+" "+patronymic+" добавлен!", Toast.LENGTH_SHORT).show();
		        	}else{
		        		Toast.makeText(this, "Новый модельер не был добавлен", Toast.LENGTH_SHORT).show();
		        	}
		          break;
		          
		        case REQUEST_ADD_FOTO:
		        	if (resultCode == RESULT_OK) {
		        		Toast.makeText(this, "Фотография закреплена за моделью!", Toast.LENGTH_SHORT).show();
		        	}else{
		        		model_img_src = "";
		        		Toast.makeText(this, "Фотография можели не сохранена!", Toast.LENGTH_SHORT).show();
		        	}
		        	break;
	        }
	}
	
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString("src", model_img_src);
	}
	
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    model_img_src = savedInstanceState.getString("src");
	    if(!model_img_src.equals(""))
	    	Toast.makeText(this, "Фотография закреплена за моделью!", Toast.LENGTH_SHORT).show();
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit_new_order:
			final String order_number = new_order_number.getText().toString().trim();
			String model 		  	  = new_model.getText().toString().trim();
			String size		  		= new_size.getText().toString().trim();
			String urk 		  		= new_urk.getText().toString().trim();
			String height 	  		= new_height.getText().toString().trim();
			String top_volume   	= new_top_volume.getText().toString().trim();
			String ankle_volume 	= new_ankle_volume.getText().toString().trim();
			String kv_volume 	  	= new_kv_volume.getText().toString().trim();
			String customersn 	  	= new_customerSN.getText().toString().trim();
			String customerfn 	  	= new_customerFN.getText().toString().trim();
			String customerp 	  	= new_customerP.getText().toString().trim();
	  
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
			
			if(order_number.length() == 0){
				new_order_number.setError("Введите номер заказа");
				return;
			}
			if(!db.checkID(order_number)){
				new_order_number.setError("Такой заказ уже есть в базе");
				return;
			}
			if(model.length() == 0){
				new_model.setError("Введите номер модели");
				return;
			}
			if(size.length() == 0){
				new_size.setError("Введите размер(ы)");
				return;
			}
			if(urk.length() == 0){
				new_urk.setError("Введите значение(я) УРК");
				return;
			}
			if(height.length() == 0){
				new_height.setError("Введите значение(я) высоты");
				return;
			}
			if(top_volume.length() == 0){
				new_top_volume.setError("Введите значение(я) объема верха");
				return;
			}
			if(ankle_volume.length() == 0){
				new_ankle_volume.setError("Введите значение(я) объема лодыжки");
				return;
			}
			if(kv_volume.length() == 0){
				new_kv_volume.setError("Введите значение(я) КВ");
				return;
			}
			if(customersn.length() == 0){
				new_customerSN.setError("Введите фамилию заказчика");
				return;
			}
			if(customerfn.length() == 0){
				new_customerFN.setError("Введите имя заказчика");
				return;
			}
			if(customerp.length() == 0){
				new_customerP.setError("Введите отчество заказчика");
				return;
			}

			Matcher match_new_size = Pattern.compile("(^\\d\\d$)|(^\\d\\d \\d\\d$)").matcher(size);
			if(!match_new_size.find()){
				new_size.setError("Размер(ы): данные введены некорректно!");
				return;
			}
			
	        Matcher match_new_urk = Pattern.compile("(^\\d\\d\\d$)|(^\\d\\d\\d \\d\\d\\d$)").matcher(urk);
	        if(!match_new_urk.find()){
				new_urk.setError("УРК: данные введены некорректно!");
				return;
			}

	        Matcher match_new_height = Pattern.compile("(^\\d\\d$)|(^\\d\\d \\d\\d$)").matcher(height);
	        if(!match_new_height.find()){
				new_height.setError("Высота: данные введены некорректно!");
				return;
			}

	        Matcher match_new_top_volume = Pattern.compile("(^\\d\\d(\\.\\d)?$)|(^\\d\\d(\\.\\d)?$)").matcher(top_volume);
	        if(!match_new_top_volume.find()){
				new_top_volume.setError("Объем верха: данные введены некорректно!");
				return;
			}

	        Matcher match_new_ankle_volume  = Pattern.compile("(^\\d\\d(\\.\\d)?$)|(^\\d\\d(\\.\\d)?$)").matcher(ankle_volume);
	        if(!match_new_ankle_volume.find()){
				new_ankle_volume.setError("Объем лодыжки: данные введены некорректно!");
				return;
			}
	        
	        Matcher match_new_kv_volume 	= Pattern.compile("(^\\d\\d(\\.\\d)?$)|(^\\d\\d(\\.\\d)?$)").matcher(kv_volume);
	        if(!match_new_kv_volume.find()){
				new_kv_volume.setError("Объем КВ: данные введены некорректно!");
				return;
			}

		  if(size.length() > 2){
		     	String[] arr_size = size.split(" ");
		     	size_left  = arr_size[0];
		     	size_right = arr_size[1];
		  }else{
		     	size_left = size_right = size;
		  }
		
		  // УРК //
		  if(urk.length() > 3){
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
							 customersn,
							 customerfn,
							 customerp,
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
			new_customerSN.setText("");
			new_customerFN.setText("");
			new_customerP.setText("");

			break;

		case R.id.get_model_image:
			// проверяем доступность SD
		    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		    	Toast.makeText(getApplicationContext(), "Ошибка! SD-карта не доступна: " + Environment.getExternalStorageState(), Toast.LENGTH_LONG).show();
		    	return;
		    }
		    // получаем путь к SD
		    File sdPath = Environment.getExternalStorageDirectory();
		    // добавляем свой каталог к пути
		    sdPath = new File(sdPath.getAbsolutePath() + "/OrthopedicGallery");
		    // создаем каталог
		    if (!sdPath.exists()) {
		    	sdPath.mkdirs();
		    }
		    // формируем объект File, который содержит путь к файлу
		    String timeStamp = String.valueOf(System.currentTimeMillis());
			String FILENAME_SD = "ORTHOIMG_" + timeStamp+".jpg";
		    File sdFile = new File(sdPath, FILENAME_SD);
		    
			// проверяем, была ли уже сделана фотография, если да, то удаляем предыдущую фотографию
		    if(!model_img_src.equals("")){
		    	File old_img = new File(model_img_src);
		    	old_img.delete();
		    }
		    	
		    // сохраняем путь к фотографии
		    model_img_src = sdFile.toString();
		    
			Intent intentGetFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intentGetFoto.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(sdFile));
			startActivityForResult(intentGetFoto, REQUEST_ADD_FOTO);
			break;
		default:
			break;
		}

	}
	
}
