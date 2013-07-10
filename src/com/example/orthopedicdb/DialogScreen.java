package com.example.orthopedicdb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

public class DialogScreen{
	
	public static final int NODB   		= 1;
	public static final int NEWDB  		= 2;
	public static final int CREATED 	= 3;
	public static final int DONTCREATED = 4;
	public static final int WHATSNEW 	= 5;
	public static final int NEWEMPLOYEE = 6;
	
    public static AlertDialog getDialog(final Activity activity, int ID) {
        	
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            switch(ID) {
            
	            case NODB:
	                builder.setTitle("���������� ������!");
	                builder.setMessage("���� ������ �� ����������. ������ ������� ������?");
	                builder.setCancelable(false);
	                builder.setPositiveButton("��", new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.dismiss();		
	                    }
	                });
	                
	                builder.setNegativeButton("���", new DialogInterface.OnClickListener() { 
	                	// ������ NO
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        activity.finish();
	                    }
	                });
	                return builder.create();
	                
	            case NEWDB: // progress dialog
	            	ProgressDialog pd = new ProgressDialog(activity);
	            	pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	                pd.setTitle("Загрузка базы данных");
	                pd.setMessage("Пожалуйста, подождите...");
	                pd.setCancelable(false);
	                return pd;
	
	            case CREATED: // alert dialog
	                builder.setTitle("���������");
	                builder.setMessage("���� ������ ������� �������!");
	                builder.setCancelable(false);
	                builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.dismiss();
	                    }
	                });
	                return builder.create();
	                
	            case DONTCREATED: // alert dialog
	                builder.setTitle("���������");
	                builder.setMessage("������ ��� �������� ���� ������.");
	                builder.setCancelable(false);
	                builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.dismiss();
	                    }
	                });
	                return builder.create();
	                
	            case NEWEMPLOYEE: // alert dialog
	                builder.setTitle("Новый модельер");
	                builder.setCancelable(true);
	                builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.dismiss();
	                    }
	                });
	                return builder.create();
	                
	            case WHATSNEW: // alert dialog
	                builder.setTitle("Что нового");
	                builder.setMessage("Версия 0.0.4 Краткий просмотр заказов (beta), удаление заказа и детальный просмотр заказа\n\n" +
	                		"0.0.3 Добавление нового заказа (beta)");
	                builder.setCancelable(false);
	                builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.dismiss();
	                    }
	                });
	                return builder.create();
	            
	            default:
	                return null;
            }
        }

}