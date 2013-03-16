package com.ngochoang.referenceselector;

import java.util.Vector;

import com.ngochoang.database.DatabaseLayer;

public class ReferenceSelector {

	DatabaseLayer db = new DatabaseLayer();
	Vector<ReferenceSet> rules = new Vector<ReferenceSet>();

	public static void main(String[] args) {
		App.Init();
		ReferenceSelector ref = new ReferenceSelector();
		ref.run();
	}

	public void run() {
		if (db.Connect() == false)
			System.exit(-1);
		db.GenerateDatabase();
		for (int i = 0; i < App.number_of_rules; i++) {
			ReferenceSet r = new ReferenceSet();
			r.db = db;
			r.GenerateReferenceSet();
		}
	}

}
