package com.example.orthopedicdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class FragmentBackupAndRestore extends Fragment {

	View view;
	final String LOG_TAG = "myLogs";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.backup_and_restore, null);
		getActivity().setTitle("Сохранение и восстановление");
		Button restore = (Button) view.findViewById(R.id.restore);
		restore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				restore();
			}
		});

		Button backup = (Button) view.findViewById(R.id.backup);
		backup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					return;
				}
				backup();
				Toast.makeText(getActivity(),"Резервная копия успешно создана!", Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}// ON CREATE

	public static void backup() {
		new Thread(new Runnable() {
			public void run() {
				final String inFileName = "/data/data/com.example.orthopedicdb/databases/SHOES";
				File dbFile = new File(inFileName);
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(dbFile);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

				String outFileName = Environment.getExternalStorageDirectory()
						+ "/SHOES_BACKUP";

				// Open the empty db as the output stream
				OutputStream output = null;
				try {
					output = new FileOutputStream(outFileName);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

				// Transfer bytes from the inputfile to the outputfile
				byte[] buffer = new byte[1024];
				int length;
				try {
					while ((length = fis.read(buffer)) > 0) {
						output.write(buffer, 0, length);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				// Close the streams
				try {
					output.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

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
