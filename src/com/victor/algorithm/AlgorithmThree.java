package com.victor.algorithm;

import java.util.Vector;

import com.victor.database.DatabaseLayer;
import com.victor.preferenceselector.App;

public class AlgorithmThree {
	DatabaseLayer db;
	
	//running queries and joining "WHERE" with previous queries
	public AlgorithmThree(DatabaseLayer _db)
	{
		db = _db;
	}
	
	public void Process()
	{
		Vector<Integer> products = db.RunAlgorithmThree(App.number_of_returned_results,App.drop_rate);
		System.out.println(products.toString());
	}
}
