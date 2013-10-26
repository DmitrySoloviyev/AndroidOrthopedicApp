package com.example.orthopedicdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
	DialogFragment alertDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.backup_and_restore, null);
		getActivity().setTitle("Сохранение и восстановление");
		Button restore = (Button) view.findViewById(R.id.restore);
		restore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				alertDialog = FragmentDialogScreen.newInstance(FragmentDialogScreen.DIALOG_ALERT);
				alertDialog.show(getFragmentManager(), "adlg");
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

				String outFileName = Environment.getExternalStorageDirectory() + "/SHOES_BACKUP";

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
}
