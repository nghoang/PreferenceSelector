package com.victor.algorithm;

import java.util.Vector;

import com.victor.database.DatabaseLayer;
import com.victor.preferenceselector.App;

public class AlgorithmOne {
	DatabaseLayer db;
	
	//combine all queries by OR and run to get result at once
	public AlgorithmOne(DatabaseLayer _db)
	{
		db = _db;
	}
	
	public void Process()
	{
		Vector<Integer> attributes = db.RunAlgorithmOne(App.number_of_returned_results,App.drop_rate);
		System.out.println(attributes.toString());
		if (db.overflow)
			System.out.println("Alert Overflow");
	}
}
