package com.ngochoang.referenceselector;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import com.ngochoang.database.DatabaseLayer;

public class ReferenceSet {
	public Vector<Vector<String>> referenceAttributeSets;
	public Vector<Vector<Vector<Integer>>> referenceValueSets;
	public DatabaseLayer db;
	Random ran = new Random();
	public static int numAttr = 0;

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

		referenceValueSets = new Vector<Vector<Vector<Integer>>>();
		
		for (int i = 0; i < numAttr; i++) {
			Vector<Vector<Integer>> referenceValues = new Vector<Vector<Integer>>();
			Vector<Integer> values = new Vector<Integer>();
			int numValues = ran.nextInt(numAttr) + 1;
			Vector<Integer> selectedV = new Vector<Integer>();
			for (int j = 0; j < numValues; j++) {
				int newGroup = ran.nextInt(4);
				int v = 0;
				do {
					v = ran.nextInt(App.max_value_on_attribute
							- App.min_value_on_attribute)
							+ App.min_value_on_attribute;
				} while (selectedV.contains(v) == true);
				selectedV.add(v);
				
				if (newGroup == 0 || j == numValues-1)
				{
					values.add(v);
					if (j == numValues - 1)
						referenceValues.add(values);
				} 
				else // create new set
				{
					if (values.size() == 0) {
						values = new Vector<Integer>();
						values.add(v);
					} else {
						referenceValues.add(values);
						values = new Vector<Integer>();
						values.add(v);
					}
				}
			}
			referenceValueSets.add(referenceValues);
		}

		db.InsertReference(referenceAttributeSets, referenceValueSets);
	}
}
