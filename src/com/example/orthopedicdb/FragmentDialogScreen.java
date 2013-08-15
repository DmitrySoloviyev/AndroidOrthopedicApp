package com.example.orthopedicdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class FragmentDialogScreen extends DialogFragment implements OnClickListener{
	
	DB db;
	public static final int DIALOG_MATERIALS = 1;
	public static final int DIALOG_EMPLOYEES = 2;
	public static final int DIALOG_PROGRESS  = 3;
	public static final int DIALOG_ALERT     = 4;
	int WHICHDIALOG;
	
	public static FragmentDialogScreen newInstance(int dialogID) {
		FragmentDialogScreen fragmentDialod = new FragmentDialogScreen();
        Bundle arguments = new Bundle();
        arguments.putInt("WHICHDIALOG", dialogID);
        fragmentDialod.setArguments(arguments);
        return fragmentDialod;
    }
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder adb = null;
		ProgressDialog progressDialog = null;
		WHICHDIALOG = getArguments().getInt("WHICHDIALOG");
		db = new DB(getActivity());
		db.open();
		
		switch (WHICHDIALOG) {
			case DIALOG_MATERIALS:
				adb = new AlertDialog.Builder(getActivity());
				adb.setTitle("Выберите материал");
				adb.setMultiChoiceItems(FragmentExtenedSearchActivity.materialsCursor, "MaterialChecked","MaterialValue",
						new DialogInterface.OnMultiChoiceClickListener() {
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								db.changeMaterialFlag(which, isChecked);
								FragmentExtenedSearchActivity.materialsCursor.requery();
							}
						});
				adb.setPositiveButton("OK", this);
				break;
	
			case DIALOG_EMPLOYEES:
				adb = new AlertDialog.Builder(getActivity());
				adb.setTitle("Выберите модельера");
				adb.setMultiChoiceItems(FragmentExtenedSearchActivity.employeeCursor, "EmployeeChecked", "Employee",
						new DialogInterface.OnMultiChoiceClickListener() {
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								db.changeEmployeeFlag(which, isChecked);
								FragmentExtenedSearchActivity.employeeCursor.requery();
							}
						});
				adb.setPositiveButton("OK", this);
				break;
			case DIALOG_PROGRESS:
				   progressDialog = new ProgressDialog(getActivity());
			       progressDialog.setMessage("Пожалуйста, подождите...");
			       progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				break;
			case DIALOG_ALERT:
				adb = new AlertDialog.Builder(getActivity());
				adb.setTitle("Внимание!");
				adb.setMessage("Текущая база данных будет уничтожена. Продолжить?");
				adb.setNegativeButton("НЕТ", this);
				adb.setPositiveButton("ДА", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						restore();
					}
				});
				break;
		}
		if(WHICHDIALOG == 3)
			return progressDialog;
		else{
	    	return adb.create();
	    }
	}
	  public void onClick(DialogInterface dialog, int which) {}
	  
	  
	  public void restore() {
			File file = new File(Environment.getExternalStorageDirectory() + "/SHOES_BACKUP");
			if (file.exists()) {
				OutputStream myOutput;
				try {
					myOutput = new FileOutputStream("/data/data/com.example.orthopedicdb/databases/SHOES");
					InputStream myInputs = new FileInputStream(Environment.getExternalStorageDirectory() + "/SHOES_BACKUP");
					byte[] buffer = new byte[1024];
					int length;
					while ((length = myInputs.read(buffer)) > 0) {
						myOutput.write(buffer, 0, length);
					}
					myOutput.flush();
					myOutput.close();
					myInputs.close();
					Toast.makeText(getActivity(),"База данных успешно восстановлена!",Toast.LENGTH_SHORT).show();
				} catch (FileNotFoundException e) {
					Toast.makeText(getActivity(),"Ошибка, файл резервной копии должен быть на SD-карте!",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(getActivity(), "Ошибка восстановления!",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}else{
				Toast.makeText(getActivity(),"Ошибка, файл резервной копии должен быть на SD-карте!",Toast.LENGTH_SHORT).show();
			}
		}
}