package com.ngochoang.referenceselector;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import com.ngochoang.database.DatabaseLayer;

public class ReferenceSet {
	Vector<Vector<String>> referenceAttributeSets;
	Vector<Vector<Integer>> referenceValues;
	public DatabaseLayer db;
	Random ran = new Random();
	static int numAttr = 0;

	public void GenerateReferenceSet() {
		if (numAttr == 0)
			numAttr = ran.nextInt(App.max_attr_in_rule - App.min_attr_in_rule)
					+ App.min_attr_in_rule;
		referenceAttributeSets = new Vector<Vector<String>>();
		Vector<String> refSet = new Vector<String>();
		Vector<Integer> selected = new Vector<Integer>();// selected attribute
															// to avoid
															// duplicated
															// selection
		for (int i = 0; i < numAttr; i++) {
			int att = 0;
			do {
				att = ran.nextInt(App.number_of_attributes);
			} while (selected.contains(att) == true);
			selected.add(att);
			int newGroup = ran.nextInt(4);
			
			if (newGroup == 0 || i == numAttr-1)// add to the current set
			{
				refSet.add(Integer.toString(att));
				if (i == numAttr - 1)
					referenceAttributeSets.add(refSet);
			} 
			else // create new set
			{
				if (refSet.size() == 0) {
					refSet = new Vector<String>();
					refSet.add(Integer.toString(att));
				} else {
					referenceAttributeSets.add(refSet);
					refSet = new Vector<String>();
					refSet.add(String.valueOf(att));
				}
			}
		}

		referenceValues = new Vector<Vector<Integer>>();
		for (int i = 0; i < numAttr; i++) {
			Vector<Integer> values = new Vector<Integer>();
			for (int j = 0; j < numAttr; j++) {
				values.add(ran.nextInt(App.max_value_on_attribute
						- App.min_value_on_attribute)
						+ App.min_value_on_attribute);
			}
			referenceValues.add(values);
		}

		db.InsertReference(numAttr, referenceAttributeSets, referenceValues);
	}
}
