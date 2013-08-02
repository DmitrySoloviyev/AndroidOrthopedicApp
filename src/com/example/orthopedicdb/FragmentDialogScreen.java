package com.example.orthopedicdb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class FragmentDialogScreen extends DialogFragment implements OnClickListener{
	
	DB db;
	public static final int DIALOG_MATERIALS = 1;
	public static final int DIALOG_EMPLOYEES = 2;
	public static final int DIALOG_PROGRESS  = 3;
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
	
				break;
			case DIALOG_PROGRESS:
				   progressDialog = new ProgressDialog(getActivity());
			       progressDialog.setMessage("Пожалуйста, подождите...");
			       progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				break;
		}
		if(WHICHDIALOG == 3)
			return progressDialog;
		else{
			adb.setPositiveButton("OK", this);
	    	return adb.create();
	    }
	}
	  public void onClick(DialogInterface dialog, int which) {}
}

