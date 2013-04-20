package com.ngochoang.referenceselector;

import java.util.Vector;

import com.ngochoang.algorithm.AlgorithmBisection;
import com.ngochoang.algorithm.AlgorithmOne;
import com.ngochoang.algorithm.AlgorithmThree;
import com.ngochoang.algorithm.AlgorithmTwo;
import com.ngochoang.algorithm.GradualPartialRelease;
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
		if (App.step.equals("create_data_set")) {
			db.GenerateDatabase();
		} 
		else if (App.step.equals("create_sample_dataset"))
		{
			db.CreateSampleDB(App.sample_data_size,App.sample_data_size_step);
		}
		else if (App.step.equals("generate_references")) {
			for (int i = 0; i < App.number_of_rules; i++) {
				ReferenceSet r = new ReferenceSet();
				r.db = db;
				r.GenerateReferenceSet();
			}
		} 
		else if (App.step.equals("generate_queries")) {
			db.GenerateSelectionQueries();
		}else if (App.step.equals("run_algorithm")) {
			if (App.algorithm.equals("gradualpartialrelease")) {
				GradualPartialRelease al = new GradualPartialRelease(db);
				al.Process();
			} else if (App.algorithm.equals("two")) {
				AlgorithmTwo al = new AlgorithmTwo(db);
				al.Process();
			} else if (App.algorithm.equals("one")) {
				AlgorithmOne al = new AlgorithmOne(db);
				al.Process();
			} else if (App.algorithm.equals("bisec")) {
				AlgorithmBisection al = new AlgorithmBisection(db);
				al.Process();
			} else if (App.algorithm.equals("three")) {
				AlgorithmThree al = new AlgorithmThree(db);
				al.Process();
			}
		}
	}

}
