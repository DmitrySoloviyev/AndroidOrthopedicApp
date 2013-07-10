package com.example.orthopedicdb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewMaterial extends Activity implements OnClickListener{
	
	EditText editMATERIAL;
	Button  submit;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_material);
		editMATERIAL = (EditText)findViewById(R.id.new_added_material);
		submit = (Button)findViewById(R.id.create_new_material);
		submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent();
		String material = editMATERIAL.getText().toString().trim();
		if(material.isEmpty()){
	    	setResult(RESULT_CANCELED);
		}else{	
			intent.putExtra("material", material);
	    	setResult(RESULT_OK, intent);
		}
	    finish();
	}
}
