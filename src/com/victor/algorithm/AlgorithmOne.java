package com.victor.algorithm;

import java.util.Vector;

import com.victor.database.DatabaseLayer;

public class AlgorithmOne {
	DatabaseLayer db;
	
	public AlgorithmOne(DatabaseLayer _db)
	{
		db = _db;
	}
	
	public void Process()
	{
		Vector<Integer> attributes = db.RunAlgorithmOne();
		System.out.println(attributes.toString());
	}
}
