package com.example.orthopedicdb;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class MyService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new StackViewFactoryAdapter(getApplicationContext(), intent);
	}

}
