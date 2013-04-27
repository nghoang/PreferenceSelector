package com.victor.algorithm;

import java.util.Vector;

import com.victor.database.DatabaseLayer;
import com.victor.preferenceselector.App;

public class AlgorithmBisection {
	DatabaseLayer db;

	public AlgorithmBisection(DatabaseLayer _db) {
		db = _db;
	}

	public void Process() {
		Vector<Integer> products = db.RunAlgorithmBisection(App.number_of_returned_results,App.drop_rate);
		System.out.println(products.toString());
	}
}
