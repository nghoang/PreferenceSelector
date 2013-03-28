package com.ngochoang.algorithm;

import java.util.Vector;

import com.ngochoang.database.DatabaseLayer;

public class AlgorithmBisection {
	DatabaseLayer db;

	public AlgorithmBisection(DatabaseLayer _db) {
		db = _db;
	}

	public void Process() {
		Vector<Integer> products = db.RunAlgorithmBisection();
		System.out.println(products.toString());
	}
}
