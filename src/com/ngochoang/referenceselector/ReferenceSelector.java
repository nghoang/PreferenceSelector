package com.ngochoang.referenceselector;

import com.ngochoang.database.DatabaseLayer;

public class ReferenceSelector {

	DatabaseLayer db = new DatabaseLayer();
	
	public static void main(String[] args) {
		App.Init();
		ReferenceSelector ref = new ReferenceSelector();
		ref.run();
	}
	
	public void run()
	{
		if (db.Connect() == false)
			System.exit(-1);
		db.GenerateDatabase();
	}

}
