package com.example.orthopedicdb;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class FragmentPreferencesActivity extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.preferences);
		super.onCreate(savedInstanceState);
		getActivity().setTitle("Настройки");
	}
}
