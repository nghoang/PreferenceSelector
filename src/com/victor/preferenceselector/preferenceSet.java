package com.victor.preferenceselector;

import java.util.Vector;

import com.victor.database.DatabaseLayer;


//this class contains functions to generate and manage the reference rules
public class preferenceSet {
	public Vector<Vector<String>> preferenceAttributeSets;
	public Vector<Vector<Vector<Integer>>> preferenceValueSets;
	public DatabaseLayer db;
	public static int numAttr = 0;

	public void GeneratePreferenceSet() {
		numAttr = UsefulFunctions.RandomInRange(App.min_attr_in_rule,App.max_attr_in_rule);
		preferenceAttributeSets = new Vector<Vector<String>>();
		Vector<String> refSet = new Vector<String>();
		Vector<Integer> selected = new Vector<Integer>();// selected attribute
															// to avoid
															// duplicated
															// selection
		for (int i = 0; i < numAttr; i++) {
			int att = 1;
			do {
				att = UsefulFunctions.RandomInRange(1,App.number_of_attributes);
			} while (selected.contains(att) == true);
			selected.add(att);

			int newGroup = 1;
			if (UsefulFunctions.RandomInRange(1,100) <= App.attribute_grouping_rate)
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
		
		for (int i = 0; i < numAttr; i++) {//loop number of generating rule
			//store the generated values
			Vector<Vector<Integer>> preferenceValues = new Vector<Vector<Integer>>();
			//temporary variable to store buffer generated values
			Vector<Integer> values = new Vector<Integer>();
			//creating random number of values for one attribute
			int numValues = App.min_values_in_rule + (int)(Math.random() * ((App.max_values_in_rule - App.min_values_in_rule) + 1));
			//var to store the generated value to avoid duplicated generated values
			Vector<Integer> selectedV = new Vector<Integer>();
			for (int j = 0; j < numValues; j++) {
				int newGroup = 1;
				if (UsefulFunctions.RandomInRange(1,100) <= App.attribute_grouping_rate)
					newGroup = 0;
				
				//randomly generate one value
				int v = 0;
				do {
					v = UsefulFunctions.RandomInRange(App.min_customer_value_on_attribute,App.max_customer_value_on_attribute);
				} while (selectedV.contains(v) == true);
				selectedV.add(v);
				
				//decide if group them into a group or not
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
