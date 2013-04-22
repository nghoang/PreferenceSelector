package com.victor.preferenceselector;

import java.util.Vector;

import com.victor.algorithm.AlgorithmBisection;
import com.victor.algorithm.AlgorithmOne;
import com.victor.algorithm.AlgorithmThree;
import com.victor.algorithm.AlgorithmTwo;
import com.victor.database.DatabaseLayer;

public class preferenceSelector {

	DatabaseLayer db = new DatabaseLayer();
	Vector<preferenceSet> rules = new Vector<preferenceSet>();

	public static void main(String[] args) {
		App.Init();
		preferenceSelector ref = new preferenceSelector();
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
		else if (App.step.equals("generate_preferences")) {
			for (int i = 0; i < App.number_of_rules; i++) {
				preferenceSet r = new preferenceSet();
				r.db = db;
				r.GeneratePreferenceSet();
			}
		} 
		else if (App.step.equals("generate_queries")) {
			db.GenerateSelectionQueries();
		}else if (App.step.equals("run_algorithm")) {
			if (App.algorithm.equals("two")) {
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
