package com.ngochoang.algorithm;

import java.util.Vector;

import com.ngochoang.database.DatabaseLayer;
import com.ngochoang.referenceselector.App;

public class AlgorithmTwo {
	DatabaseLayer db;
	
	public AlgorithmTwo(DatabaseLayer _db)
	{
		db = _db;
	}
	
	public void Process()
	{
		Vector<Integer> products = db.RunAlgorithmTwo(App.number_of_returned_results);
		System.out.println(products.toString());
	}
}
