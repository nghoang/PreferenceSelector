package com.ngochoang.algorithm;

import java.util.Vector;

import com.ngochoang.database.DatabaseLayer;
import com.ngochoang.referenceselector.App;

public class AlgorithmThree {
	DatabaseLayer db;
	
	public AlgorithmThree(DatabaseLayer _db)
	{
		db = _db;
	}
	
	public void Process()
	{
		Vector<Integer> products = db.RunAlgorithmThree(App.number_of_returned_results);
		System.out.println(products.toString());
	}
}
