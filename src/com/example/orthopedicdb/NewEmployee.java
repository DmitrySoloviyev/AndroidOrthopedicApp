package com.example.orthopedicdb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewEmployee extends Activity implements OnClickListener{
	
	EditText SURNAME;// фамилия
	EditText FIRSTNAME;//имя
	EditText PATRONYMIC;//отчество
	Button create_new_employee;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_employee_dialog);

		SURNAME 	= (EditText) findViewById(R.id.new_employeeSN);
		FIRSTNAME 	= (EditText) findViewById(R.id.new_employeeFN);
		PATRONYMIC 	= (EditText) findViewById(R.id.new_employeeP);
	    create_new_employee = (Button) findViewById(R.id.create_new_employee);
	    create_new_employee.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		String sn = SURNAME.getText().toString().trim();
		String fn = FIRSTNAME.getText().toString().trim();
		String p  = PATRONYMIC.getText().toString().trim();
		
		if(sn.isEmpty() | fn.isEmpty() | p.isEmpty()){
			setResult(RESULT_CANCELED);
		}else{
			intent.putExtra("surname", sn);
		    intent.putExtra("firstname", fn);
		    intent.putExtra("patronymic", p);
		    setResult(RESULT_OK, intent);
		}
	    finish();
	}
}
