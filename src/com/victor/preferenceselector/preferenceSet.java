package com.victor.preferenceselector;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import com.victor.database.DatabaseLayer;

public class preferenceSet {
	public Vector<Vector<String>> preferenceAttributeSets;
	public Vector<Vector<Vector<Integer>>> preferenceValueSets;
	public DatabaseLayer db;
	Random ran = new Random();
	public static int numAttr = 0;

	public void GeneratePreferenceSet() {
		numAttr = ran.nextInt(App.max_attr_in_rule - App.min_attr_in_rule + 1)
				+ App.min_attr_in_rule;
		preferenceAttributeSets = new Vector<Vector<String>>();
		Vector<String> refSet = new Vector<String>();
		Vector<Integer> selected = new Vector<Integer>();// selected attribute
															// to avoid
															// duplicated
															// selection
		for (int i = 0; i < numAttr; i++) {
			int att = 1;
			do {
				att = ran.nextInt(App.number_of_attributes)+1;
			} while (selected.contains(att) == true);
			selected.add(att);

			int newGroup = 1;
			if (ran.nextInt(100)+1 <= App.attribute_grouping_rate)
				newGroup = 0;
			
			if (newGroup == 0)// add to the current set
			{
				refSet.add(Integer.toString(att));
				if (i == numAttr - 1)
					preferenceAttributeSets.add(refSet);
			} 
			else // create new set
			{
				if (refSet.size() == 0) {
					refSet = new Vector<String>();
					refSet.add(Integer.toString(att));
					if (i == numAttr-1)
						preferenceAttributeSets.add(refSet);
				} else {
					preferenceAttributeSets.add(refSet);
					refSet = new Vector<String>();
					refSet.add(String.valueOf(att));
					if (i == numAttr-1)
						preferenceAttributeSets.add(refSet);
				}
			}
		}

		preferenceValueSets = new Vector<Vector<Vector<Integer>>>();
		
		for (int i = 0; i < numAttr; i++) {
			Vector<Vector<Integer>> preferenceValues = new Vector<Vector<Integer>>();
			Vector<Integer> values = new Vector<Integer>();
			int numValues = ran.nextInt(numAttr) + 1;
			Vector<Integer> selectedV = new Vector<Integer>();
			for (int j = 0; j < numValues; j++) {
				int newGroup = 1;
				if (ran.nextInt(100)+1 <= App.attribute_grouping_rate)
					newGroup = 0;
				int v = 0;
				do {
					v = ran.nextInt(App.max_customer_value_on_attribute
							- App.min_customer_value_on_attribute)
							+ App.min_customer_value_on_attribute;
				} while (selectedV.contains(v) == true);
				selectedV.add(v);
				
				if (newGroup == 0)
				{
					values.add(v);
					if (j == numValues - 1)
						preferenceValues.add(values);
				} 
				else // create new set
				{
					if (values.size() == 0) {
						values = new Vector<Integer>();
						values.add(v);
						if (j == numValues - 1)
							preferenceValues.add(values);
					} else {
						preferenceValues.add(values);
						values = new Vector<Integer>();
						values.add(v);
						if (j == numValues - 1)
							preferenceValues.add(values);
					}
				}
			}
			preferenceValueSets.add(preferenceValues);
		}

		db.InsertPreference(preferenceAttributeSets, preferenceValueSets);
	}
}
