package com.victor.algorithm;

import java.util.Vector;

import com.victor.database.DatabaseLayer;
import com.victor.preferenceselector.App;

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
